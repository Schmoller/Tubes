package schmoller.tubes.routing;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.helpers.BaseRouter;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class ImportSourceFinder extends BaseRouter
{
	private IFilter mItem;
	private int mStartDir;
	private SizeMode mMode;
	
	public ImportSourceFinder(IBlockAccess world, Position position, int startDirection, IFilter filterItem, SizeMode mode)
	{
		mItem = filterItem;
		mStartDir = startDirection;
		mMode = mode;
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
					if(!con.canPathThrough())
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
		
		if((conns & (1 << mStartDir)) != 0)
		{
			PathLocation loc = new PathLocation(position, mStartDir);
			
			TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
			ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
			
			if(con != null)
			{
				if(!con.canPathThrough())
					return;
				
				loc.dist += con.getRouteWeight() - 1;
			}
			
			addSearchPoint(loc);
		}
	}

	@Override
	protected boolean isTerminator( Position current, int side )
	{
		TileEntity ent = CommonHelper.getTileEntity(getWorld(), current);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		
		if(con == null)
		{
			IPayloadHandler handler = InteractionHandler.getHandler((mItem == null ? null : mItem.getPayloadType()), getWorld(),current);
			if(handler != null)
			{
				Payload extracted;
				if(mItem == null)
					extracted = handler.extract(new AnyFilter(0), side ^ 1, false);
				else
					extracted = handler.extract(mItem, side ^ 1, mItem.size(), mMode, false);
				
				if(extracted != null)
					return true;
			}
		}
		return false;
	}

}
