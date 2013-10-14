package schmoller.tubes.definitions;

import codechicken.core.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.FilterTube;

public class TypeFilterTube extends TubeDefinition
{
	public Icon centerIcon;
	public Icon straightIcon;
	
	public static Icon filterIcon;
	public static Icon filterOpenIcon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
		filterIcon = register.registerIcon("Tubes:tube-filter");
		filterOpenIcon = register.registerIcon("Tubes:tube-filter-center");
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
		return new FilterTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
