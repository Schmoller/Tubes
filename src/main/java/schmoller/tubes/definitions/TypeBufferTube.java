package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.BufferTube;

public class TypeBufferTube extends TubeDefinition
{
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/bufferTube.png");
	public static IIcon center;
	public static IIcon centerExport;
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		center = register.registerIcon("tubes:tube-buffer");
		centerExport = register.registerIcon("tubes:tube-buffer-export");
	}
	
	@Override
	public IIcon getCenterIcon()
	{
		return center;
	}

	@Override
	public IIcon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}

	@Override
	public TMultiPart createTube()
	{
		return new BufferTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
	}

}
