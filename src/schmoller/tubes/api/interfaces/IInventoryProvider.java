package schmoller.tubes.api.interfaces;

import net.minecraft.inventory.IInventory;

/**
 * A provider can specify an inventory for any object. 
 * This can be used to remap slots, make an inventory smart,
 * give an inventory to something that didn't have one, etc.
 */
public interface IInventoryProvider
{
	/**
	 * Returns an inventory object for the specified object, or null
	 */
	public IInventory provide(Object object);
}
