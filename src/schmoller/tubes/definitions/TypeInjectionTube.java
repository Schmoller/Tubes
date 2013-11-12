package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.InjectionTube;

public class TypeInjectionTube extends TubeDefinition
{
	public Icon centerIcon;
	public Icon straightIcon;
	
	public static Icon coreIcon;
	
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/injectionTube.png");
	
	@Override
	public void registerIcons( IconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
		coreIcon = register.registerIcon("Tubes:injection-core");
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
		return new InjectionTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
