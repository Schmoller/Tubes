package schmoller.tubes.inventory;

import net.minecraft.item.ItemStack;

public interface IInventoryHandler
{
	/**
	 * Inserts an item into the inventory
	 * @param item The item to insert. This cannot be null
	 * @param side The side to insert from
	 * @param doAdd When false, this action will only be simulated
	 * @return Any remaining items that were not inserted, or null if everything was inserted
	 */
	public ItemStack insertItem(ItemStack item, int side, boolean doAdd);
	
	/**
	 * Extracts an item out of the inventory
	 * @param template The type of item to grab, or null for any item
	 * @param side The side to grab from
	 * @param doExtract When false, this action will only be simulated
	 * @return The item that was extracted
	 */
	public ItemStack extractItem(ItemStack template, int side, boolean doExtract);
	/**
	 * Extracts an item out of the inventory
	 * @param template The type of item to grab, or null for any item
	 * @param side The side to grab from
	 * @param count The amount of items to pull. The exact meaning of this depends on mode
	 * @param mode What amount of items to pull
	 * @param doExtract When false, this action will only be simulated
	 * @return The item that was extracted
	 */
	public ItemStack extractItem(ItemStack template, int side, int count, SizeMode mode, boolean doExtract);
}
