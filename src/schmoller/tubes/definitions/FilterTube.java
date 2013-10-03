package schmoller.tubes.definitions;

import codechicken.core.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.ITube;
import schmoller.tubes.logic.FilterTubeLogic;
import schmoller.tubes.logic.TubeLogic;

public class FilterTube extends TubeDefinition
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
	public TubeLogic getTubeLogic( ITube tube )
	{
		return new FilterTubeLogic(tube);
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
