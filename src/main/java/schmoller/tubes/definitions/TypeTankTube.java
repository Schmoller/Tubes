package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.TankTube;

public class TypeTankTube extends TubeDefinition
{
	public static IIcon coreIcon;
	public static IIcon coreOpenIcon;
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		coreIcon = register.registerIcon("Tubes:tube-tank");
		coreOpenIcon = register.registerIcon("Tubes:tube-tank-top");
	}
	
	@Override
	public IIcon getCenterIcon()
	{
		return coreIcon;
	}
	
	public IIcon getStraightIcon() 
	{
		return TypeNormalTube.straightIcon;
	}
	
	@Override
	public TMultiPart createTube()
	{
		return new TankTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
