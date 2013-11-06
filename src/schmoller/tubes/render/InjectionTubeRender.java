package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeInjectionTube;

public class InjectionTubeRender extends NormalTubeRender
{
	@Override
	public boolean renderDynamic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		return false;
	}
	
	@Override
	public void renderStatic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		super.renderStatic(type, tube, world, x, y, z);
		
		mRender.setIcon(TypeInjectionTube.coreIcon);
		mRender.drawBox(63, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
	}
	
	@Override
	public void renderItem( TubeDefinition type, ItemStack item )
	{
		mRender.resetTransform();
		mRender.enableNormals = true;
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.resetLighting(15728880);
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		mRender.setIcon(TypeInjectionTube.coreIcon);
		mRender.drawBox(63, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		
		tes.draw();
	}
}
