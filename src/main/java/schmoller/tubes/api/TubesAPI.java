package schmoller.tubes.api;

import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IRoutingGoal;
import schmoller.tubes.routing.goals.InputGoal;
import schmoller.tubes.routing.goals.OutputGoal;
import schmoller.tubes.routing.goals.OverflowGoal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public abstract class TubesAPI
{
	public static TubesAPI instance = null;
	public static final IRoutingGoal goalOutput = new OutputGoal();
	public static final IRoutingGoal goalInput = new InputGoal();
	public static final IRoutingGoal goalOverflow = new OverflowGoal();
	
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
	
	/**
	 * Finds a destination for the specified item using the specified goal
	 * @param item The item to route
	 * @param world The world for the location
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 * @param goal The goal of this routing
	 * @return A PathLocation or null
	 */
	public abstract PathLocation routeItem(TubeItem item, IBlockAccess world, int x, int y, int z, IRoutingGoal goal);
	
	/**
	 * Finds a destination for the specified item using the specified goal starting from a specific direction
	 * @param item The item to route
	 * @param world The world for the location
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 * @param direction The starting direction
	 * @param goal The goal of this routing
	 * @return A PathLocation or null
	 */
	public abstract PathLocation routeItem(TubeItem item, IBlockAccess world, int x, int y, int z, int direction, IRoutingGoal goal);
	
	public abstract CreativeTabs getCreativeTab();
}
