package schmoller.tubes.routing;

import schmoller.tubes.Position;
import schmoller.tubes.api.InventoryHandlerRegistry;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IInventoryHandler;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class OutputRouter extends BaseRouter
{
	private TubeItem mItem;
	private int mDirection = -1;
	
	public OutputRouter(IBlockAccess world, Position position, TubeItem item)
	{
		mItem = item.clone();
		mItem.state = TubeItem.NORMAL;
		setup(world, position);
	}
	
	public OutputRouter(IBlockAccess world, Position position, TubeItem item, int direction)
	{
		mItem = item.clone();
		mItem.state = TubeItem.NORMAL;
		mDirection = direction;
		setup(world, position);
	}
	
	
	@Override
	protected void getNextLocations( PathLocation current )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), current.position);
		ITubeConnectable myCon = TubeHelper.getTubeConnectable(getWorld(), current.position.x, current.position.y, current.position.z);
		int allowed = (myCon != null ? myCon.getRoutableDirections(mItem) : 63);
		
		conns &= allowed;
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				PathLocation loc = new PathLocation(current, i);
				
				TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
				ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
				
				if(con != null)
				{
					mItem.direction = loc.dir;
					mItem.colour = loc.color;
					if(!con.canItemEnter(mItem))
						continue;
					
					loc.dist += con.getRouteWeight() - 1;
				}
				
				addSearchPoint(loc);
			}
		}
	}
	
	@Override
	protected void getInitialLocations( Position position )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), position);
		ITubeConnectable myCon = TubeHelper.getTubeConnectable(getWorld(), position.x, position.y, position.z);
		int allowed = (myCon != null ? myCon.getRoutableDirections(mItem) : 63);
		
		conns &= allowed;
		
		for(int i = 0; i < 6; ++i)
		{
			if(mDirection != -1 && mDirection != i)
				continue;
			
			if((conns & (1 << i)) != 0)
			{
				PathLocation loc = new PathLocation(position, i);
				loc.color = mItem.colour;
				
				TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
				ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
				
				if(con != null)
				{
					mItem.direction = loc.dir;
					mItem.colour = loc.color;
					if(!con.canItemEnter(mItem))
						continue;
					
					loc.dist += con.getRouteWeight() - 1;
				}
				
				addSearchPoint(loc);
			}
		}
	}
	
	@Override
	protected void updateState( PathLocation current )
	{
		TileEntity ent = CommonHelper.getTileEntity(getWorld(), current.position);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		
		if(con != null)
		{
			mItem.colour = current.color;
			mItem.direction = current.dir;
			con.simulateEffects(mItem);
			
			current.color = mItem.colour;
		}
	}

	@Override
	protected boolean isTerminator( Position current, int side )
	{
		TileEntity ent = CommonHelper.getTileEntity(getWorld(), current);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		mItem.direction = side;
		
		if(con == null)
		{
			IInventoryHandler handler = InventoryHandlerRegistry.getHandler(ent);
			if(handler != null)
			{
				ItemStack remaining = handler.insertItem(mItem.item, side ^ 1, false);
				
				if(remaining == null || remaining.stackSize != mItem.item.stackSize)
					return true;
			}
		}
		else if(!con.canPathThrough() && con.canItemEnter(mItem))
			return true;

		return false;
	}

}
