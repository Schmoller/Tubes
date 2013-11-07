package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeColoringTube;

public class ColoringTubeRender extends NormalTubeRender
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
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		int col = tube.getColor();
		
		int invCons = 0;
		
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				TileEntity tile = world.getBlockTileEntity(x + ForgeDirection.getOrientation(i).offsetX, y + ForgeDirection.getOrientation(i).offsetY, z + ForgeDirection.getOrientation(i).offsetZ);
				
				if(tile instanceof IInventory && TubeHelper.getTubeConnectable(tile) == null)
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
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) == 0)
			{
				switch(i)
				{
				case 0:
					mRender.setupBox(0.25f, 0.1875f, 0.25f, 0.75f, 0.25f, 0.75f);
					break;
				case 1:
					mRender.setupBox(0.25f, 0.75f, 0.25f, 0.75f, 0.8125f, 0.75f);
					break;
				case 2:
					mRender.setupBox(0.25f, 0.25f, 0.1875f, 0.75f, 0.75f, 0.25f);
					break;
				case 3:
					mRender.setupBox(0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 0.8125f);
					break;
				case 4:
					mRender.setupBox(0.1875f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
					break;
				case 5:
					mRender.setupBox(0.75f, 0.25f, 0.25f, 0.8125f, 0.75f, 0.75f);
					break;
				}
				
				mRender.setIcon(TypeColoringTube.center);
				mRender.drawFaces(63);
				
				if(col != -1)
				{
					mRender.setIcon(TypeColoringTube.paint);
					mRender.setColorRGB(CommonHelper.getDyeColor(col));
					mRender.drawFaces(1 << i);
					mRender.resetColor();
				}
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
		mRender.resetColor();
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		renderCore(0, type, (int)((System.currentTimeMillis() / 1000) % 16));
		
		tes.draw();
	}
}
