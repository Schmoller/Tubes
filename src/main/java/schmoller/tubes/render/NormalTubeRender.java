package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.AdvRender;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.client.ITubeRender;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.definitions.TypeNormalTube;

public class NormalTubeRender implements ITubeRender
{
	protected AdvRender mRender = new AdvRender();
	
	@Override
	public void renderDynamic( TubeDefinition type, ITube tube, World world, int x, int y, int z, float frameTime)
	{
		RenderHelper.renderTubeItems(tube, x, y, z, frameTime); 
	}

	@Override
	public void renderStatic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		int connections = tube.getConnections();
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.resetColor();
		
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
		
		if(connections == 3 || connections == 12 || connections == 48)
		{
			renderStraight(connections, type, invCons, col);
			renderInventoryConnections(invCons, type);
		}
		else
		{
			renderCore(connections, type, col);
			
			renderConnections(tubeCons, type);
			renderInventoryConnections(invCons, type);
		}
	}
	
	protected void renderStraight(int connections, TubeDefinition def, int cutoff, int col)
	{
		mRender.setIcon(def.getStraightIcon());
		
		if(connections == 3)
		{
			float min = 0;
			float max = 1;
			
			if((cutoff & 1) != 0)
				min = 0.25f;
			if((cutoff & 2) != 0)
				max = 0.75f;
			
			mRender.drawBox(60, 0.25f, min, 0.25f, 0.75f, max, 0.75f);
			if(col != -1)
			{
				mRender.setIcon(TypeNormalTube.paintStraight);
				mRender.setColorRGB(CommonHelper.getDyeColor(col));
				mRender.setupBox(0.25f, min, 0.25f, 0.75f, max, 0.75f);
				mRender.outset(0.001f);
				mRender.drawFaces(60);
			}
		}
		else if(connections == 12)
		{
			float min = 0;
			float max = 1;
			
			if((cutoff & 4) != 0)
				min = 0.25f;
			if((cutoff & 8) != 0)
				max = 0.75f;
			
			mRender.setTextureRotation(0, 0, 1, 1, 1, 1);
			mRender.drawBox(51, 0.25f, 0.25f, min, 0.75f, 0.75f, max);
			if(col != -1)
			{
				mRender.setIcon(TypeNormalTube.paintStraight);
				mRender.setColorRGB(CommonHelper.getDyeColor(col));
				mRender.setupBox(0.25f, 0.25f, min, 0.75f, 0.75f, max);
				mRender.outset(0.001f);
				mRender.drawFaces(51);
			}
		}
		else
		{
			float min = 0;
			float max = 1;
			
			if((cutoff & 16) != 0)
				min = 0.25f;
			if((cutoff & 32) != 0)
				max = 0.75f;
			mRender.setTextureRotation(1);
			mRender.drawBox(15, min, 0.25f, 0.25f, max, 0.75f, 0.75f);
			if(col != -1)
			{
				mRender.setIcon(TypeNormalTube.paintStraight);
				mRender.setColorRGB(CommonHelper.getDyeColor(col));
				mRender.setupBox(min, 0.25f, 0.25f, max, 0.75f, 0.75f);
				mRender.outset(0.001f);
				mRender.drawFaces(15);
			}
		}
		mRender.resetColor();
		mRender.resetTextureRotation();
	}
	
	protected void renderCore(int connections, TubeDefinition def, int col)
	{
		mRender.setIcon(def.getCenterIcon());
		mRender.drawBox((~connections) & 63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		
		if(col != -1)
		{
			mRender.setIcon(TypeNormalTube.paintCenter);
			mRender.setColorRGB(CommonHelper.getDyeColor(col));
			mRender.setupBox(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
			mRender.outset(0.001f);
			mRender.drawFaces((~connections) & 63);
			mRender.resetColor();
		}
	}
	
	protected void renderInventoryConnections(int connections, TubeDefinition def)
	{
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				mRender.setIcon(TypeEjectionTube.funnelIcon);
				switch(i)
				{
				case 0:
					mRender.drawBox(60, 0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
					mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon);
					mRender.drawBox(63, 0.1875f, 0.0f, 0.1875f, 0.8125f, 0.0625f, 0.8125f);
					break;
				case 1:
					mRender.drawBox(60, 0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
					mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon);
					mRender.drawBox(63, 0.1875f, 0.9375f, 0.1875f, 0.8125f, 1.0f, 0.8125f);
					break;
				case 2:
					mRender.drawBox(51, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
					mRender.setIcon(TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon);
					mRender.drawBox(63, 0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.0625f);
					break;
				case 3:
					mRender.drawBox(51, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
					mRender.setIcon(TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon);
					mRender.drawBox(63, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f, 1.0f);
					break;
				case 4:
					mRender.drawBox(15, 0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
					mRender.setIcon(TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
					mRender.drawBox(63, 0.0f, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f);
					break;
				case 5:
					mRender.drawBox(15, 0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
					mRender.setIcon(TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.funnelIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
					mRender.drawBox(63, 0.9375f, 0.1875f, 0.1875f, 1.0f, 0.8125f, 0.8125f);
					break;
				}
			}
		}
	}
	
	protected void renderConnections(int connections, TubeDefinition def)
	{
		mRender.setIcon(def.getCenterIcon());
		
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				mRender.resetTextureRotation();
				switch(i)
				{
				case 0: // Down
					mRender.setupBox(0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
					break;
				case 1: // Up
					mRender.setupBox(0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
					break;
				case 2: // North
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
					break;
				case 3: // South
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox(0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
					break;
				case 4: // West
					mRender.setTextureRotation(1);
					mRender.setupBox(0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
					break;
				case 5: // East
					mRender.setTextureRotation(1);
					mRender.setupBox(0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
					break;
				}
				
				mRender.drawFaces(63 - (1 << (i ^ 1))  - (1 << i));
			}
		}
		
		mRender.resetTextureRotation();
	}

	@Override
	public void renderItem( TubeDefinition type, ItemStack item )
	{
		mRender.resetTransform();
		mRender.enableNormals = true;
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.resetLighting(15728880);
		mRender.resetColor();
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		
		tes.draw();
	}

}
