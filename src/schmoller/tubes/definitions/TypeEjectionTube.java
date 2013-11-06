package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.EjectionTube;

public class TypeEjectionTube extends TubeDefinition
{
	public Icon centerIcon;
	public Icon straightIcon;
	public static Icon funnelIcon;
	public static Icon endIcon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
		funnelIcon = register.registerIcon("Tubes:tube-ejection");
		endIcon = register.registerIcon("Tubes:tube-ejection-edge");
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
		return new EjectionTube();
	}

}
