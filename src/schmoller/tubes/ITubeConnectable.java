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
	
	public boolean canPathThrough();
	
	public int getRouteWeight();
	
	public int getColor();
}
