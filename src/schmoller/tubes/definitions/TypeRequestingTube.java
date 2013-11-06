package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.RequestingTube;

public class TypeRequestingTube extends TubeDefinition
{
	public static Icon icon;
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/requesterTube.png");
	
	@Override
	public void registerIcons( IconRegister register )
	{
		icon = register.registerIcon("Tubes:tube-requesting");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}

	@Override
	public Icon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}
	
	@Override
	public BaseTube createTube()
	{
		return new RequestingTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
