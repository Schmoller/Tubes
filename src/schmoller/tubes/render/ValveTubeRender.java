package schmoller.tubes.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IDirectionalTube;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.definitions.TypeValveTube;
import schmoller.tubes.types.ValveTube;

public class ValveTubeRender extends NormalTubeRender
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
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		connections -= (connections & (1 << direction));
		
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
		int col = tube.getColor();
		
		renderCore(connections | (1 << direction), type, col);
		renderConnections(tubeCons, type);
		
		renderInventoryConnections(invCons, type);
		
		renderValve(direction, ((ValveTube)tube).isOpen());
	}
	
	private void renderValve(int side, boolean open)
	{
		mRender.setIcon(TypeValveTube.valve);
		
		switch(side)
		{
		case 0:
			mRender.drawBox(60, 0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
			mRender.setIcon(TypeValveTube.valveEdge, TypeValveTube.valveEdge, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve);
			mRender.drawBox(63, 0.1875f, 0.0f, 0.1875f, 0.8125f, 0.0625f, 0.8125f);
			mRender.drawBox(63, 0.1875f, 0.125f, 0.1875f, 0.8125f, 0.1875f, 0.8125f);
			mRender.setIcon((open ? TypeValveTube.valveOpen : TypeValveTube.valveClosed));
			mRender.drawBox(63, 0.125f, 0.0625f, 0.125f, 0.875f, 0.125f, 0.875f);
			break;
		case 1:
			mRender.drawBox(60, 0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
			mRender.setIcon(TypeValveTube.valveEdge, TypeValveTube.valveEdge, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve);
			mRender.drawBox(63, 0.1875f, 0.9375f, 0.1875f, 0.8125f, 1.0f, 0.8125f);
			mRender.drawBox(63, 0.1875f, 0.8125f, 0.1875f, 0.8125f, 0.875f, 0.8125f);
			mRender.setIcon((open ? TypeValveTube.valveOpen : TypeValveTube.valveClosed));
			mRender.drawBox(63, 0.125f, 0.875f, 0.125f, 0.875f, 0.9375f, 0.875f);
			break;
		case 2:
			mRender.setTextureRotation(0, 0, 0, 0, 3, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
			mRender.setIcon(TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valveEdge, TypeValveTube.valveEdge, TypeValveTube.valve, TypeValveTube.valve);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.0625f);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.125f, 0.8125f, 0.8125f, 0.1875f);
			mRender.setIcon((open ? TypeValveTube.valveOpen : TypeValveTube.valveClosed));
			mRender.drawBox(63, 0.125f, 0.125f, 0.0625f, 0.875f, 0.875f, 0.125f);
			break;
		case 3:
			mRender.setTextureRotation(0, 0, 0, 0, 3, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
			mRender.setIcon(TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valveEdge, TypeValveTube.valveEdge, TypeValveTube.valve, TypeValveTube.valve);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f, 1.0f);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f, 0.875f);
			mRender.setIcon((open ? TypeValveTube.valveOpen : TypeValveTube.valveClosed));
			mRender.drawBox(63, 0.125f, 0.125f, 0.875f, 0.875f, 0.875f, 0.9375f);
			break;
		case 4:
			mRender.setTextureRotation(1, 1, 1, 1, 0, 0);
			mRender.drawBox(15, 0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
			mRender.setIcon(TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valveEdge, TypeValveTube.valveEdge);
			mRender.drawBox(63, 0.0f, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f);
			mRender.drawBox(63, 0.125f, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f);
			mRender.setIcon((open ? TypeValveTube.valveOpen : TypeValveTube.valveClosed));
			mRender.drawBox(63, 0.0625f, 0.125f, 0.125f, 0.125f, 0.875f, 0.875f);
			break;
		case 5:
			mRender.setTextureRotation(1, 1, 1, 1, 0, 0);
			mRender.drawBox(15, 0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
			mRender.setIcon(TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valve, TypeValveTube.valveEdge, TypeValveTube.valveEdge);
			mRender.drawBox(63, 0.9375f, 0.1875f, 0.1875f, 1.0f, 0.8125f, 0.8125f);
			mRender.drawBox(63, 0.8125f, 0.1875f, 0.1875f, 0.875f, 0.8125f, 0.8125f);
			mRender.setIcon((open ? TypeValveTube.valveOpen : TypeValveTube.valveClosed));
			mRender.drawBox(63, 0.875f, 0.125f, 0.125f, 0.9375f, 0.875f, 0.875f);
			break;
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
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		
		renderValve(3, false);
		
		tes.draw();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
