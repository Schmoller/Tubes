package schmoller.tubes.definitions;

import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.BasicTube;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class TypeNormalTube extends TubeDefinition
{
	public static IIcon centerIcon;
	public static IIcon straightIcon;
	
	public static IIcon paintStraight;
	public static IIcon paintCenter;
	
	public static IIcon itemBorder;
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
		paintStraight = register.registerIcon("Tubes:paint");
		paintCenter = register.registerIcon("Tubes:paint-center");
		itemBorder = register.registerIcon("Tubes:item-border");
	}
	
	@Override
	public IIcon getCenterIcon()
	{
		return centerIcon;
	}
	
	public IIcon getStraightIcon() 
	{
		return straightIcon;
	}
	
	@Override
	public BaseTube createTube()
	{
		return new BasicTube("basic");
	}
}
