package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.InjectionTube;

public class TypeInjectionTube extends TubeDefinition
{
	public static IIcon coreIcon;
	public static IIcon coreOpenIcon;
	
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/injectionTube.png");
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		coreIcon = register.registerIcon("Tubes:tube-injection");
		coreOpenIcon = register.registerIcon("Tubes:tube-injection-open");
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
	public BaseTube createTube()
	{
		return new InjectionTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
