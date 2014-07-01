package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import schmoller.tubes.AdvRender.FaceMode;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IDirectionalTube;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeAdvancedExtractionTube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.types.AdvancedExtractionTube;

import org.lwjgl.opengl.GL11;

public class AdvancedExtractionTubeRender extends NormalTubeRender
{
	private int mDirection;
	@Override
	public void renderStatic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		int connections = tube.getConnections();
		mDirection = ((IDirectionalTube)tube).getFacing();
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.faceMode = FaceMode.Both;
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		int col = tube.getColor();
		
		int invCons = 0;
		
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				if(TubeHelper.renderAsInventoryConnection(world, x, y, z, i))
					invCons |= (1 << i);
			}
		}
		
		int tubeCons = connections - invCons;
		
		renderCore(connections | (1 << mDirection), type, col);
		renderConnections(tubeCons, type);
		
		renderInventoryConnections(invCons, type);
	}
	
	private void renderBody(int direction)
	{
		mRender.setIcon(TypeAdvancedExtractionTube.main);
		IIcon back = TypeAdvancedExtractionTube.backClosed;
		// TODO: If open, render open icon
		//back = TypeAdvancedExtractionTube.backOpen;
		
		switch(direction)
		{
		case 0:
			mRender.setTextureRotation(0, 0, 2, 2, 2, 2);
			mRender.drawBox(60, 0.25f, 0f, 0.25f, 0.75f, 0.3125f, 0.75f);
			mRender.setIcon(TypeAdvancedExtractionTube.backOpen, back, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.25f, 0.1875f, 0.8125f, 0.8125f, 0.8125f); // Body
			mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.0f, 0.1875f, 0.8125f, 0.0625f, 0.8125f); // Ring
			break;
		case 1:
			mRender.setTextureRotation(0, 0, 0, 0, 0, 0);
			mRender.drawBox(60, 0.25f, 0.6875f, 0.25f, 0.75f, 1.0f, 0.75f);
			mRender.setIcon(back, TypeAdvancedExtractionTube.backOpen, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.75f, 0.8125f); // Body
			mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.9375f, 0.1875f, 0.8125f, 1.0f, 0.8125f); // Ring
			break;
		case 2:
			mRender.setTextureRotation(2, 0, 0, 0, 3, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0f, 0.75f, 0.75f, 0.3125f);
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.backOpen, back, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.25f, 0.8125f, 0.8125f, 0.8125f); // Body
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.0625f); // Ring
			break;
		case 3:
			mRender.setTextureRotation(0, 2, 0, 0, 1, 3);
			mRender.drawBox(51, 0.25f, 0.25f, 0.6875f, 0.75f, 0.75f, 1.0f);
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, back, TypeAdvancedExtractionTube.backOpen, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.75f); // Body
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f, 1.0f); // Ring
			break;
		case 4:
			mRender.setTextureRotation(3, 3, 1, 3, 0, 0);
			mRender.drawBox(15, 0f, 0.25f, 0.25f, 0.3125f, 0.75f, 0.75f);
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.backOpen, back);
			mRender.drawBox(63, 0.25f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f); // Body
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
			mRender.drawBox(63, 0f, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f); // Ring
			break;
		case 5:
			mRender.setTextureRotation(1, 1, 3, 1, 0, 0);
			mRender.drawBox(15, 0.6875f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, back, TypeAdvancedExtractionTube.backOpen);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.1875f, 0.75f, 0.8125f, 0.8125f); // Body
			mRender.setIcon(TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeAdvancedExtractionTube.main, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
			mRender.drawBox(63, 0.9375f, 0.1875f, 0.1875f, 1.0f, 0.8125f, 0.8125f); // Ring
			break;
		}
		
		mRender.resetTextureRotation();
	}
	
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col )
	{
		renderBody(mDirection);
	}
	
	private void renderPump(int side, float animTime)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TypeAdvancedExtractionTube.pump);
		
		mRender.setTextureIndex(0);
		mRender.setTextureSize(1, 1);
		
		Tessellator tes = Tessellator.instance;
		tes.setColorOpaque_F(1, 1, 1);
		
		tes.startDrawingQuads();
		
		float amount = ((float)Math.cos(animTime * Math.PI * 2) / 2f + 0.5f) * 0.0625f * 2;
		
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
		mRender.faceMode = FaceMode.Normal;
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(55, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		
		renderBody(3);
		
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
		mRender.faceMode = FaceMode.Both;
		
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		renderPump(direction, ((AdvancedExtractionTube)tube).animTime + 0.05f * frameTime);
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		
		super.renderDynamic(type, tube, world, x, y, z, frameTime);
	}
}
