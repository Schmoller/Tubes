package schmoller.tubes.render;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.helpers.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

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
		GL11.glPushMatrix();
		if(type == ItemRenderType.ENTITY)
			GL11.glTranslated(-0.5,-0.5, -0.5);
		RenderHelper.renderItem(item, TubeRegistry.instance().getDefinition(ModTubes.itemTube.getTubeType(item)));
		GL11.glPopMatrix();
	}

}
