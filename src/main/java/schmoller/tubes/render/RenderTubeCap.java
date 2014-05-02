package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import schmoller.tubes.AdvRender;
import schmoller.tubes.parts.TubeCap;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RenderTubeCap implements IItemRenderer
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
		Tessellator tes = Tessellator.instance;
		
		mRender.resetLighting(15728880);
		mRender.enableNormals = true;
		mRender.setLocalLights(1, 1, 1, 1, 1, 1);
		mRender.setIcon(TubeCap.icon);
		mRender.resetTransform();
		
		if(type == ItemRenderType.ENTITY)
			mRender.translate(-0.5f, -0.5f, -0.5f);
		
		mRender.translate(0, 0, -0.34375f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		
		tes.setColorOpaque_F(1, 1, 1);
		tes.startDrawingQuads();
		
		mRender.drawBox(63, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 0.9375f);
		
		tes.draw();
	}
	
}
