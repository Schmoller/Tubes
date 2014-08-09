package schmoller.tubes.routing.goals;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.api.interfaces.IRoutingGoal;

public class OverflowGoal implements IRoutingGoal
{
	@Override
	public boolean isDestination( Position position, IBlockAccess world, int side, TubeItem item )
	{
		TileEntity ent = CommonHelper.getTileEntity(world, position);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		
		if(con instanceof ITubeOverflowDestination && ((ITubeOverflowDestination)con).canAcceptOverflowFromSide(side))
			return true;
		
		return false;
	}
	
	@Override
	public boolean hasCustomRoute()
	{
		return true;
	}
	
	@Override
	public PathLocation route( TubeItem item, IBlockAccess world, int x, int y, int z )
	{
		PathLocation path = TubesAPI.instance.routeItem(item, world, x, y, z, TubesAPI.goalOutput);
		
		if(path == null)
			path = TubesAPI.instance.routeItem(item, world, x, y, z, TubesAPI.goalOverflow);
		else
			item.goal = TubesAPI.goalOutput;
		
		return path;
	}
}
