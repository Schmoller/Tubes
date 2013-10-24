package schmoller.tubes.routing;

import schmoller.tubes.CommonHelper;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ITubeOverflowDestination;
import schmoller.tubes.Position;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class BlockedRouter extends BaseRouter
{
	private TubeItem mItem;

	public BlockedRouter(IBlockAccess world, Position position, TubeItem item)
	{
		mItem = item.clone();
		setup(world, position);
	}
	
	@Override
	protected void getNextLocations( PathLocation current )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), current.position);
		
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
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				PathLocation loc = new PathLocation(position, i);
				loc.color = mItem.colour;				
				
				TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
				ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
				
				if(con != null)
				{
					mItem.direction = loc.dir;
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
		
		if(con instanceof ITubeOverflowDestination && ((ITubeOverflowDestination)con).canAcceptOverflowFromSide(side))
			return true;
		
		return false;
	}

}
