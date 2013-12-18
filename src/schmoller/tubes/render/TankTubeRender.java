package schmoller.tubes.render;

import org.lwjgl.opengl.GL11;

import codechicken.lib.render.CCRenderState;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeTankTube;
import schmoller.tubes.types.TankTube;

public class TankTubeRender extends NormalTubeRender
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
		
		renderCore(connections, type, -1);
		
		renderConnections(connections, type);
	}
	
	
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col )
	{
		mRender.setIcon(TypeTankTube.coreIcon);
		mRender.drawBox(~connections & 60, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		
		mRender.setIcon(TypeTankTube.coreOpenIcon);
		mRender.drawBox(~connections & 3, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		
		mRender.setIcon(TypeTankTube.coreOpenIcon);
		mRender.drawBox(connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
	}
	
	@Override
	public void renderDynamic( TubeDefinition type, ITube tube, World world, int x, int y, int z, float frameTime )
	{
		FluidStack fluid = ((TankTube)tube).getFluid();
		
		if(fluid != null)
		{
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);

			mRender.resetTransform();
			mRender.enableNormals = false;
			mRender.setLightingFromBlock(world, x, y, z);
			mRender.resetTextureFlip();
			mRender.resetTextureRotation();
			mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
			
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y, z);
			
			CCRenderState.changeTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(fluid.getFluid().getSpriteNumber()));
			
			mRender.setIcon(fluid.getFluid().getIcon(fluid));
			
			float amount = fluid.amount / 1000f;
			if(amount > 1)
				amount = 1;
			
			
			Tessellator tes = Tessellator.instance;
			tes.startDrawingQuads();
			mRender.drawBox(63, 0.188f, 0.188f, 0.188f, 0.812f, 0.188f + (0.624f * amount), 0.812f);
			tes.draw();
			
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		
		super.renderDynamic(type, tube, world, x, y, z, frameTime);
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
	}
}
