package schmoller.tubes.definitions;

import codechicken.core.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.RequestingTube;

public class TypeRequestingTube extends TubeDefinition
{
	public static Icon icon;
	
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
