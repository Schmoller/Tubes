package schmoller.tubes.render;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.ITube;
import schmoller.tubes.definitions.TubeDefinition;

public interface ITubeRender
{
	public boolean renderDynamic(TubeDefinition type, ITube tube, World world, int x, int y, int z);
	
	public void renderStatic(TubeDefinition type, ITube tube, World world, int x, int y, int z);
	
	public void renderItem(TubeDefinition type, ItemStack item);
}
