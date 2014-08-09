package schmoller.tubes.routing.goals;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.IRoutingGoal;

public class OutputGoal implements IRoutingGoal
{
	@Override
	public boolean isDestination( Position position, IBlockAccess world, int side, TubeItem item )
	{
		TileEntity ent = CommonHelper.getTileEntity(world, position);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		
		if(con == null)
		{
			IPayloadHandler handler = InteractionHandler.getHandler(item.item.getClass(), world, position);
			if(handler != null)
			{
				Payload remaining = handler.insert(item.item, side ^ 1, false);
				
				if(remaining == null || remaining.size() != item.item.size())
					return true;
			}
		}
		else if(!con.canPathThrough() && con.canItemEnter(item))
			return true;
		
		return false;
	}

	@Override
	public boolean hasCustomRoute()
	{
		return false;
	}

	@Override
	public PathLocation route( TubeItem item, IBlockAccess world, int x, int y, int z )
	{
		return null;
	}
}
