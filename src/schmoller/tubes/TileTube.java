package schmoller.tubes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import schmoller.tubes.network.packets.ModPacketAddItem;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileTube extends TileEntity implements ITube
{
	private LinkedList<TubeItem> mItemsInTransit = new LinkedList<TubeItem>();

	public static int transitTime = 1000;
	
	private int mConnections = -1;
	
	@Override
	public boolean addItem(ItemStack item, int fromDir)
	{
		TubeItem tItem = new TubeItem(item);
		tItem.direction = fromDir;
		tItem.progress = 0;
		
		if(!worldObj.isRemote)
			mItemsInTransit.add(tItem);
		else
			ModTubes.packetManager.sendPacketForBlock(new ModPacketAddItem(xCoord, yCoord, zCoord, tItem), worldObj);
		
		return true;
	}
	
	@Override
	public boolean addItem(TubeItem item)
	{
		mItemsInTransit.add(item);
		return true;
	}
	
	@Override
	public boolean canAddItem( TubeItem item )
	{
		return true;
	}
	
	@Override
	public boolean canPathThrough()
	{
		return true;
	}
	
	public int getConnections()
	{
		//if(mConnections == -1)
			return mConnections = TubeHelper.getConnectivity(worldObj, xCoord, yCoord, zCoord);
		
		//return mConnections;
	}
	
	@Override
	public int getConnectableMask()
	{
		return 63;
	}
	
	private int getNextDirection(TubeItem item)
	{
		int count = 0;
		int dir = -1;
		
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
			if(worldObj.isRemote)
				return -1;
			
			if(count > 1)
				dir = TubeHelper.findNextDirection(worldObj, xCoord, yCoord, zCoord, item);
			
			if(dir == -1)
				dir = item.direction ^ 1;
			
			int l = item.direction;
			item.direction = dir;
			item.updated = true;
			ModTubes.packetManager.sendPacketForBlock(new ModPacketAddItem(xCoord, yCoord, zCoord, item), worldObj);
			item.updated = false;
			item.direction = l;
		}
		else if(dir == -1)
			dir = item.direction ^ 1;
			
		
		return dir;
	}
	
	@Override
	public void updateEntity()
	{
		Iterator<TubeItem> it = mItemsInTransit.iterator();
		
		while(it.hasNext())
		{
			TubeItem item = it.next();
			item.progress += 0.1;
			
			if(!item.updated && item.progress >= 0.5)
			{
				// Find new direction to go
				item.direction = getNextDirection(item);
				if(item.direction == -1)
				{
					it.remove();
					continue;
				}
				
				item.updated = true;
			}
			
			if(item.progress >= 1)
			{
				if(transferToNext(item))
					it.remove();
				else
				{
					item.progress -= 1;
					item.updated = false;
					item.direction ^= 1;
					
					if(!worldObj.isRemote)
						ModTubes.packetManager.sendPacketForBlock(new ModPacketAddItem(xCoord, yCoord, zCoord, item), worldObj);
				}
			}
		}
	}
	
	public List<TubeItem> getItems()
	{
		return mItemsInTransit;
	}
	
	private boolean transferToNext(TubeItem item)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
		
		TileEntity ent = worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
		
		if(ent instanceof ITubeConnectable)
		{
			item.progress -= 1;
			item.updated = false;
			
			if(((ITubeConnectable)ent).addItem(item))
				return true;
		}
		
		if(worldObj.isRemote)
			return true;
		
		if(ent != null && InventoryHelper.canAcceptItem(item.item, worldObj, ent.xCoord, ent.yCoord, ent.zCoord, item.direction))
		{
			InventoryHelper.insertItem(item.item, worldObj, ent.xCoord, ent.yCoord, ent.zCoord, item.direction);
			
			if(item.item.stackSize == 0)
				return true;
		}
		
		return false;
	}

	public void onNeighbourUpdate()
	{
		mConnections = -1;
	}
	
	@Override
	public int getRouteWeight()
	{
		return 1;
	}

	@Override
	public boolean isBlocked()
	{
		return false;
	}
	
}
