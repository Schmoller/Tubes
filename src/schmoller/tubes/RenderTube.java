package schmoller.tubes;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import org.lwjgl.opengl.GL11;

public class RenderTube implements ISimpleBlockRenderingHandler
{
	public static int renderId = -1;
	private AdvRender mRender = new AdvRender();
	
	public RenderTube()
	{
		if(renderId == -1)
			renderId = RenderingRegistry.getNextAvailableRenderId();
	}
	
	@Override
	public void renderInventoryBlock( Block block, int metadata, int modelID, RenderBlocks renderer )
	{
		
	}

	@Override
	public boolean renderWorldBlock( IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer )
	{
		TileTube tube = (TileTube)world.getBlockTileEntity(x, y, z);
		
		int connections = TubeHelper.getConnectivity(world, x, y, z);
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		if(connections == 3 || connections == 12 || connections == 48)
			renderStraight(connections);
		else
		{
			renderCore(connections);
			renderConnections(connections);
		}

		
		return true;
	}
	
	private void renderStraight(int connections)
	{
		mRender.setIcon(BlockTube.straight);
		
		if(connections == 3)
		{
			mRender.drawBox(60, 0.25f, 0.0f, 0.25f, 0.75f, 1.0f, 0.75f);
			mRender.drawBox(60, 0.75f, 1.0f, 0.75f, 0.25f, 0.0f, 0.25f);
		}
		else if(connections == 12)
		{
			mRender.setTextureRotation(0, 0, 1, 1, 1, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 1.0f);
			mRender.drawBox(51, 0.75f, 0.75f, 1.0f, 0.25f, 0.25f, 0.0f);
		}
		else
		{
			mRender.setTextureRotation(1);
			mRender.drawBox(15, 0.0f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
			mRender.drawBox(15, 1.0f, 0.75f, 0.75f, 0.0f, 0.25f, 0.25f);
		}
	}
	
	private void renderCore(int connections)
	{
		mRender.setIcon(BlockTube.center);
		mRender.drawBox((~connections) & 63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);

		int invCon = (~connections) & 63;;
		invCon = (invCon & 0xF) | (invCon & 0x20) >> 1 | (invCon & 0x10) << 1;
		invCon = (invCon & 0x33) | (invCon & 0x8) >> 1 | (invCon & 0x4) << 1;
		invCon = (invCon & 0x3C) | (invCon & 0x2) >> 1 | (invCon & 0x1) << 1;
		
		mRender.drawBox(invCon, 0.75f, 0.75f, 0.75f, 0.25f, 0.25f, 0.25f);
	}
	
	private void renderConnections(int connections)
	{
		mRender.setIcon(BlockTube.straight);
		
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
				
				switch(i)
				{
				case 0: // Down
					mRender.setupBox(0.75f, 0.25f, 0.75f, 0.25f, 0.0f, 0.25f);
					break;
				case 1: // Up
					mRender.setupBox(0.75f, 1.0f, 0.75f, 0.25f, 0.75f, 0.25f);
					break;
				case 2: // North
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox(0.75f, 0.75f, 0.25f, 0.25f, 0.25f, 0.0f);
					break;
				case 3: // South
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox( 0.75f, 0.75f, 1.0f, 0.25f, 0.25f, 0.75f);
					break;
				case 4: // West
					mRender.setTextureRotation(1);
					mRender.setupBox(0.25f, 0.75f, 0.75f, 0.0f, 0.25f, 0.25f);
					break;
				case 5: // East
					mRender.setTextureRotation(1);
					mRender.setupBox(1.0f, 0.75f, 0.75f, 0.75f, 0.25f, 0.25f);
					break;
				}
				
				mRender.drawFaces(63 - (1 << (i ^ 1))  - (1 << i));
			}
		}
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return false;
	}

	@Override
	public int getRenderId()
	{
		return renderId;
	}

}
