package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.ColoringTube;

public class TypeColoringTube extends TubeDefinition
{
	public static Icon center;
	public static Icon paint;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		center = register.registerIcon("tubes:tube-coloring");
		paint = register.registerIcon("tubes:tube-coloring-paint");
	}
	@Override
	public Icon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}

	@Override
	public Icon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}

	@Override
	public TMultiPart createTube()
	{
		return new ColoringTube();
	}

	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}

