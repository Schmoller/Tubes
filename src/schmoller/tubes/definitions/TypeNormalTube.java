package schmoller.tubes.definitions;

import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.BasicTube;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class TypeNormalTube extends TubeDefinition
{
	public static Icon centerIcon;
	public static Icon straightIcon;
	
	public static Icon paintStraight;
	public static Icon paintCenter;
	
	public static Icon itemBorder;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
		paintStraight = register.registerIcon("Tubes:paint");
		paintCenter = register.registerIcon("Tubes:paint-center");
		itemBorder = register.registerIcon("Tubes:item-border");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return centerIcon;
	}
	
	public Icon getStraightIcon() 
	{
		return straightIcon;
	}
	
	@Override
	public BaseTube createTube()
	{
		return new BasicTube("basic");
	}
}
