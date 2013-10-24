package schmoller.tubes;

import net.minecraft.item.ItemStack;

public interface ITubeConnectable
{
	/**
	 * Gets what faces tubes can connect to.
	 * @return An Integer where each bit from 0-5 represents a side
	 */
	public int getConnectableMask();
	
	public boolean canItemEnter(TubeItem item);
	public boolean canAddItem(ItemStack item, int direction);
	
	public boolean addItem(ItemStack item, int side);
	public boolean addItem(TubeItem item);
	public boolean addItem(TubeItem item, boolean syncToClient);
	
	/**
	 * This is used by routing to keep track of state changes to items.
	 * If a tube sets the items color, this method should do that
	 * @return false indicates that the item would have been removed
	 */
	public boolean simulateEffects(TubeItem item);
	
	public boolean canPathThrough();
	
	public int getRouteWeight();
	
	public int getColor();
}
