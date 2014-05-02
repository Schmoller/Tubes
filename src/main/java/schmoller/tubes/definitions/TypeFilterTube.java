package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.FilterTube;

public class TypeFilterTube extends TubeDefinition
{
	public IIcon centerIcon;
	public IIcon straightIcon;
	
	public static IIcon filterIcon;
	public static IIcon filterOpenIcon;
	
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/filterTube.png");
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
		filterIcon = register.registerIcon("Tubes:tube-filter");
		filterOpenIcon = register.registerIcon("Tubes:tube-filter-center");
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
		return new FilterTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
