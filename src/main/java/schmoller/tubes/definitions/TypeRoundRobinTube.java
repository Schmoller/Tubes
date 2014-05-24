package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.RoundRobinTube;

public class TypeRoundRobinTube extends TubeDefinition
{
	public static IIcon center;
	
	@Override
	public IIcon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}

	@Override
	public IIcon getStraightIcon()
	{
		return TypeNormalTube.paintStraight;
	}

	@Override
	public TMultiPart createTube()
	{
		return new RoundRobinTube();
	}
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		center = register.registerIcon("tubes:tube-roundrobin-center");
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
