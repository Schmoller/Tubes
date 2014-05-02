package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.FluidExtractionTube;

public class TypeFluidExtractionTube extends TubeDefinition
{
	public static IIcon icon;
	public static ResourceLocation pumpTexture = new ResourceLocation("tubes", "textures/models/tube-extractor-pump.png");
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		icon = register.registerIcon("Tubes:tube-fluid-extraction");
	}
	
	@Override
	public IIcon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}

	@Override
	public IIcon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}

	@Override
	public BaseTube createTube()
	{
		return new FluidExtractionTube();
	}
}
