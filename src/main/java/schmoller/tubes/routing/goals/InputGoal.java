package schmoller.tubes.routing.goals;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.ITubeImportDest;
import schmoller.tubes.api.interfaces.IRoutingGoal;

public class InputGoal implements IRoutingGoal
{
	@Override
	public boolean isDestination( Position position, IBlockAccess world, int side, TubeItem item )
	{
		TileEntity ent = CommonHelper.getTileEntity(world, position);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		
		return (con instanceof ITubeImportDest && ((ITubeImportDest)con).canImportFromSide(side) && con.canItemEnter(item));
	}

	@Override
	public int getStateId()
	{
		return 1;
	}
}
