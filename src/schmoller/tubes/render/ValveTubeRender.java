package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.interfaces.IDirectionalTube;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeEjectionTube;

public class ValveTubeRender extends NormalTubeRender
{
	@Override
	public void renderStatic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		int connections = tube.getConnections();
		int direction = ((IDirectionalTube)tube).getFacing();
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		int col = tube.getColor();
		
		renderCore(connections | (1 << direction), type, col);
		renderConnections(connections, type);
		
		renderInventoryConnections(1 << direction, type);
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
		
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		mRender.drawBox(63, 0.75f, 0.75f, 0.75f, 0.25f, 0.25f, 0.25f);
		
		mRender.setIcon(TypeEjectionTube.funnelIcon);
		mRender.drawBox(51, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
		mRender.drawBox(51, 0.75f, 0.75f, 1.0f, 0.25f, 0.25f, 0.75f);
		mRender.setIcon(TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon);
		mRender.drawBox(63, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f, 1.0f);
		mRender.drawBox(63, 0.8125f, 0.8125f, 1.0f, 0.1875f, 0.1875f, 0.9375f);
		
		tes.draw();
	}
}
