package schmoller.tubes.api.client;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.interfaces.ITube;

/**
 * Use this to define how to render a tube.
 */
public interface ITubeRender
{
	/**
	 * This is the same as a TESR. Called once per frame.
	 * @param type The type of tube being rendered
	 * @param tube The tube object
	 * @param world A shortcut to the world object
	 * @param x A shortcut to the x location
	 * @param y A shortcut to the y location
	 * @param z A shortcut to the z location
	 */
	public void renderDynamic(TubeDefinition type, ITube tube, World world, int x, int y, int z, float frameTime);
	
	/**
	 * This is the same as an ISimpleBlockRenderingHandler 
	 * @param type The type of tube being rendered.
	 * @param tube The tube object.
	 * @param world A shortcut to the world object
	 * @param x A shortcut to the x location
	 * @param y A shortcut to the y location
	 * @param z A shortcut to the z location
	 */
	public void renderStatic(TubeDefinition type, ITube tube, World world, int x, int y, int z);
	
	/**
	 * This is used to render the tube in inventories and in item form
	 * @param type The type of tube to render
	 * @param item The actual item
	 */
	public void renderItem(TubeDefinition type, ItemStack item);
}
