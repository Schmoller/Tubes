package schmoller.tubes.definitions;

import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.RestrictionTube;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class TypeRestrictionTube extends TubeDefinition
{
	public static Icon center;
	public static Icon straight;
	public static Icon edge;
	
	public static Icon paintStraight;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		center = register.registerIcon("Tubes:tube-restriction-center");
		straight = register.registerIcon("Tubes:tube-restriction");
		paintStraight = register.registerIcon("Tubes:paint-restriction");
		edge = register.registerIcon("Tubes:tube-restriction-edge");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}
	
	public Icon getStraightIcon() 
	{
		return TypeNormalTube.straightIcon;
	}
	
	@Override
	public BaseTube createTube()
	{
		return new RestrictionTube();
	}
}
