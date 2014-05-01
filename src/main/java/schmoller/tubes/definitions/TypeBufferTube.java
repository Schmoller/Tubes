package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.BufferTube;

public class TypeBufferTube extends TubeDefinition
{
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/bufferTube.png");
	public static Icon center;
	public static Icon centerExport;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		center = register.registerIcon("tubes:tube-buffer");
		centerExport = register.registerIcon("tubes:tube-buffer-export");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return center;
	}

	@Override
	public Icon getStraightIcon()
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
