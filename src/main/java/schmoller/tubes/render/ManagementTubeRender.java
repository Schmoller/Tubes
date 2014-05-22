package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.AdvRender.FaceMode;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.interfaces.IDirectionalTube;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeManagementTube;

public class ManagementTubeRender extends NormalTubeRender
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
		mRender.faceMode = FaceMode.Normal;
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		int col = tube.getColor();
		
		renderBody(direction);
		mRender.faceMode = FaceMode.Both;
		renderConnections(connections, type);
	}
	
	public void renderBody(int direction)
	{
		mRender.setIcon(TypeManagementTube.main);
		mRender.setIcon(direction, TypeManagementTube.invEnd);
		mRender.setIcon(direction ^ 1, TypeManagementTube.back);
		switch(direction)
		{
		case 0:
			mRender.setTextureRotation(0, 0, 2, 2, 2, 2);
			mRender.setupBox(0.125f, 0.0f, 0.125f, 0.875f, 0.8125f, 0.875f);
			break;
		case 1:
			mRender.setupBox(0.125f, 0.1875f, 0.125f, 0.875f, 1.0f, 0.875f);
			break;
		case 2:
			mRender.setTextureRotation(2, 0, 0, 0, 3, 1);
			mRender.setupBox(0.125f, 0.125f, 0.0f, 0.875f, 0.875f, 0.8125f);
			break;
		case 3:
			mRender.setTextureRotation(0, 2, 0, 0, 1, 3);
			mRender.setupBox(0.125f, 0.125f, 0.1875f, 0.875f, 0.875f, 1.0f);
			break;
		case 4:
			mRender.setTextureRotation(3, 3, 1, 3, 0, 0);
			mRender.setupBox(0.0f, 0.125f, 0.125f, 0.8125f, 0.875f, 0.875f);
			break;
		case 5:
			mRender.setTextureRotation(1, 1, 3, 1, 0, 0);
			mRender.setupBox(0.1875f, 0.125f, 0.125f, 1.0f, 0.875f, 0.875f);
			break;
		}
		
		mRender.drawFaces(63);
		mRender.resetTextureRotation();
	}
	
	@Override
	protected void renderConnections( int connections, TubeDefinition def )
	{
		mRender.setIcon(TypeManagementTube.main);
		
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
					mRender.setTextureRotation(0, 0, 2, 2, 2, 2);
					mRender.setupBox(0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
					break;
				case 2: // North
					mRender.setTextureRotation(0, 2, 0, 0, 1, 3);
					mRender.setupBox(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
					break;
				case 3: // South
					mRender.setTextureRotation(2, 0, 0, 0, 3, 1);
					mRender.setupBox(0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
					break;
				case 4: // West
					mRender.setTextureRotation(1, 1, 3, 1, 0, 0);
					mRender.setupBox(0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
					break;
				case 5: // East
					mRender.setTextureRotation(3, 3, 1, 3, 0, 0);
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
		mRender.faceMode = FaceMode.Normal;
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		renderBody(3);
		
		tes.draw();
	}
}
