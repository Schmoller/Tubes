package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.TankTube;

public class TypeTankTube extends TubeDefinition
{
	public static Icon coreIcon;
	public static Icon coreOpenIcon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		coreIcon = register.registerIcon("Tubes:tube-tank");
		coreOpenIcon = register.registerIcon("Tubes:tube-tank-top");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return coreIcon;
	}
	
	public Icon getStraightIcon() 
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
