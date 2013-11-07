package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.ValveTube;

public class TypeValveTube extends TubeDefinition
{
	public static Icon valve;
	public static Icon valveOpen;
	public static Icon valveClosed;
	public static Icon valveEdge;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		valve = register.registerIcon("tubes:tube-valve");
		valveOpen = register.registerIcon("tubes:tube-valve-open");
		valveClosed = register.registerIcon("tubes:tube-valve-closed");
		valveEdge = register.registerIcon("tubes:tube-valve-edge");
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
	public TMultiPart createTube()
	{
		return new ValveTube();
	}

}
