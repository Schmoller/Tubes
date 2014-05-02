package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.EjectionTube;

public class TypeEjectionTube extends TubeDefinition
{
	public IIcon centerIcon;
	public IIcon straightIcon;
	public static IIcon funnelIcon;
	public static IIcon endIcon;
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
		funnelIcon = register.registerIcon("Tubes:tube-ejection");
		endIcon = register.registerIcon("Tubes:tube-ejection-edge");
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
		return new EjectionTube();
	}

}
