package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import schmoller.tubes.AdvRender;
import schmoller.tubes.parts.BaseTubePart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RenderTubeItem implements IItemRenderer
{
	private AdvRender mRender = new AdvRender();
	
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
		mRender.resetTransform();
		mRender.enableNormals = true;
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.resetLighting(15728880);
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture("/terrain.png");
		tes.startDrawingQuads();
		
		mRender.setIcon(BaseTubePart.center);
		mRender.drawBox(63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		mRender.drawBox(63, 0.75f, 0.75f, 0.75f, 0.25f, 0.25f, 0.25f);
		
		tes.draw();
	}

}
