package schmoller.tubes.routing;

import schmoller.tubes.CommonHelper;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ITubeOverflowDestination;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;

public class BlockedRouter extends BaseRouter
{
	private TubeItem mItem;

	public BlockedRouter(IBlockAccess world, ChunkPosition position, TubeItem item)
	{
		mItem = item;
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
					if((mItem.colour != -1 && con.getColor() != -1 && con.getColor() != mItem.colour) || !con.canItemEnter(mItem))
						continue;
					
					loc.dist += con.getRouteWeight() - 1;
				}
				
				addSearchPoint(loc);
			}
		}
	}
	
	@Override
	protected void getInitialLocations( ChunkPosition position )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), position);
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				PathLocation loc = new PathLocation(position, i);
				
				TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
				ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
				
				if(con != null)
				{
					if((mItem.colour != -1 && con.getColor() != -1 && con.getColor() != mItem.colour) || !con.canItemEnter(mItem))
						continue;
					
					loc.dist += con.getRouteWeight() - 1;
				}
				
				addSearchPoint(loc);
			}
		}
	}

	@Override
	protected boolean isTerminator( ChunkPosition current, int side )
	{
		TileEntity ent = CommonHelper.getTileEntity(getWorld(), current);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		
		if(con instanceof ITubeOverflowDestination && ((ITubeOverflowDestination)con).canAcceptOverflowFromSide(side))
			return true;
		
		return false;
	}

}
