package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.AdvRender;
import schmoller.tubes.ITube;
import schmoller.tubes.definitions.TubeDefinition;

public class NormalTubeRender implements ITubeRender
{
	protected AdvRender mRender = new AdvRender();
	
	@Override
	public boolean renderDynamic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		// Let the default rendering handle it
		return false; 
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
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		if(connections == 3 || connections == 12 || connections == 48)
			renderStraight(connections, type);
		else
		{
			renderCore(connections, type);
			renderConnections(connections, type);
		}
	}
	
	protected void renderStraight(int connections, TubeDefinition def)
	{
		mRender.setIcon(def.getStraightIcon());
		
		if(connections == 3)
		{
			mRender.drawBox(60, 0.25f, 0.0f, 0.25f, 0.75f, 1.0f, 0.75f);
		}
		else if(connections == 12)
		{
			mRender.setTextureRotation(0, 0, 1, 1, 1, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 1.0f);
		}
		else
		{
			mRender.setTextureRotation(1);
			mRender.drawBox(15, 0.0f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
		}
	}
	
	protected void renderCore(int connections, TubeDefinition def)
	{
		mRender.setIcon(def.getCenterIcon());
		mRender.drawBox((~connections) & 63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}
	
	protected void renderConnections(int connections, TubeDefinition def)
	{
		mRender.setIcon(def.getStraightIcon());
		
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
		
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		mRender.drawBox(63, 0.75f, 0.75f, 0.75f, 0.25f, 0.25f, 0.25f);
		
		tes.draw();
	}

}
