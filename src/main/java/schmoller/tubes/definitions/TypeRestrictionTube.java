package schmoller.tubes.definitions;

import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.RestrictionTube;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class TypeRestrictionTube extends TubeDefinition
{
	public static IIcon center;
	public static IIcon straight;
	public static IIcon edge;
	
	public static IIcon paintStraight;
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		center = register.registerIcon("Tubes:tube-restriction-center");
		straight = register.registerIcon("Tubes:tube-restriction");
		paintStraight = register.registerIcon("Tubes:paint-restriction");
		edge = register.registerIcon("Tubes:tube-restriction-edge");
	}
	
	@Override
	public IIcon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}
	
	public IIcon getStraightIcon() 
	{
		return TypeNormalTube.straightIcon;
	}
	
	@Override
	public BaseTube createTube()
	{
		return new RestrictionTube();
	}
}
