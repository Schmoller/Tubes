package schmoller.tubes.routing;

import net.minecraft.tileentity.TileEntity;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.helpers.BaseRouter;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.types.ManagementTube;

public class ManagementTubeFinder extends BaseRouter
{
	private ManagementTube mStart;
	
	public ManagementTubeFinder(ManagementTube start)
	{
		mStart = start;
		setup(start.world(), new Position(start.x(), start.y(), start.z()));
	}
	
	@Override
	protected void getInitialLocations( Position position )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), position);
		ITubeConnectable myCon = TubeHelper.getTubeConnectable(getWorld(), position.x, position.y, position.z);

		int dir = mStart.getFacing() ^ 1;
		if((conns & (1 << dir)) != 0)
		{
			PathLocation loc = new PathLocation(position, dir);
			
			TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
			ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
			
			if(con != null)
			{
				loc.dist += con.getRouteWeight() - 1;
				
				addSearchPoint(loc);
			}
		}
	}
	
	@Override
	protected void getNextLocations( PathLocation current )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), current.position);
		ITubeConnectable myCon = TubeHelper.getTubeConnectable(getWorld(), current.position.x, current.position.y, current.position.z);
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				PathLocation loc = new PathLocation(current, i);
				
				TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
				ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
				
				if(con != null)
				{
					loc.dist += con.getRouteWeight() - 1;
					addSearchPoint(loc);
				}
			}
		}
	}

	@Override
	protected boolean isTerminator( Position current, int side )
	{
		ITubeConnectable con = TubeHelper.getTubeConnectable(getWorld(), current.x, current.y, current.z);
		
		if(con instanceof ManagementTube)
		{
			ManagementTube other = (ManagementTube)con;
			
			if((other.getColor() == -1 || mStart.getColor() == -1 || mStart.getColor() == other.getColor()) &&
				other.getPriority() > mStart.getPriority())
				return true;
		}
		
		return false;
	}

}
