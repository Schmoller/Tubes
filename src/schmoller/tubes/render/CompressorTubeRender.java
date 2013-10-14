package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.ITube;
import schmoller.tubes.definitions.TypeCompressorTube;
import schmoller.tubes.definitions.TypeFilterTube;
import schmoller.tubes.definitions.TubeDefinition;

public class CompressorTubeRender extends NormalTubeRender
{
	@Override
	public boolean renderDynamic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		renderPumps(tube.getConnections());
		
		return false;
	}
	
	private void renderPumps(int connections)
	{
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture("/mods/Tubes/textures/models/tube-compressor-pumps.png");
		
		mRender.setTextureIndex(0);
		mRender.setTextureSize(1, 1);
		
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
		
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture("/terrain.png");
		tes.startDrawingQuads();
		
		renderCore(0, type, -1);
		tes.draw();
		
		renderPumps(0);
	}
}
