package schmoller.tubes.render;

import schmoller.tubes.ModTubes;
import schmoller.tubes.TubeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RenderTubeItem implements IItemRenderer
{
	@Override
	public boolean handleRenderType( ItemStack item, ItemRenderType type )
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper( ItemRenderType type, ItemStack item, ItemRendererHelper helper )
	{
		return true;
	}

	@Override
	public void renderItem( ItemRenderType type, ItemStack item, Object... data )
	{
		RenderHelper.renderItem(item, TubeRegistry.instance().getDefinition(ModTubes.itemTube.getTubeType(item)));
	}

}
