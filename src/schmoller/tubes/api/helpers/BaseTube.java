package schmoller.tubes.api.helpers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.microblock.HollowMicroblock;
import codechicken.multipart.TFacePart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.scalatraits.TSlottedTile;

import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.parts.BaseTubePart;

public abstract class BaseTube extends BaseTubePart implements ITube
{
	private LinkedList<TubeItem> mItemsInTransit = new LinkedList<TubeItem>();
	private boolean mIsUpdating = false;
	private LinkedList<TubeItem> mWaitingToAdd = new LinkedList<TubeItem>();

	public static int transitTime = 1000;
	public static int blockedWaitTime = 10;
	
	public static final int CHANNEL_ADDITEM = 0;
	public static final int NO_COLOUR = -1;
	
	private int mTick = 0;
	
	public static final int NO_ROUTE = -1;
	public static final int ROUTE_TERM = -2;
	
	public BaseTube( String type )
	{
		super(type);
	}

	
	@Override
	public boolean addItem(Payload item, int fromDir)
	{
		assert(fromDir >= -1 && fromDir < 6);
		
		
		TubeItem tItem = new TubeItem(item);
		if(fromDir == -1)
		{
			tItem.direction = 6;
			tItem.setProgress(0.5f);
		}
		else
		{
			tItem.direction = fromDir;
			tItem.setProgress(0);
		}
		
		onItemEnter(tItem);
		
		if(!world().isRemote)
		{
			if(mIsUpdating)
				mWaitingToAdd.add(tItem);
			else
				mItemsInTransit.add(tItem);
			
			if(tItem.direction != 6)
				addToClient(tItem);
		}
		
		return true;
	}
	
	@Override
	public boolean addItem(TubeItem item)
	{
		return addItem(item, false);
	}
	
	@Override
	public boolean addItem(TubeItem item, boolean syncToClient)
	{
		assert(item.direction >= -1 && item.direction <= 6);
		
		onItemEnter(item);
		if(mIsUpdating)
			mWaitingToAdd.add(item);
		else
			mItemsInTransit.add(item);
		
		if(syncToClient)
			addToClient(item);
		
		return true;
	}
	
	@Override
	public int getColor()
	{
		return NO_COLOUR;
	}
	
	@Override
	public boolean canPathThrough()
	{
		return true;
	}
	
	@Override
	public int getConnections()
	{
		return TubeHelper.getConnectivity(world(), x(), y(), z());
	}
	
	private int getNumConnections()
	{
		int count = 0;
		int con = getConnections();
		for(int i = 0; i < 6; ++i)
		{
			if((con & (1 << i)) != 0)
				++count;
		}
		
		return count;
	}
	
	@Override
	public final int getConnectableMask()
	{
		int con = getConnectableSides();
		
		if (tile() instanceof TSlottedTile)
		{
			for(int i = 0; i < 6; ++i)
			{
				TMultiPart part = tile().partMap(i);
				if(part instanceof TFacePart && !(part instanceof HollowMicroblock))
					con -= (con & (1 << i));
			}
		}
		return con;
	}
	
	private boolean mDidRoute = false;
	private int getNextDirection(TubeItem item)
	{
		int count = 0;
		int dir = NO_ROUTE;
		
		mDidRoute = false;
		
		if(hasCustomRouting())
		{
			if(world().isRemote)
				return ROUTE_TERM;
			
			mDidRoute = true;
			return onDetermineDestination(item);
		}
		
		int conns = getConnections();
		
		conns -= (conns & (1 << (item.direction ^ 1)));
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				++count;
				dir = i;
			}
		}
		
		if(count > 1)
		{
			if(world().isRemote)
				return ROUTE_TERM;
			
			if(count > 1)
			{
				dir = TubeHelper.findNextDirection(world(), x(), y(), z(), item);
				
				mDidRoute = true;
			}
		}
		
		return dir;
	}
	
	@Override
	public int getRouteWeight()
	{
		return 1;
	}
	
	@Override
	public void update()
	{
		mIsUpdating = true;
		Iterator<TubeItem> it = mItemsInTransit.iterator();
		
		while(it.hasNext())
		{
			TubeItem item = it.next();
			
			if(item.direction == 6) // It needs a path right away
			{
				if(!onItemJunction(item))
				{
					it.remove();
					continue;
				}
				else
					addToClient(item);
			}
			
			item.lastProgress = item.progress;
			item.progress += 0.1;
			
			if(!item.updated && item.progress >= 0.5)
			{
				// Find new direction to go
				if(!onItemJunction(item))
				{
					it.remove();
					continue;
				}
			}
			
			if(item.progress >= 1)
			{
				if(onItemLeave(item))
					it.remove();
				else
				{
					item.state = TubeItem.BLOCKED;
					item.progress -= 1;
					item.lastProgress -= 1;
					item.updated = false;
					item.direction ^= 1;
					
					if(!world().isRemote)
						addToClient(item);
				}
			}
		}
		
		mItemsInTransit.addAll(mWaitingToAdd);
		mWaitingToAdd.clear();
		
		mIsUpdating = false;
		
		int rate = getTickRate();
		
		if(rate > 0)
		{
			++mTick;
			if(mTick >= rate)
			{
				mTick = 0;
				onTick();
			}
		}
	}
	
	public List<TubeItem> getItems()
	{
		return mItemsInTransit;
	}
	
	private int randDirection(int fromDir)
	{
		fromDir = fromDir ^ 1;
		int total = getNumConnections();
		if(total <= 1)
			return fromDir;
		
		
		int num = TubeHelper.rand.nextInt(total - 1);
		
		int index = 0;
		int con = getConnections();
		for(int i = 0; i < 6; ++i)
		{
			if(i != fromDir && (con & (1 << i)) != 0)
			{
				if(num == index)
					return i;
				
				++index;
			}
		}
		
		return fromDir;
	}
	

	private boolean handleJunction(TubeItem item)
	{
		int lastDir = item.direction;
		item.direction = getNextDirection(item);
		item.updated = true;
		if(item.direction == NO_ROUTE)
		{
			if(getNumConnections() == 1)
				item.direction = lastDir ^ 1;
			else
			{
				item.state = TubeItem.BLOCKED;
				item.direction = TubeHelper.findNextDirection(world(), x(), y(), z(), item);
				if(item.direction == NO_ROUTE)
					item.direction = randDirection(lastDir);
				
				addToClient(item); // Client will have deleted it
			}
		}
		else if(item.direction == ROUTE_TERM)
			return false;
		// Synch the new direction to client
		else if(!world().isRemote && mDidRoute)
			addToClient(item);
		
		return true;
	}
	
	protected void addToClient(TubeItem item)
	{
		if(world().isRemote)
			return;
	
		item.write(openChannel(CHANNEL_ADDITEM));
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		NBTTagList list = new NBTTagList();
		for(TubeItem item : mItemsInTransit)
		{
			NBTTagCompound tag = new NBTTagCompound();
			item.writeToNBT(tag);
			list.appendTag(tag);
		}
		
		root.setTag("items", list);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		mItemsInTransit.clear();
		
		NBTTagList list = root.getTagList("items");
		
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)list.tagAt(i);
			
			mItemsInTransit.add(TubeItem.readFromNBT(tag));
		}
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		packet.writeShort(mItemsInTransit.size());
		for(TubeItem item : mItemsInTransit)
			item.write(packet);
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		int count = packet.readShort() & 0xFFFF;
		
		mItemsInTransit.clear();
		
		for(int i = 0; i < count; ++i)
			mItemsInTransit.add(TubeItem.read(packet));
	}
	
	@Override
	protected void onRecieveDataClient( int channel, MCDataInput input )
	{
		if(channel == CHANNEL_ADDITEM)
			mItemsInTransit.add(TubeItem.read(input));

		super.onRecieveDataClient(channel, input);
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		for(TubeItem item : mItemsInTransit)
		{
			if(item.item instanceof ItemPayload)
				itemsToDrop.add((ItemStack)item.item.get());
		}
	}
	
	@Override
	public void simulateEffects( TubeItem item ) {}
	
	@Override
	public int getRoutableDirections( TubeItem item ) { return 63; }
	
	// ======================================
	//  Thingies
	// ======================================
	
	@Override
	public boolean canItemEnter(TubeItem item) { return canAddItem(item.item, item.direction); }
	@Override
	public boolean canAddItem(Payload item, int direction) { return true; }
	
	/**
	 * Called when an item enters this tube either from another tube, or from something else
	 */
	protected void onItemEnter(TubeItem item) {}
	
	/**
	 * Called when an item is about to leave the tube in a particular direction.
	 * @return true will cause the item to be removed
	 */
	protected boolean onItemLeave(TubeItem item) 
	{
		if(item.direction == 6)
			return false;
		
		ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
		
		TileEntity ent = world().getBlockTileEntity(x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		if(con != null)
		{
			if(con.canItemEnter(item))
			{
				item.progress -= 1;
				item.lastProgress -= 1;
				item.updated = false;
				
				
				return con.addItem(item);
			}
			else
				return false;
		}
		
		if(world().isRemote)
			return true;
		
		IPayloadHandler handler = InteractionHandler.getHandler(item.item.getClass(), world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
	
		if(handler != null)
		{
			Payload remaining = handler.insert(item.item, item.direction ^ 1, true);
			if(remaining == null)
				return true;
			
			item.item = remaining;
		}
		
		return false;
	}
	
	/**
	 * Called when an item hits the junction point (regardless if there is a junction or not)
	 * @return false will cause the item to be removed
	 */
	protected boolean onItemJunction(TubeItem item) 
	{
		return handleJunction(item);
	}
	
	@Override
	public boolean canConnectToInventories() { return true; }
	@Override
	public boolean canConnectTo(ITubeConnectable con) { return true; }
	
	protected int getConnectableSides() { return 63; }
	
	protected boolean hasCustomRouting() { return false; }
	protected int onDetermineDestination(TubeItem item) {return NO_ROUTE;}
	
	/**
	 * Gets how often this logic receives a tick. A rate of 1 is every tick, 2 is every second tick and so on. 
	 * A rate of 0 means to never tick
	 */
	public int getTickRate() { return 0; }
	
	public void onTick() {}
	
	
}
