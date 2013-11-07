package schmoller.tubes.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeCompressorTube;
import schmoller.tubes.definitions.TypeFilterTube;

public class CompressorTubeRender extends NormalTubeRender
{
	@Override
	public void renderDynamic( TubeDefinition type, ITube tube, World world, int x, int y, int z, float frameTime )
	{
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		renderPumps(tube.getConnections());
		
		super.renderDynamic(type, tube, world, x, y, z, frameTime);
	}
	
	private void renderPumps(int connections)
	{
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TypeCompressorTube.pumpTexture);
		
		mRender.setTextureIndex(0);
		mRender.setTextureSize(1, 1);
		
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator tes = Tessellator.instance;
		tes.startDrawingQuads();
		
		float amount = (float)Math.sin(((System.currentTimeMillis() % 2000) / 2000.0f) * Math.PI * 2) / 2.5f + 0.5f;
		
		for(int i = 0; i < 6; ++i)
		{
			if((~connections & (1 << i)) != 0)
			{
				mRender.resetTransform();
				switch(i)
				{
				case 0:
					mRender.translate(0, amount / 8, 0);
					mRender.drawBox(63, 0.375f, 0.0f, 0.375f, 0.625f, 0.125f, 0.625f);
					break;
				case 1:
					mRender.translate(0, -amount / 8, 0);
					mRender.drawBox(63, 0.375f, 0.875f, 0.375f, 0.625f, 1.0f, 0.625f);
					break;
				case 2:
					mRender.translate(0, 0, amount / 8);
					mRender.drawBox(63, 0.375f, 0.375f, 0.0f, 0.625f, 0.625f, 0.125f);
					break;
				case 3:
					mRender.translate(0, 0, -amount / 8);
					mRender.drawBox(63, 0.375f, 0.375f, 0.875f, 0.625f, 0.625f, 1.0f);
					break;
				case 4:
					mRender.translate(amount / 8, 0, 0);
					mRender.drawBox(63, 0.0f, 0.375f, 0.375f, 0.125f, 0.625f, 0.625f);
					break;
				case 5:
					mRender.translate(-amount / 8, 0, 0);
					mRender.drawBox(63, 0.875f, 0.375f, 0.375f, 1.0f, 0.625f, 0.625f);
					break;
				}
				
			}
		}
		
		tes.draw();
		
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col )
	{
		mRender.resetTextureRotation();
		
		mRender.setIcon(TypeFilterTube.filterOpenIcon);
		mRender.drawBox(connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		
		mRender.setIcon(TypeCompressorTube.compressorIcon);
		mRender.drawBox(~connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		
		for(int i = 0; i < 6; ++i)
		{
			if((~connections & (1 << i)) != 0)
			{
				switch(i)
				{
				case 0:
					mRender.drawBox(61, 0.3125f, 0.125f, 0.3125f, 0.6875f, 0.1875f, 0.6875f);
					break;
				case 1:
					mRender.drawBox(62, 0.3125f, 0.8125f, 0.3125f, 0.6875f, 0.875f, 0.6875f);
					break;
				case 2:
					mRender.drawBox(55, 0.3125f, 0.3125f, 0.125f, 0.6875f, 0.6875f, 0.1875f);
					break;
				case 3:
					mRender.drawBox(59, 0.3125f, 0.3125f, 0.8125f, 0.6875f, 0.6875f, 0.875f);
					break;
				case 4:
					mRender.drawBox(31, 0.125f, 0.3125f, 0.3125f, 0.1875f, 0.6875f, 0.6875f);
					break;
				case 5:
					mRender.drawBox(47, 0.8125f, 0.3125f, 0.3125f, 0.875f, 0.6875f, 0.6875f);
					break;
				}
			}
		}
	}
	
	@Override
	protected void renderStraight( int connections, TubeDefinition def, int cutoff, int col )
	{
		super.renderStraight(connections, def, cutoff, col);
		
		renderCore(connections, def, col);
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
		
		renderCore(0, type, -1);
		tes.draw();
		
		renderPumps(0);
	}
}
