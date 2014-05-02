package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.CompressorTube;

public class TypeCompressorTube extends TubeDefinition
{
	public static IIcon compressorIcon;
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/compressorTube.png");
	public static ResourceLocation pumpTexture = new ResourceLocation("tubes", "textures/models/tube-compressor-pumps.png");
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		compressorIcon = register.registerIcon("Tubes:tube-compressor-center");
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
		return new CompressorTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}

}
