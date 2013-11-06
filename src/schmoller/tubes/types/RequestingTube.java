package schmoller.tubes.types;

import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import schmoller.tubes.ModTubes;
import schmoller.tubes.Position;
import schmoller.tubes.PullMode;
import schmoller.tubes.api.InventoryHandlerRegistry;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.InventoryHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IInventoryHandler;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.ITubeImportDest;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.routing.ImportSourceFinder;
import schmoller.tubes.routing.OutputRouter;
import schmoller.tubes.routing.BaseRouter.PathLocation;

public class RequestingTube extends DirectionalTube implements ITubeImportDest, IRedstonePart, ITubeOverflowDestination
{
	private ItemStack[] mFilter = new ItemStack[16];
	private int mNext = 0;
	private PullMode mMode = PullMode.RedstoneConstant;
	private OverflowBuffer mOverflow;
	private int mColor = -1;
	
	private int mPulses = 0;
	private boolean mIsPowered;
	
	public RequestingTube()
	{
		super("requesting");
		
		mOverflow = new OverflowBuffer();
	}
	
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return !(con instanceof RequestingTube);
	}
	
	@Override
	protected int getConnectableSides()
	{
		int dir = getFacing();

		return (1 << dir) | (1 << (dir ^ 1));
	}
	
	@Override
	public boolean canPathThrough()
	{
		return false;
	}
	
	@Override
	public int getTickRate()
	{
		return mOverflow.isEmpty() ? 20 : 10;
	}
	
	@Override
	public void onTick()
	{
		if(world().isRemote)
			return;
		
		if(!mOverflow.isEmpty())
		{
			TubeItem item = mOverflow.peekNext();
			PathLocation loc = new OutputRouter(world(), new Position(x(),y(),z()), item, getFacing() ^ 1).route();
			
			if(loc != null)
			{
				mOverflow.getNext();
				item.state = TubeItem.NORMAL;
				item.direction = getFacing() ^ 1;
				item.updated = true;
				item.progress = 0.5f;
				addItem(item, true);
			}
		}
		else if(mMode == PullMode.Constant || (mMode == PullMode.RedstoneConstant && mIsPowered) || (mMode == PullMode.RedstoneSingle && mPulses > 0))
		{
			ItemStack filterItem = null;
			int start = mNext;
			do
			{
				filterItem = mFilter[mNext++];
				if(mNext >= 16)
					mNext = 0;
			}
			while(filterItem == null && mNext != start);
			
			PathLocation source = new ImportSourceFinder(world(), new Position(x(), y(), z()), getFacing(), filterItem).route();
			
			if(source != null)
			{
				IInventoryHandler handler = InventoryHandlerRegistry.getHandlerFor(world(), source.position);
				if(handler != null)
				{
					ItemStack extracted;
					if(filterItem == null)
						extracted = handler.extractItem(null, source.dir ^ 1, true);
					else
						extracted = handler.extractItem(filterItem, source.dir ^ 1, filterItem.stackSize, SizeMode.Exact, true);
					
					if(extracted != null)
					{
						TubeItem item = new TubeItem(extracted);
						item.state = TubeItem.IMPORT;
						item.direction = source.dir ^ 1;
						
						PathLocation tubeLoc = new PathLocation(source, source.dir ^ 1);
						TileEntity tile = CommonHelper.getTileEntity(world(), tubeLoc.position);
						ITubeConnectable con = TubeHelper.getTubeConnectable(tile);
						if(con != null)
							con.addItem(item, true);
						
						--mPulses;
						if(mPulses < 0)
							mPulses = 0;
					}
				}
			}
		}
	}
	
	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return (side == getFacing());
	}
	
	@Override
	public boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	public boolean simulateEffects( TubeItem item )
	{
		item.colour = mColor;
		item.state = TubeItem.NORMAL;
		
		return true;
	}
	
	@Override
	public int onDetermineDestination( TubeItem item )
	{
		if(item.state != TubeItem.IMPORT)
			return item.direction ^ 1;
		
		item.colour = mColor;
		item.state = TubeItem.NORMAL;
		
		return getFacing() ^ 1;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		if(item.state == TubeItem.BLOCKED && item.direction == getFacing())
			return true;
		else if(item.state != TubeItem.IMPORT)
			return false;
		
		return canAddItem(item.item, item.direction);
	}
	
	@Override
	public boolean canAddItem( ItemStack item, int direction )
	{
		if(direction != (getFacing() ^ 1))
			return false;
		
		boolean empty = true;
		for(int i = 0; i < 16; ++i)
		{
			if(mFilter[i] == null)
				continue;
			
			empty = false;
			
			if(InventoryHelper.areItemsEqual(mFilter[i], item))
				return true;
		}
		
		return empty;
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.state == TubeItem.BLOCKED)
		{
			if(!world().isRemote)
				mOverflow.addItem(item);
			
			return false;
		}

		if(item.direction == getFacing())
		{
			item.direction = getFacing() ^ 1;
			item.updated = true;
			
			return true;
		}
		else if(!mOverflow.isEmpty())
		{
			if(!world().isRemote)
				mOverflow.addItem(item);
			
			return false;
		}
		
		return super.onItemJunction(item);
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
			
			if(!mIsPowered && state && mMode == PullMode.RedstoneSingle)
				++mPulses;
	
			mIsPowered = state;
		}
		
		super.update();
	}
	
	@Override
	public boolean canConnectRedstone( int side ) { return true; }

	@Override
	public int strongPowerLevel( int side ) { return 0; }

	@Override
	public int weakPowerLevel( int side ) { return 0; }

	
	@Override
	public boolean canImportFromSide( int side )
	{
		return side == (getFacing() ^ 1);
	}

	public ItemStack getFilter(int slot)
	{
		return mFilter[slot];
	}
	
	public void setFilter(int slot, ItemStack item)
	{
		mFilter[slot] = item;
	}
	
	public PullMode getMode()
	{
		return mMode;
	}
	
	public void setMode(PullMode mode)
	{
		mMode = mode;
	}
	
	public short getColour()
	{
		return (short)mColor;
	}
	
	public void setColour(short colour)
	{
		mColor = colour;
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		mOverflow.onDropItems(itemsToDrop);
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		super.readDesc(input);
		mMode = PullMode.values()[input.readByte()];
		mColor = input.readShort();
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		super.writeDesc(output);
		output.writeByte(mMode.ordinal());
		output.writeShort(mColor);
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		NBTTagList filter = new NBTTagList();
		for(int i = 0; i < 16; ++i)
		{
			if(mFilter[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				mFilter[i].writeToNBT(tag);
				filter.appendTag(tag);
			}
		}

		root.setTag("Filter", filter);
		
		root.setString("PullMode", mMode.name());
		root.setInteger("Pulses", mPulses);
		mOverflow.save(root);
		
		root.setShort("Color", (short)mColor);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		NBTTagList filter = root.getTagList("Filter");
		
		if(filter == null)
			return;
		
		for(int i = 0; i < filter.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)filter.tagAt(i);
			
			int slot = tag.getInteger("Slot");
			mFilter[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
		
		mMode = PullMode.valueOf(root.getString("PullMode"));
		
		mPulses = root.getInteger("Pulses");
		mOverflow.load(root);
		
		if(root.hasKey("Color"))
			mColor = root.getShort("Color");
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(!super.activate(player, part, item))
		{
			player.openGui(ModTubes.instance, ModTubes.GUI_REQUESTING_TUBE, world(), x(), y(), z());
			return true;
		}
		return false;
	}
}
