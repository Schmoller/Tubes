package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.IDirectionalTube;
import schmoller.tubes.ITube;
import schmoller.tubes.definitions.EjectionTube;
import schmoller.tubes.definitions.TubeDefinition;

public class EjectionTubeRender extends NormalTubeRender
{
	private int mDir;
	@Override
	public void renderStatic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		int direction = ((IDirectionalTube)tube).getFacing();
		mDir = direction;
		
		super.renderStatic(type, tube, world, x, y, z);
		
		mRender.setIcon(EjectionTube.funnelIcon);
		switch(direction)
		{
		case 0:
			mRender.drawBox(60, 0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
			mRender.setIcon(EjectionTube.endIcon, EjectionTube.endIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon);
			mRender.drawBox(63, 0.1875f, 0.0f, 0.1875f, 0.8125f, 0.0625f, 0.8125f);
			break;
		case 1:
			mRender.drawBox(60, 0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
			mRender.setIcon(EjectionTube.endIcon, EjectionTube.endIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon);
			mRender.drawBox(63, 0.1875f, 0.9375f, 0.1875f, 0.8125f, 1.0f, 0.8125f);
			break;
		case 2:
			mRender.drawBox(51, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
			mRender.setIcon(EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.endIcon, EjectionTube.endIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.0625f);
			break;
		case 3:
			mRender.drawBox(51, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
			mRender.setIcon(EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.endIcon, EjectionTube.endIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon);
			mRender.drawBox(63, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f, 1.0f);
			break;
		case 4:
			mRender.drawBox(15, 0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
			mRender.setIcon(EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.endIcon, EjectionTube.endIcon);
			mRender.drawBox(63, 0.0f, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f);
			break;
		case 5:
			mRender.drawBox(15, 0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
			mRender.setIcon(EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.endIcon, EjectionTube.endIcon);
			mRender.drawBox(63, 0.9375f, 0.1875f, 0.1875f, 1.0f, 0.8125f, 0.8125f);
			break;
		}
	}
	
	@Override
	protected void renderCore( int connections, TubeDefinition def )
	{
		connections |= (1 << mDir);
		super.renderCore(connections, def);
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
		
		mRender.translate(-0.5f, -0.5f, -0.5f);
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		mRender.drawBox(63, 0.75f, 0.75f, 0.75f, 0.25f, 0.25f, 0.25f);
		
		mRender.setIcon(EjectionTube.funnelIcon);
		mRender.drawBox(51, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
		mRender.drawBox(51, 0.75f, 0.75f, 1.0f, 0.25f, 0.25f, 0.75f);
		mRender.setIcon(EjectionTube.funnelIcon, EjectionTube.funnelIcon, EjectionTube.endIcon, EjectionTube.endIcon, EjectionTube.funnelIcon, EjectionTube.funnelIcon);
		mRender.drawBox(63, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f, 1.0f);
		mRender.drawBox(63, 0.8125f, 0.8125f, 1.0f, 0.1875f, 0.1875f, 0.9375f);
		
		tes.draw();
	}
}
