package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IDirectionalTube;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.definitions.TypeExtractionTube;
import schmoller.tubes.definitions.TypeFilterTube;
import schmoller.tubes.definitions.TypeRequestingTube;
import schmoller.tubes.types.RequestingTube;

import org.lwjgl.opengl.GL11;

public class RequestingTubeRender extends NormalTubeRender
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
		
		int invCons = 0;
		
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				if(InteractionHandler.isInteractable(world, x + ForgeDirection.getOrientation(i).offsetX, y + ForgeDirection.getOrientation(i).offsetY, z + ForgeDirection.getOrientation(i).offsetZ, i) 
					&& TubeHelper.getTubeConnectable(world, x + ForgeDirection.getOrientation(i).offsetX, y + ForgeDirection.getOrientation(i).offsetY, z + ForgeDirection.getOrientation(i).offsetZ) == null)
					invCons |= (1 << i);
			}
		}
		
		int tubeCons = connections - invCons;
		
		renderCore(connections | (1 << direction), type, -1);
		renderConnections(tubeCons, type);
		
		renderInventoryConnections(invCons, type);
		
		renderExtractor(direction);
	}
	
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col )
	{
		mRender.setIcon(TypeRequestingTube.icon);
		mRender.drawBox(~connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		mRender.setIcon(TypeFilterTube.filterOpenIcon);
		mRender.drawBox(connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
	}
	
	private void renderExtractor(int side)
	{
		mRender.setIcon(TypeRequestingTube.icon);
		
		switch(side)
		{
		case 0:
			mRender.drawBox(60, 0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
			mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon);
			mRender.drawBox(63, 0.1875f, 0.0f, 0.1875f, 0.8125f, 0.0625f, 0.8125f);
			break;
		case 1:
			mRender.drawBox(60, 0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
			mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon);
			mRender.drawBox(63, 0.1875f, 0.9375f, 0.1875f, 0.8125f, 1.0f, 0.8125f);
			break;
		case 2:
			mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
			mRender.setIcon(TypeRequestingTube.icon, TypeRequestingTube.icon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeRequestingTube.icon, TypeRequestingTube.icon);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.0625f);
			break;
		case 3:
			mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
			mRender.setIcon(TypeRequestingTube.icon, TypeRequestingTube.icon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeRequestingTube.icon, TypeRequestingTube.icon);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f, 1.0f);
			break;
		case 4:
			mRender.setTextureRotation(1, 1, 1, 1, 0, 0);
			mRender.drawBox(15, 0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
			mRender.setIcon(TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
			mRender.drawBox(63, 0.0f, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f);
			break;
		case 5:
			mRender.setTextureRotation(1, 1, 1, 1, 0, 0);
			mRender.drawBox(15, 0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
			mRender.setIcon(TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeRequestingTube.icon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
			mRender.drawBox(63, 0.9375f, 0.1875f, 0.1875f, 1.0f, 0.8125f, 0.8125f);
			break;
		}
		
		mRender.resetTextureRotation();
	}
	
	private void renderPump(int side, float time)
	{
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TypeExtractionTube.pumpTexture);
		
		mRender.setTextureIndex(0);
		mRender.setTextureSize(1, 1);
		
		Tessellator tes = Tessellator.instance;
		tes.setColorOpaque_F(1, 1, 1);
		
		tes.startDrawingQuads();
		
		float amount = ((float)Math.cos(time * Math.PI * 2) / 2f + 0.5f) * 0.0625f;
		
		mRender.resetTransform();
		switch(side)
		{
		case 0:
			mRender.translate(0, amount, 0);
			mRender.drawBox(63, 0.1875f, 0.0625f, 0.1875f, 0.8125f, 0.125f, 0.8125f);
			break;
		case 1:
			mRender.translate(0, -amount, 0);
			mRender.drawBox(63, 0.1875f, 0.875f, 0.1875f, 0.8125f, 0.9375f, 0.8125f);
			break;
		case 2:
			mRender.translate(0, 0, amount);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f, 0.125f);
			break;
		case 3:
			mRender.translate(0, 0, -amount);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.875f, 0.8125f, 0.8125f, 0.9375f);
			break;
		case 4:
			mRender.translate(amount, 0, 0);
			mRender.drawBox(63, 0.0625f, 0.1875f, 0.1875f, 0.125f, 0.8125f, 0.8125f);
			break;
		case 5:
			mRender.translate(-amount, 0, 0);
			mRender.drawBox(63, 0.875f, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f);
			break;
		}
		
		tes.draw();
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
		
		GL11.glDisable(GL11.GL_CULL_FACE);

		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(55, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		
		renderExtractor(3);
		renderCore(8, type, -1);
		
		tes.draw();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		renderPump(3, ((System.currentTimeMillis() % 1000) / 1000.0f));
	}
	
	
	@Override
	public void renderDynamic( TubeDefinition type, ITube tube, World world, int x, int y, int z, float frameTime )
	{
		int direction = ((IDirectionalTube)tube).getFacing();
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		renderPump(direction, ((RequestingTube)tube).animTime + 0.05f * frameTime);
		
		GL11.glPopMatrix();
		
		super.renderDynamic(type, tube, world, x, y, z, frameTime);
	}
}
