package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.ColoringTube;

public class TypeColoringTube extends TubeDefinition
{
	public static IIcon center;
	public static IIcon paint;
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		center = register.registerIcon("tubes:tube-coloring");
		paint = register.registerIcon("tubes:tube-coloring-paint");
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

