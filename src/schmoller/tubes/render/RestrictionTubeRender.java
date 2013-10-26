package schmoller.tubes.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import schmoller.tubes.CommonHelper;
import schmoller.tubes.definitions.TubeDefinition;
import schmoller.tubes.definitions.TypeNormalTube;
import schmoller.tubes.definitions.TypeRestrictionTube;

public class RestrictionTubeRender extends NormalTubeRender
{
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col )
	{
		mRender.setIcon(TypeRestrictionTube.center);
		mRender.drawBox((~connections) & 63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		
		if(col != -1)
		{
			mRender.setIcon(TypeNormalTube.paintCenter);
			mRender.setColorRGB(CommonHelper.getDyeColor(col));
			mRender.drawBox((~connections) & 63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
			mRender.resetColor();
		}
	}
	
	@Override
	protected void renderConnections( int connections, TubeDefinition def )
	{
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				mRender.setIcon(TypeRestrictionTube.center);
				
				int faces = 63 - (1 << (i ^ 1))  - (1 << i);
				
				mRender.resetTextureRotation();
				switch(i)
				{
				case 0: // Down
					mRender.drawBox(faces, 0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
					mRender.setIcon(TypeRestrictionTube.edge, TypeRestrictionTube.edge, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center);
					mRender.drawBox(63, 0.1875f, 0.0625f, 0.1875f, 0.8125f, 0.1875f, 0.8125f);
					break;
				case 1: // Up
					mRender.drawBox(faces, 0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
					mRender.setIcon(TypeRestrictionTube.edge, TypeRestrictionTube.edge, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center);
					mRender.drawBox(63, 0.1875f, 0.8125f, 0.1875f, 0.8125f, 0.9375f, 0.8125f);
					break;
				case 2: // North
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.drawBox(faces, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
					mRender.setIcon(TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.edge, TypeRestrictionTube.edge, TypeRestrictionTube.center, TypeRestrictionTube.center);
					mRender.drawBox(63, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f, 0.1875f);
					break;
				case 3: // South
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.drawBox(faces, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
					mRender.setIcon(TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.edge, TypeRestrictionTube.edge, TypeRestrictionTube.center, TypeRestrictionTube.center);
					mRender.drawBox(63, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f, 0.9375f);
					break;
				case 4: // West
					mRender.setTextureRotation(1);
					mRender.drawBox(faces, 0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
					mRender.setIcon(TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.edge, TypeRestrictionTube.edge);
					mRender.drawBox(63, 0.0625f, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f);
					break;
				case 5: // East
					mRender.setTextureRotation(1);
					mRender.drawBox(faces, 0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
					mRender.setIcon(TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.center, TypeRestrictionTube.edge, TypeRestrictionTube.edge);
					mRender.drawBox(63, 0.8125f, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f);
					break;
				}
			}
		}
		
		mRender.resetTextureRotation();
	}
	
	@Override
	protected void renderStraight( int connections, TubeDefinition def, int cutoff, int col )
	{
		mRender.setIcon(TypeRestrictionTube.straight);
		
		if(connections == 3)
		{
			float min = 0;
			float max = 1;
			
			if((cutoff & 1) != 0)
				min = 0.25f;
			if((cutoff & 2) != 0)
				max = 0.75f;
			
			mRender.drawBox(60, 0.25f, min, 0.25f, 0.75f, max, 0.75f);
			mRender.setIcon(TypeRestrictionTube.edge, TypeRestrictionTube.edge, TypeRestrictionTube.straight, TypeRestrictionTube.straight, TypeRestrictionTube.straight, TypeRestrictionTube.straight);
			if((cutoff & 1) == 0)
				mRender.drawBox(63, 0.1875f, 0.0625f, 0.1875f, 0.8125f, 0.1875f, 0.8125f);
			if((cutoff & 2) == 0)
				mRender.drawBox(63, 0.1875f, 0.8125f, 0.1875f, 0.8125f, 0.9375f, 0.8125f);
			
			if(col != -1)
			{
				mRender.setIcon(TypeRestrictionTube.paintStraight);
				mRender.setColorRGB(CommonHelper.getDyeColor(col));
				mRender.drawBox(60, 0.3125f, min, 0.3125f, 0.6875f, max, 0.6875f);
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
			
			mRender.setIcon(TypeRestrictionTube.straight, TypeRestrictionTube.straight, TypeRestrictionTube.edge, TypeRestrictionTube.edge, TypeRestrictionTube.straight, TypeRestrictionTube.straight);
			if((cutoff & 4) == 0)
				mRender.drawBox(63, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f, 0.1875f);
			if((cutoff & 8) == 0)
				mRender.drawBox(63, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f, 0.9375f);
			
			if(col != -1)
			{
				mRender.setIcon(TypeRestrictionTube.paintStraight);
				mRender.setColorRGB(CommonHelper.getDyeColor(col));
				mRender.drawBox(51, 0.25f, 0.25f, min, 0.75f, 0.75f, max);
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
			
			mRender.setIcon(TypeRestrictionTube.straight, TypeRestrictionTube.straight, TypeRestrictionTube.straight, TypeRestrictionTube.straight, TypeRestrictionTube.edge, TypeRestrictionTube.edge);
			if((cutoff & 16) == 0)
				mRender.drawBox(63, 0.0625f, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f);
			if((cutoff & 32) == 0)
				mRender.drawBox(63, 0.8125f, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f);
			
			
			if(col != -1)
			{
				mRender.setIcon(TypeRestrictionTube.paintStraight);
				mRender.setColorRGB(CommonHelper.getDyeColor(col));
				mRender.drawBox(15, min, 0.25f, 0.25f, max, 0.75f, 0.75f);
			}
		}
		mRender.resetColor();
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
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		mRender.setIcon(TypeRestrictionTube.straight);
		
		mRender.setTextureRotation(0, 0, 1, 1, 1, 1);
		mRender.drawBox(51, 0.25f, 0.25f, 0f, 0.75f, 0.75f, 1f);
		
		mRender.setIcon(TypeRestrictionTube.straight, TypeRestrictionTube.straight, TypeRestrictionTube.edge, TypeRestrictionTube.edge, TypeRestrictionTube.straight, TypeRestrictionTube.straight);
		mRender.drawBox(63, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f, 0.1875f);
		mRender.drawBox(63, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f, 0.9375f);
		
		tes.draw();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
