package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.ITube;
import schmoller.tubes.logic.EjectionTubeLogic;
import schmoller.tubes.logic.TubeLogic;
import schmoller.tubes.parts.BaseTubePart;
import schmoller.tubes.parts.DirectionalTubePart;

public class EjectionTube extends TubeDefinition
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
	public TubeLogic getTubeLogic( ITube tube )
	{
		return new EjectionTubeLogic(tube);
	}
	
	@Override
	public Class<? extends BaseTubePart> getPartClass()
	{
		return DirectionalTubePart.class;
	}

}
