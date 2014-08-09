package schmoller.tubes.routing;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseRouter;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.IRoutingGoal;

public class GoalRouter extends BaseRouter
{
	private TubeItem mItem;
	private int mDirection = -1;
	private IRoutingGoal mGoal;
	
	public GoalRouter(IBlockAccess world, Position position, TubeItem item, IRoutingGoal goal)
	{
		mItem = item.clone();
		mItem.goal = goal;
		mGoal = goal;
		setup(world, position);
	}
	
	public GoalRouter(IBlockAccess world, Position position, TubeItem item, int direction, IRoutingGoal goal)
	{
		mItem = item.clone();
		mItem.goal = goal;
		mDirection = direction;
		mGoal = goal;
		setup(world, position);
	}
	
	@Override
	protected void getNextLocations( PathLocation current )
	{
		mItem.colour = current.color;
		mItem.direction = current.dir;
		int conns = TubeHelper.getConnectivity(getWorld(), current.position);
		ITubeConnectable myCon = TubeHelper.getTubeConnectable(getWorld(), current.position.x, current.position.y, current.position.z);
		int allowed = (myCon != null ? myCon.getRoutableDirections(mItem) : 63);
		
		conns &= allowed;
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				mItem.colour = current.color;
				mItem.direction = current.dir;
				
				PathLocation loc = new PathLocation(current, i);
				
				TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
				ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
				
				if(con != null)
				{
					mItem.direction = loc.dir;
					mItem.colour = loc.color;
					mItem.goal = mGoal;
					
					if(!con.canItemEnter(mItem))
						continue;
					
					myCon.simulateEffects(mItem);
					loc.color = mItem.colour;
					
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
		
		int initialColor = mItem.colour;
		int initialDir = mItem.direction;
		
		for(int i = 0; i < 6; ++i)
		{
			if(mDirection != -1 && mDirection != i)
				continue;
			
			if((conns & (1 << i)) != 0)
			{
				mItem.colour = initialColor;
				mItem.direction = initialDir;
				mItem.goal = mGoal;
				
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
					
					myCon.simulateEffects(mItem);
					loc.color = mItem.colour;
					
					loc.dist += con.getRouteWeight() - 1;
				}
				
				addSearchPoint(loc);
			}
		}
	}

	@Override
	protected boolean isTerminator( Position current, int side )
	{
		mItem.direction = side;
		
		return mGoal.isDestination(current, getWorld(), side, mItem);
	}

}
