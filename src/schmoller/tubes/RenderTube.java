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
		
		GL11.glCullFace(GL11.GL_FRONT);
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		//mRender.resetLighting(block.getMixedBrightnessForBlock(world, x, y, z));
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		renderCore(connections);
		renderConnections(connections);
		
		GL11.glCullFace(GL11.GL_BACK);
		
		return false;
	}
	
	private void renderCore(int connections)
	{
		mRender.setIcon(BlockTube.center);
		mRender.drawBox((~connections) & 63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
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
