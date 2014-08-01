package schmoller.tubes.api.interfaces;

import net.minecraft.world.IBlockAccess;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;

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
	
	public int getStateId();
}
