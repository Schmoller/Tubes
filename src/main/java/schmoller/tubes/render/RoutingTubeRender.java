package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import schmoller.tubes.AdvRender.FaceMode;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeFilterTube;
import schmoller.tubes.definitions.TypeRoutingTube;

public class RoutingTubeRender extends NormalTubeRender
{
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
		mRender.faceMode = FaceMode.Both;
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		int col = tube.getColor();
		
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
		
		renderCore(connections, type, col);
		
		renderConnections(tubeCons, type);
		renderInventoryConnections(invCons, type);
	}
	
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col )
	{
		mRender.setIcon(TypeRoutingTube.center);
		mRender.drawBox(~connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		mRender.setIcon(TypeFilterTube.filterOpenIcon);
		mRender.drawBox(connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
	}
	
	@Override
	protected void renderConnections( int connections, TubeDefinition def )
	{
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				mRender.resetTextureRotation();
				mRender.setIcon(TypeRoutingTube.center);
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
				mRender.setIcon(TypeRoutingTube.colours);
				mRender.setColorRGB(CommonHelper.getDyeColor(TypeRoutingTube.sideColours[i]));
				mRender.outset(0.001f);
				mRender.clamp();
				mRender.drawFaces(63 - (1 << (i ^ 1))  - (1 << i));
				mRender.resetColor();
			}
		}
		
		mRender.resetTextureRotation();
	}
	
	@Override
	protected void renderInventoryConnections( int connections, TubeDefinition def )
	{
		super.renderInventoryConnections(connections, def);
		mRender.setIcon(TypeRoutingTube.colours);
		
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
				
				mRender.setColorRGB(CommonHelper.getDyeColor(TypeRoutingTube.sideColours[i]));
				mRender.drawFaces(63 - (1 << (i ^ 1))  - (1 << i));
				mRender.resetColor();
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
		mRender.faceMode = FaceMode.Normal;
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		mRender.setIcon(TypeRoutingTube.center);
		mRender.drawBox(63, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		
		tes.draw();
	}
}
