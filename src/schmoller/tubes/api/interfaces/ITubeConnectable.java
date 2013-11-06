package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.TubeItem;
import net.minecraft.item.ItemStack;

/**
 * Implement this if you are making a tube connectable block. 
 * If you are making a tube, implement ITube
 */
public interface ITubeConnectable
{
	/**
	 * Gets a mask of which sides can be connected to. 
	 * Each bit from 0-5 represents a side where the 0th bit is down.
	 */
	public int getConnectableMask();

	/**
	 * Checks if the item can enter this tube.
	 */
	public boolean canItemEnter(TubeItem item);
	/**
	 * Checks if the item can enter this tube from the specified direction.
	 * @param direction A direction from 0-5 or -1 for any direction
	 */
	public boolean canAddItem(ItemStack item, int direction);

	/**
	 * Adds the item to the tube from the specified side.
	 * @param side The side 0-5 to add from, or -1 for any direction
	 * @return True if the item was actually added
	 */
	public boolean addItem(ItemStack item, int side);
	/**
	 * Adds the item to the tube.
	 * The updated field of the item should be reset by this method.
	 * The should not be synchronized to the client
	 * @return True if the item was actually added
	 */
	public boolean addItem(TubeItem item);
	/**
	 * Adds the item to the tube.
	 * The updated field of the item should be reset by this method.
	 * @param syncToClient True if the client should be sent to the client.
	 * @return True if the item was actually added
	 */
	public boolean addItem(TubeItem item, boolean syncToClient);
	
	/**
	 * This is used by routing to keep track of state changes to items.
	 * If a tube sets the items color, this method should do that
	 * @return false indicates that the item would have been removed
	 */
	public boolean simulateEffects(TubeItem item);
	
	/**
	 * This is used by routing to determine directions the routing can go. Only called if canPathThrough() is true
	 * @return A mask of the directions that can be pathed through the same format as getConnectableMask() 
	 */
	public int getRoutableDirections(TubeItem item);
	
	/**
	 * If true, routing can use this tube connectable to find others. 
	 */
	public boolean canPathThrough();

	/**
	 * The route weight is how much distance should be added by this tube.
	 * You CANNOT use values <= 0, the results will be unpredictable
	 */
	public int getRouteWeight();
	
	/**
	 * The color this tube associates with. This is primarily used for rendering. The color is a dye index
	 */
	public int getColor();
}
