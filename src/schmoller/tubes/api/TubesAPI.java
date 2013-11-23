package schmoller.tubes.api;

import schmoller.tubes.api.helpers.BaseRouter;
import schmoller.tubes.api.interfaces.IFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public abstract class TubesAPI
{
	public static TubesAPI instance = null;
	
	/**
	 * Registers an advanced shaped recipe. 
	 * Advanced shaped recipes can handle fluid containers, 
	 * ore dictionary items, and items that need NBT data to 
	 * differentiate them (implement ISpecialItemCompare on the Item)
	 * @param output The item that is resultant
	 * @param input This is exactly the same as normal shaped recipes, 
	 *              except you can put a char -> string mapping for an ore dictionary entry, 
	 *              and a char -> fluid (char -> FluidStack) for a liquid container that holds 
	 *              the specified liquid (and amount if you use a FluidStack)
	 */
	public abstract void registerShapedRecipe(ItemStack output, Object... input);
	
	/**
	 * Registers an advanced shapeless recipe.
	 * Advanced shapeless recipes can handle fluid containers, 
	 * ore dictionary items, and items that need NBT data to 
	 * differentiate them (implement ISpecialItemCompare on the Item)
	 * @param output The item that is resultant
	 * @param input This is exactly the same as normal shapeless recipes, 
	 *              except you can put a string for an ore dictionary entry, 
	 *              and a fluid (or FluidStack) for a liquid container that holds 
	 *              the specified liquid (and amount if you use a FluidStack)
	 */
	public abstract void registerShapelessRecipe(ItemStack output, Object... input);
	
	/**
	 * Creates an ItemStack containing that tube type
	 * @param type A type that was registered with the TubeRegistry
	 * @return The ItemStack, or null if there wasn't one
	 */
	public abstract ItemStack createTubeForType(String type);
	/**
	 * Creates an ItemStack containing that tube type with the specified amount
	 * @param type A type that was registered with the TubeRegistry
	 * @param amount The stack size of the returned ItemStack
	 * @return The ItemStack, or null if there wasn't one
	 */
	public abstract ItemStack createTubeForType(String type, int amount);
	
	/**
	 * Gets what type of tube the ItemStack holds, or null if it does not.
	 */
	public abstract String getTubeType(ItemStack item);
	
	
	public abstract BaseRouter getOutputRouter(IBlockAccess world, Position position, TubeItem item);
	public abstract BaseRouter getOutputRouter(IBlockAccess world, Position position, TubeItem item, int direction);
	
	public abstract BaseRouter getImportRouter(IBlockAccess world, Position position, TubeItem item);
	public abstract BaseRouter getImportSourceRouter(IBlockAccess world, Position position, int startDirection, IFilter filter, SizeMode mode);
	
	public abstract BaseRouter getOverflowRouter(IBlockAccess world, Position position, TubeItem item);
}
