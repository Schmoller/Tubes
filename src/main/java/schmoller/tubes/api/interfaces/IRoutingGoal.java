package schmoller.tubes.api.interfaces;

import net.minecraft.world.IBlockAccess;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;

public interface IRoutingGoal
{
	/**
	 * Is the specified position a valid destination for this goal
	 * @param position
	 * @param world
	 * @param side
	 * @param item
	 * @return True if it is
	 */
	public boolean isDestination(Position position, IBlockAccess world, int side, TubeItem item);
	
	public boolean hasCustomRoute();
	
	public PathLocation route(TubeItem item, IBlockAccess world, int x, int y, int z);
}
