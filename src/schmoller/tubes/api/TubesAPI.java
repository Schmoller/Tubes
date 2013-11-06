package schmoller.tubes.api;

import net.minecraft.item.ItemStack;

public abstract class TubesAPI
{
	public static TubesAPI instance = null;
	
	/**
	 * Registers an advanced shaped recipe. 
	 * @param output The item that is resultant
	 * @param input This is exactly the same as normal shaped recipes, 
	 *              except you can put a char -> string mapping for an ore dictionary entry, 
	 *              and a char -> fluid (char -> FluidStack) for a liquid container that holds 
	 *              the specified liquid (and amount if you use a FluidStack)
	 */
	public abstract void registerShapedRecipe(ItemStack output, Object... input);
	
	/**
	 * Registers an advanced shapeless recipe.
	 * @param output The item that is resultant
	 * @param input This is exactly the same as normal shapeless recipes, 
	 *              except you can put a string for an ore dictionary entry, 
	 *              and a fluid (or FluidStack) for a liquid container that holds 
	 *              the specified liquid (and amount if you use a FluidStack)
	 */
	public abstract void registerShapelessRecipe(ItemStack output, Object... input);
	
	public abstract ItemStack createTubeForType(String type);
	public abstract ItemStack createTubeForType(String type, int amount);
	
	public abstract String getTubeType(ItemStack item);
}
