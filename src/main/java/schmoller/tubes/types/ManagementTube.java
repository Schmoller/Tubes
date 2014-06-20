package schmoller.tubes.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.Constants;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IImportSource;
import schmoller.tubes.api.interfaces.IImportController;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.IPropertyHolder;
import schmoller.tubes.api.interfaces.ITubeImportDest;
import schmoller.tubes.routing.ManagementTubeFinder;

public class ManagementTube extends DirectionalTube implements ITubeImportDest, IImportSource, IImportController, IPropertyHolder
{
	public enum ManagementMode
	{
		Stock,
		Fill,
		PassiveStock,
		PassiveFill;
	}
	
	private static HashSet<ManagementTube>[] mAllTubes = new HashSet[17];
	
	static
	{
		for(int i = 0; i < 17; ++i)
			mAllTubes[i] = new HashSet<ManagementTube>();
	}
	
	protected static final int CHANNEL_COLOR = 1;
	protected static final int CHANNEL_PRIORITY = 2;
	protected static final int CHANNEL_MODE = 3;
	public static final int PROP_MODE = 1;
	public static final int PROP_COLOR = 2;
	public static final int PROP_PRIORITY = 3;
	
	private IFilter[] mFilters;
	private ManagementMode mMode;
	private int mColor;
	private int mPriority;
	
	public ManagementTube()
	{
		super("management");
		mFilters = new IFilter[48];
		mMode = ManagementMode.Stock;
		mColor = -1;
		mPriority = 0;
	}
	
	@Override
	public Cuboid6 getBounds()
	{
		switch(getFacing())
		{
		default:
		case 0:
			return new Cuboid6(0.125f, 0.0f, 0.125f, 0.875f, 0.8125f, 0.875f);
		case 1:
			return new Cuboid6(0.125f, 0.1875f, 0.125f, 0.875f, 1.0f, 0.875f);
		case 2:
			return new Cuboid6(0.125f, 0.125f, 0.0f, 0.875f, 0.875f, 0.8125f);
		case 3:
			return new Cuboid6(0.125f, 0.125f, 0.1875f, 0.875f, 0.875f, 1.0f);
		case 4:
			return new Cuboid6(0.0f, 0.125f, 0.125f, 0.8125f, 0.875f, 0.875f);
		case 5:
			return new Cuboid6(0.1875f, 0.125f, 0.125f, 1.0f, 0.875f, 0.875f);
		}
	}
	
	@Override
	public int getHollowSize( int side )
	{
		if(side == getFacing())
			return 12;
		
		return super.getHollowSize(side);
	}
	
	@Override
	protected int getConnectableSides()
	{
		return 1 << (getFacing() ^ 1); 
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return false;
	}
	
	public IFilter getFilter(int x, int y)
	{
		return mFilters[x + (y * 8)];
	}
	
	public void setFilter(int x, int y, IFilter filter)
	{
		mFilters[x + (y * 8)] = filter;
		tile().markDirty();
	}
	
	public ManagementMode getMode()
	{
		return mMode;
	}
	
	public int getColor()
	{
		return mColor;
	}

	
	public int getPriority()
	{
		return mPriority;
	}
	
	@Override
	public <T> T getProperty( int prop )
	{
		switch(prop)
		{
		case PROP_MODE:
			return (T)mMode;
		case PROP_COLOR:
			return (T)Integer.valueOf(mColor);
		case PROP_PRIORITY:
			return (T)Integer.valueOf(mPriority);
		}
		return null;
	}
	
	@Override
	public <T> void setProperty( int prop, T value )
	{
		switch(prop)
		{
		case PROP_MODE:
			mMode = (ManagementMode)value;
			break;
		case PROP_COLOR:
			mColor = ((Number)value).intValue();
			break;
		case PROP_PRIORITY:
			mPriority = ((Number)value).intValue();
			break;
		}
		
		tile().markDirty();
	}

	private IPayloadHandler<? extends Payload> getHandler(Class<? extends Payload> payloadClass)
	{
		Position pos = new Position(x(), y(), z()).offset(getFacing(), 1);
		return InteractionHandler.getHandler(payloadClass, world(), pos);
	}
	
	private boolean filtersMatch(Payload payload)
	{
		for(int i = 0; i < mFilters.length; ++i)
		{
			IFilter filter = mFilters[i];
			if(filter != null)
			{
				if(filter.matches(payload, SizeMode.Max))
					return true;
			}
		}
		
		return false;
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
	
	private int getCurrentLevel(Payload payload)
	{
		IPayloadHandler<? extends Payload> handler = getHandler(null);
		List<Payload> contents = CommonHelper.distinctPayloads(handler.listContents(getFacing() ^ 1));
		
		for(Payload item : contents)
		{
			if(item.isPayloadTypeEqual(payload))
				return item.size();
		}
		
		return 0;
	}
	
	private boolean extract(Payload payload)
	{
		int size = Math.min(payload.size(), payload.maxSize());
		
		IFilter filter = FilterRegistry.getInstance().createFilter(payload);
		if(filter == null)
			return false;
		
		IPayloadHandler<? extends Payload> handler = getHandler(payload.getClass());
		
		Payload extracted = handler.extract(filter, getFacing() ^ 1, size, SizeMode.Exact, true);
		
		if(extracted != null)
		{
			TubeItem item = new TubeItem(extracted);
			item.colour = mColor;
			item.direction = getFacing() ^ 1;
			
			addItem(item, true);
			return true;
		}
		
		return false;
	}
	
	private boolean expelExcess(List<Payload> contents)
	{
		for(Payload payload : contents)
		{
			// Remove any that dont match any filter
			if(!filtersMatch(payload))
			{
				if(extract(payload))
					return true;
			}
			// Remove excess for ones that do match as long as there is a size requirement
			else if(mMode == ManagementMode.Stock || mMode == ManagementMode.PassiveStock)
			{
				int max = getMaxLevel(payload);
				if(payload.size() > max)
				{
					Payload excess = payload.copy();
					excess.setSize(payload.size() - max);
					if(extract(excess))
						return true;
				}
			}
		}
		return false;
	}
	
	private boolean request(IFilter filter)
	{
		return TubeHelper.requestImport(world(), new Position(x(), y(), z()), getFacing() ^ 1, filter, SizeMode.LessEqual, getColor(), this) != null;
	}
	
	private boolean requestMore(List<Payload> contents)
	{
		// If the filter does not match any, then it will be requested 
		for(int i = 0; i < mFilters.length; ++i)
		{
			IFilter filter = mFilters[i];
			if(filter != null)
			{
				if(getHandler(filter.getPayloadType()) == null) // If there is no handler for it, there is no point going any further
					continue;
				
				boolean matched = false;
				for(Payload payload : contents)
				{
					if(filter.matches(payload, SizeMode.Max))
					{
						if(mMode == ManagementMode.Stock)
						{
							int max = getMaxLevel(payload);
							if(payload.size() < max)
							{
								int toRequest = max - payload.size();
								Payload req = payload.copy();
								req.setSize(Math.min(toRequest, req.maxSize()));
								
								// Ensure we could actually add it
								IPayloadHandler<Payload> handler = (IPayloadHandler<Payload>)getHandler(req.getClass());
								Payload leftover = handler.insert(req, getFacing() ^ 1, false);
								if(leftover != null)
								{
									if(leftover.isPayloadEqual(req)) // Nothing could be inserted
									{
										matched = true;
										break;
									}
									req.setSize(req.size() - leftover.size()); // dont request more than we could actually insert
								}
								
								if(request(FilterRegistry.getInstance().createFilter(req)))
									return true;
							}
						}
						else if(mMode == ManagementMode.Fill)
						{
							Payload req = payload.copy();
							req.setSize(req.maxSize());
							
							// Ensure we could actually add it
							IPayloadHandler<Payload> handler = (IPayloadHandler<Payload>)getHandler(req.getClass());
							Payload leftover = handler.insert(req, getFacing() ^ 1, false);
							if(leftover != null)
							{
								if(leftover.isPayloadEqual(req)) // Nothing could be inserted
								{
									matched = true;
									break;
								}
								req.setSize(req.size() - leftover.size()); // dont request more than we could actually insert
							}
							
							if(request(FilterRegistry.getInstance().createFilter(req)))
								return true;
						}
						
						matched = true;
						break;
					}
				}
				
				if(!matched)
				{
					if(request(filter))
						return true;
				}
				
			}
		}
		
		return false;
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
		
		IPayloadHandler<? extends Payload> handler = getHandler(null);
		List<Payload> contents = CommonHelper.distinctPayloads(handler.listContents(getFacing() ^ 1));
		
		if(!expelExcess(contents))
		{
			if(mMode == ManagementMode.Stock || mMode == ManagementMode.Fill)
			{
				requestMore(contents);
			}
		}
	}
	
	@Override
	public boolean canPathThrough()
	{
		return false;
	}
	
	private boolean isPrioritySatisfied(Payload payload)
	{
		List<PathLocation> tubeLocs = new ManagementTubeFinder(this).routeAll();
		ArrayList<ManagementTube> allAvailable = new ArrayList<ManagementTube>(tubeLocs.size());
		
		// Sort by priority
		for(PathLocation loc : tubeLocs)
		{
			ManagementTube other = (ManagementTube)TubeHelper.getTubeConnectable(world(), loc.position.x, loc.position.y, loc.position.z);
			
			int ind = Collections.binarySearch(allAvailable, other, PrioritySorter.instance);
			if(ind < 0)
				ind = (ind + 1) * -1;
			allAvailable.add(ind, other);
		}
		
		for(int i = allAvailable.size() - 1; i >= 0; --i)
		{
			ManagementTube other = allAvailable.get(i);
			if(!other.isSatisfied(payload))
				return false;
		}
		
		return true;
	}
	
	public boolean isSatisfied(Payload item)
	{
		if(!filtersMatch(item))
			return true;
		
		IPayloadHandler<Payload> specHandler = (IPayloadHandler<Payload>)getHandler(item.getClass());
		if(specHandler == null)
			return true;
		
		Payload leftover = specHandler.insert(item, getFacing() ^ 1, false);
		if(leftover != null)
		{
			if(leftover.isPayloadEqual(item))
				return true;
		}
		
		if(mMode == ManagementMode.Fill || mMode == ManagementMode.PassiveFill)
			return false;
		
		List<Payload> contents = CommonHelper.distinctPayloads(specHandler.listContents(getFacing() ^ 1));
		
		for(Payload payload : contents)
		{
			if(payload.isPayloadTypeEqual(item))
			{
				int max = getMaxLevel(item) - getCurrentLevel(item);
				
				return (max <= 0);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean canAddItem( Payload item, int direction )
	{
		if(world().isRemote)
			return true;

		if(!isPrioritySatisfied(item))
			return false;
		
		return !isSatisfied(item);
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		return (mColor == NO_COLOUR || item.colour == mColor || item.colour == NO_COLOUR) && super.canItemEnter(item);
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.direction != getFacing())
			return super.onItemJunction(item);
		
		if(!world().isRemote)
		{
			IPayloadHandler<Payload> handler = (IPayloadHandler<Payload>) getHandler(item.item.getClass());
			if(handler != null)
			{
				Payload remaining = null;
				if(mMode == ManagementMode.Fill || mMode == ManagementMode.PassiveFill)
					remaining = handler.insert(item.item, getFacing(), true);
				else
				{
					int max = getMaxLevel(item.item) - getCurrentLevel(item.item);
					if(max > 0)
					{
						int toAdd = Math.min(max, item.item.size());
						Payload add = item.item.copy();
						add.setSize(toAdd);
						
						remaining = handler.insert(add, getFacing() ^ 1, true);
						
						if(toAdd != item.item.size())
						{
							if(remaining == null)
							{
								remaining = item.item.copy();
								remaining.setSize(item.item.size() - toAdd);
							}
							else
								remaining.setSize(remaining.size() + (item.item.size() - toAdd));
						}
						
					}
					else
					{
						item.direction = item.direction ^ 1;
						item.state = TubeItem.BLOCKED;
						
						addToClient(item);
						return true;
					}
				}
				
				if(remaining != null)
				{
					item.item.setSize(remaining.size());
					item.direction = item.direction ^ 1;
					item.state = TubeItem.BLOCKED;
					
					addToClient(item);
					return true;
				}
			}
			else
			{
				item.direction = item.direction ^ 1;
				item.state = TubeItem.BLOCKED;
				
				addToClient(item);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(!super.activate(player, part, item))
			player.openGui(ModTubes.instance, ModTubes.GUI_MANAGEMENT_TUBE, world(), x(), y(), z());
		
		return true;
	}
	
	@Override
	public boolean canImportFromSide( int side )
	{
		return side == getFacing();
	}
	
	@Override
	public boolean isImportItemOk( Payload item )
	{
		// Check that it can fit
		IPayloadHandler<Payload> myHandler = (IPayloadHandler<Payload>)getHandler(item.getClass());
		if(myHandler == null)
			return false;
		
		Payload leftover = myHandler.insert(item, getFacing() ^ 1, false);
		if(leftover != null)
		{
			if(leftover.isPayloadEqual(item))
				return false;
		}
		
		return true;
	}
	
	@Override
	public boolean isImportSourceOk( Position position, int fromSide )
	{
		ManagementTube tube = CommonHelper.getInterface(world(), position, ManagementTube.class);
		
		if(tube != null)
			return ((tube.getColor() == -1 || mColor == -1 || mColor == tube.getColor()) && tube.getPriority() < mPriority);
		
		return true;
	}
	
	@Override
	public boolean isImportSourceOk( Position position )
	{
		// Only management tubes can pull from this
		ManagementTube tube = CommonHelper.getInterface(world(), position, ManagementTube.class);
		
		return (tube != null);
	}
	
	@Override
	public boolean canPullItem( IFilter filter, int side, int count, SizeMode mode)
	{
		IPayloadHandler<? extends Payload> handler = getHandler(null);
		if(handler != null)
			return handler.extract(filter, side, count, mode, false) != null;
		
		return false;
	}
	
	@Override
	public Payload pullItem( IFilter filter, int side, int count, SizeMode mode, boolean doExtract )
	{
		IPayloadHandler<? extends Payload> handler = getHandler(null);
		if(handler != null)
			return handler.extract(filter, side, count, mode, doExtract);
		
		return null;
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		super.writeDesc(packet);
		packet.writeByte(mPriority);
		packet.writeShort(mColor);
		packet.writeByte(mMode.ordinal());
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		super.readDesc(packet);
		mPriority = packet.readByte();
		mColor = packet.readShort();
		mMode = ManagementMode.values()[packet.readByte()];
	}
	
	@Override
	protected void onRecieveDataClient( int channel, MCDataInput input )
	{
		switch(channel)
		{
		case CHANNEL_COLOR:
			mColor = input.readShort();
			break;
		case CHANNEL_PRIORITY:
			mPriority = input.readByte();
			break;
		case CHANNEL_MODE:
			mMode = ManagementMode.values()[input.readByte()];
			break;
		default:
			super.onRecieveDataClient(channel, input);
		}
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		root.setShort("Color", (short)mColor);
		root.setByte("Priority", (byte)mPriority);
		root.setString("Mode", mMode.name());
		
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < mFilters.length; ++i)
		{
			IFilter filter = mFilters[i];
			if(filter != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				FilterRegistry.getInstance().writeFilter(filter, tag);
				tag.setInteger("FSlot", i);
				list.appendTag(tag);
			}
		}
		
		root.setTag("Filters", list);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		mColor = root.getShort("Color");
		mPriority = root.getByte("Priority");
		mMode = ManagementMode.valueOf(root.getString("Mode"));
		
		mFilters = new IFilter[mFilters.length];
		NBTTagList list = root.getTagList("Filters", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int index = tag.getInteger("FSlot");
			mFilters[index] = FilterRegistry.getInstance().readFilter(tag);
		}
	}
	
	private static class PrioritySorter implements Comparator<ManagementTube>
	{
		public static final PrioritySorter instance = new PrioritySorter();
		
		@Override
		public int compare( ManagementTube o1, ManagementTube o2 )
		{
			return Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
		}
	}
}
