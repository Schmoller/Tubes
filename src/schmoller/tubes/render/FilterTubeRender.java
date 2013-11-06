package schmoller.tubes.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.definitions.TypeFilterTube;

public class FilterTubeRender extends NormalTubeRender
{
	@Override
	public void renderStatic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		super.renderStatic(type, tube, world, x, y, z);
	}
	
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col )
	{
		mRender.resetTextureRotation();
		
		mRender.setIcon(TypeFilterTube.filterIcon);
		mRender.drawBox(~connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
		mRender.setIcon(TypeFilterTube.filterOpenIcon);
		mRender.drawBox(connections, 0.1875f, 0.1875f, 0.1875f, 0.8125f, 0.8125f, 0.8125f);
	}
	
	private void renderExtraParts(int connections)
	{
		mRender.resetTextureRotation();
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				switch(i)
				{
				case 0:
					mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon);
					mRender.drawBox(63, 0.1875f, 0.0625f, 0.1875f, 0.8125f, 0.125f, 0.8125f);
					break;
				case 1:
					mRender.setIcon(TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon);
					mRender.drawBox(63, 0.1875f, 0.875f, 0.1875f, 0.8125f, 0.9375f, 0.8125f);
					break;
				case 2:
					mRender.setIcon(TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon);
					mRender.drawBox(63, 0.1875f, 0.1875f, 0.0625f, 0.8125f, 0.8125f, 0.125f);
					break;
				case 3:
					mRender.setIcon(TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon);
					mRender.drawBox(63, 0.1875f, 0.1875f, 0.875f, 0.8125f, 0.8125f, 0.9375f);
					break;
				case 4:
					mRender.setIcon(TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
					mRender.drawBox(63, 0.0625f, 0.1875f, 0.1875f, 0.125f, 0.8125f, 0.8125f);
					break;
				case 5:
					mRender.setIcon(TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeFilterTube.filterIcon, TypeEjectionTube.endIcon, TypeEjectionTube.endIcon);
					mRender.drawBox(63, 0.875f, 0.1875f, 0.1875f, 0.9375f, 0.8125f, 0.8125f);
					break;
				}
			}
		}
	}
	
	@Override
	protected void renderConnections( int connections, TubeDefinition def )
	{
		super.renderConnections(connections, def);
		
		renderExtraParts(connections);
	}
	
	@Override
	protected void renderStraight( int connections, TubeDefinition def, int cutoff, int col )
	{
		super.renderStraight(connections, def, cutoff, col);
		
		renderExtraParts(connections);
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
		
		FMLClientHandler.instance().getClient().renderGlobal.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		renderCore(0, type, -1);
		
		tes.draw();
	}
}
