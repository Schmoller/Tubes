package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.ValveTube;

public class TypeValveTube extends TubeDefinition
{
	public static IIcon valve;
	public static IIcon valveOpen;
	public static IIcon valveClosed;
	public static IIcon valveEdge;
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		valve = register.registerIcon("tubes:tube-valve");
		valveOpen = register.registerIcon("tubes:tube-valve-open");
		valveClosed = register.registerIcon("tubes:tube-valve-closed");
		valveEdge = register.registerIcon("tubes:tube-valve-edge");
	}
	
	@Override
	public IIcon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}

	@Override
	public IIcon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}

	@Override
	public TMultiPart createTube()
	{
		return new ValveTube();
	}

}
