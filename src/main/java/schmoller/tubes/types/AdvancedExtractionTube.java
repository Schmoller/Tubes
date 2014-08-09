package schmoller.tubes.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import schmoller.tubes.AnyFilter;
import schmoller.tubes.ModTubes;
import schmoller.tubes.RedstoneMode;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.IPropertyHolder;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.routing.GoalRouter;

public class AdvancedExtractionTube extends DirectionalTube implements ITubeOverflowDestination, IPropertyHolder, IRedstonePart
{
	public enum PullMode
	{
		NormalAllow,
		NormalDeny,
		Random,
		Sequence,
		Overflow
	}
	
	public static final int PROP_PULLMODE = 1;
	public static final int PROP_REDSTONEMODE = 2;
	public static final int PROP_SIZEMODE = 3;
	public static final int PROP_COLOR = 4;
	
	public static final int CHANNEL_PULSE = 1;
	public static final int CHANNEL_POWERED = 2;
	
	private static Random mRand = new Random();
	private OverflowBuffer mOverflow;
	private PullMode mMode = PullMode.NormalAllow;
	private RedstoneMode mRSMode = RedstoneMode.High;
	private SizeMode mSize = SizeMode.Max;
	private IFilter[] mFilters;
	private int mColor = -1;
	
	private int mPulses = 0;
	private boolean mIsPowered;
	
	private int mNext;
	
	public float animTime = 0;
	
	public AdvancedExtractionTube()
	{
		super("advancedExtraction");
		mFilters = new IFilter[24];
		mOverflow = new OverflowBuffer();
	}
	
	@Override
	public <T> T getProperty( int prop )
	{
		switch(prop)
		{
		case PROP_PULLMODE:
			return (T)mMode;
		case PROP_REDSTONEMODE:
			return (T)mRSMode;
		case PROP_SIZEMODE:
			return (T)mSize;
		case PROP_COLOR:
			return (T)Integer.valueOf(mColor);
		}
		
		return null;
	}
	
	@Override
	public <T> void setProperty( int prop, T value )
	{
		switch(prop)
		{
		case PROP_PULLMODE:
			mMode = (PullMode)value;
			break;
		case PROP_REDSTONEMODE:
			mRSMode = (RedstoneMode)value;
			break;
		case PROP_SIZEMODE:
			mSize = (SizeMode)value;
			break;
		case PROP_COLOR:
			mColor = ((Number)value).intValue();
			break;
		}
		
		tile().markDirty();
	}
	
	@Override
	public Cuboid6 getBounds()
	{
		switch(getFacing())
		{
		default:
		case 0:
			return new Cuboid6(0.1875f, 0.0f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		case 1:
			return new Cuboid6(0.1875f, 0.1875f, 0.1875f, 0.8125f, 1.0f, 0.8125f);
		case 2:
			return new Cuboid6(0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.8125f);
		case 3:
			return new Cuboid6(0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 1.0f);
		case 4:
			return new Cuboid6(0.0f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		case 5:
			return new Cuboid6(0.1875f, 0.1875f, 0.1875f, 1.0f, 0.8125f, 0.8125f);
		}
	}
	
	@Override
	protected int getConnectableSides()
	{
		return (1 << (getFacing() ^ 1));
	}
	
	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return (side != (getFacing() ^ 1));
	}
	
	@Override
	public OverflowBuffer getOverflowContents()
	{
		return mOverflow;
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		mOverflow.onDropItems(itemsToDrop);
	}
		
	public IFilter getFilter(int index)
	{
		return mFilters[index];
	}
	
	public void setFilter(int index, IFilter filter)
	{
		mFilters[index] = filter;
		tile().markDirty();
	}
	
	public PullMode getMode()
	{
		return mMode;
	}
	
	public int getNext()
	{
		return mNext;
	}
	
	public void setNext(int next)
	{
		mNext = next;
	}
	
	private ArrayList<IFilter> getAllFilters()
	{
		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		for(int i = 0; i < mFilters.length; ++i)
		{
			IFilter filter = mFilters[i];
			if(filter != null)
				filters.add(filter);
		}
		
		return filters;
	}
	
	private int getMaxLevel(Payload payload)
	{
		int count = 0;
		for(int i = 0; i < mFilters.length; ++i)
		{
			IFilter filter = mFilters[i];
			if(filter != null)
			{
				if(filter.matches(payload, SizeMode.Max))
					count += filter.size();
			}
		}
		return count;
	}
	
	private IFilter getOverflowFilter()
	{
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		IPayloadHandler handler = InteractionHandler.getHandler(null, world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		List<Payload> contents = CommonHelper.distinctPayloads(handler.listContents(getFacing() ^ 1));
		
		for(Payload payload : contents)
		{
			// Remove any that dont match any filter
			IFilter match = null;
			for(IFilter filter : getAllFilters())
			{
				if(filter.matches(payload, SizeMode.Max))
				{
					match = filter;
					break;
				}
			}
			
			if(match == null)
				continue;
			
			int max = getMaxLevel(payload);
			if(payload.size() > max)
			{
				Payload excess = payload.copy();
				excess.setSize(payload.size() - max);
				if(excess.size() > excess.maxSize())
					excess.setSize(excess.maxSize());
				
				return FilterRegistry.getInstance().createFilter(excess);
			}
		}
		return null;
	}
	
	private IFilter getNormalFilter()
	{
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		IPayloadHandler handler = InteractionHandler.getHandler(null, world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		List<Payload> contents = CommonHelper.distinctPayloads(handler.listContents(getFacing() ^ 1));
		
		for(Payload payload : contents)
		{
			IFilter match = null;
			List<IFilter> filters = getAllFilters();
			if(filters.isEmpty())
				return new AnyFilter(0);
			
			for(IFilter filter : filters)
			{
				if(filter.matches(payload, SizeMode.Max))
					return filter;
			}
		}
		
		return null;
	}
	
	private IFilter getInvertFilter()
	{
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		IPayloadHandler handler = InteractionHandler.getHandler(null, world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		List<Payload> contents = CommonHelper.distinctPayloads(handler.listContents(getFacing() ^ 1));
		
		for(Payload payload : contents)
		{
			IFilter match = null;
			List<IFilter> filters = getAllFilters();
			if(filters.isEmpty())
				return new AnyFilter(0);
			
			boolean matched = false;
			for(IFilter filter : filters)
			{
				if(filter.matches(payload, SizeMode.Max))
				{
					matched = true;
					break;
				}
			}
			
			if(matched)
				continue;
			
			Payload copy = payload.copy();
			if(copy.size() > copy.maxSize())
				copy.setSize(copy.maxSize());
			return FilterRegistry.getInstance().createFilter(copy);
		}
		
		return null;
	}
	
	public IFilter getRandomFilter()
	{
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		IPayloadHandler handler = InteractionHandler.getHandler(null, world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		List<Payload> contents = CommonHelper.distinctPayloads(handler.listContents(getFacing() ^ 1));
		
		ArrayList<IFilter> filters = getAllFilters();
		if(filters.isEmpty())
			return new AnyFilter(0);
		
		while(filters.size() > 0)
		{
			int index = mRand.nextInt(filters.size());
			IFilter filter = filters.get(index);
			
			handler = InteractionHandler.getHandler(filter.getPayloadType(), world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
			if(handler.extract(filter, getFacing() ^ 1, filter.size(), mSize, false) != null)
				return filter;
			
			filters.remove(index);
		}
		
		return null;
	}
	
	@Override
	public int getTickRate()
	{
		return 10;
	}
	
	@Override
	public void onTick()
	{
		if(world().isRemote)
			return;
		
		if(!mOverflow.isEmpty())
		{
			TubeItem item = mOverflow.peekNext();
			PathLocation loc = new GoalRouter(world(), new Position(x(),y(),z()), item, TubesAPI.goalOutput).route();
			
			if(loc != null)
			{
				mOverflow.getNext();
				item.goal = TubesAPI.goalOutput;
				item.direction = item.lastDirection = getFacing() ^ 1;
				item.updated = false;
				item.setProgress(0);
				addItem(item, true);
			}
			
			return;
		}
		
		switch(mRSMode)
		{
		case High:
			if(!mIsPowered)
				return;
			break;
		case Low:
			if(mIsPowered)
				return;
			break;
		case Pulse:
			if(mPulses <= 0)
				return;
			break;
		default:
			break;
		}
		
		SizeMode size = mSize;
		IFilter filter = null;
		int next = mNext;
		switch(mMode)
		{
		case NormalAllow:
			filter = getNormalFilter();
			break;
		case NormalDeny:
			filter = getInvertFilter();
			size = SizeMode.Exact;
			break;
		case Overflow:
			filter = getOverflowFilter();
			size = SizeMode.Exact;
			break;
		case Random:
			filter = getRandomFilter();
			break;
		case Sequence:
		{
			int start = next;
			
			do
			{
				filter = mFilters[next++];
				if(next >= mFilters.length)
					next = 0;
			}
			while(filter == null && next != start);
			break;
		}
		}
		
		if(filter == null)
			return;
		
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		
		IPayloadHandler handler = InteractionHandler.getHandler(filter.getPayloadType(), world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		if(handler == null)
			return;
		
		Payload extracted = handler.extract(filter, getFacing() ^ 1, filter.size(), size, false);
		
		if(extracted != null)
		{
			TubeItem item = new TubeItem(extracted);
			item.colour = mColor;
			item.direction = dir.ordinal() ^ 1;
			
			if(new GoalRouter(world(), new Position(x(), y(), z()), item, TubesAPI.goalOutput).route() != null)
			{
				handler.extract(filter, getFacing() ^ 1, filter.size(), size, true);
				addItem(item, true);
				
				if(mMode == PullMode.Sequence)
					mNext = next;
				
				if(mRSMode == RedstoneMode.Pulse)
				{
					--mPulses;
					if(mPulses < 0)
						mPulses = 0;
					
					openChannel(CHANNEL_PULSE);
				}
			}
		}
	}
	
	private int getPower()
	{
		int current = 0;
		for(int side = 0; side < 6; ++side)
			current = Math.max(current, RedstoneInteractions.getPowerTo(world(), x(), y(), z(), side, 0x1f));
		
		return current;
	}
	
	@Override
	public void onWorldJoin()
	{
		mIsPowered = getPower() > 0;
	}
	
	@Override
	public void update()
	{
		if(!world().isRemote)
		{
			boolean state = getPower() > 0;
			
			if(state != mIsPowered)
				openChannel(CHANNEL_POWERED).writeBoolean(state);
			
			if(!mIsPowered && state && mRSMode == RedstoneMode.Pulse)
				++mPulses;
	
			mIsPowered = state;
		}
		else
		{
			switch(mRSMode)
			{
			case Ignore:
				animTime += 0.05f;
				
				if(animTime > 1)
					animTime -= 1;
				break;
			case High:
				if(mIsPowered)
				{
					animTime += 0.05f;
					
					if(animTime > 1)
						animTime -= 1;
				}
				else if(animTime > 0)
				{
					animTime += 0.05f;
					
					if(animTime > 1)
						animTime = 0;
				}
				break;
			case Low:
				if(!mIsPowered)
				{
					animTime += 0.05f;
					
					if(animTime > 1)
						animTime -= 1;
				}
				else if(animTime > 0)
				{
					animTime += 0.05f;
					
					if(animTime > 1)
						animTime = 0;
				}
				break;
			case Pulse:
				if(animTime > 0)
					animTime += 0.05f;
				
				if(animTime > 1)
					animTime = 0;
				break;
			}
		}
		
		super.update();
	}
	
	@Override
	protected void onRecieveDataClient( int channel, MCDataInput input )
	{
		switch(channel)
		{
		case CHANNEL_POWERED:
			mIsPowered = input.readBoolean();
			break;
		case CHANNEL_PULSE:
			animTime = 0.0001f;
			break;
		default:
			super.onRecieveDataClient(channel, input);
		}
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.goal == TubesAPI.goalOverflow)
		{
			item.lastDirection = item.direction; 
			item.direction = getFacing();
			item.updated = true;
			return true;
		}
		else
			return super.onItemJunction(item);
	}
	
	@Override
	protected boolean onItemLeave( TubeItem item )
	{
		if(item.goal == TubesAPI.goalOverflow && item.direction == getFacing())
		{
			if(!world().isRemote)
				mOverflow.addItem(item);
			
			return true;
		}

		return super.onItemLeave(item);
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(!super.activate(player, part, item))
			player.openGui(ModTubes.instance, ModTubes.GUI_ADV_EXTRACTION, world(), x(), y(), z());
		
		return true;
	}
	
	@Override
	public boolean canConnectRedstone( int side ) { return true; }

	@Override
	public int strongPowerLevel( int side ) { return 0; }

	@Override
	public int weakPowerLevel( int side ) { return 0; }
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		NBTTagList items = new NBTTagList();
		for(int i = 0; i < mFilters.length; ++i)
		{
			if(mFilters[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				FilterRegistry.getInstance().writeFilter(mFilters[i],tag);
				items.appendTag(tag);
			}
		}
		
		root.setTag("Filters", items);
		
		root.setInteger("Mode", mMode.ordinal());
		root.setInteger("RSMode", mRSMode.ordinal());
		root.setInteger("Size", mSize.ordinal());
		mOverflow.save(root);
		
		root.setShort("Color", (short)mColor);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		NBTTagList filters = root.getTagList("Filters", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < filters.tagCount(); ++i)
		{
			NBTTagCompound tag = filters.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");
			mFilters[slot] = FilterRegistry.getInstance().readFilter(tag);
		}
		
		mMode = PullMode.values()[root.getInteger("Mode")];
		mRSMode = RedstoneMode.values()[root.getInteger("RSMode")];
		mSize = SizeMode.values()[root.getInteger("Size")];
		
		mColor = root.getShort("Color");
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		super.writeDesc(output);
		
		output.writeByte(mMode.ordinal());
		output.writeByte(mRSMode.ordinal());
		output.writeByte(mSize.ordinal());
		output.writeShort(mColor);
		
		output.writeBoolean(mIsPowered);
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		super.readDesc(input);
		
		mMode = PullMode.values()[input.readByte()];
		mRSMode = RedstoneMode.values()[input.readByte()];
		mSize = SizeMode.values()[input.readByte()];
		mColor = input.readShort();
		
		mIsPowered = input.readBoolean();
	}

}
