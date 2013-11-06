package schmoller.tubes.api.interfaces;

import net.minecraft.item.ItemStack;

/**
 * This is used by special recipes to provide an extra equality check. 
 * It is used by tubes to differentiate between the tube items in recipes as they only differ by NBT data
 * 
 * Implement this on Item's
 */
public interface ISpecialItemCompare
{
	public boolean areItemsEqual(ItemStack a, ItemStack b);
}