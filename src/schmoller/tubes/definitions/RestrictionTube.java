package schmoller.tubes.definitions;

import schmoller.tubes.ITube;
import schmoller.tubes.logic.RestrictionTubeLogic;
import schmoller.tubes.logic.TubeLogic;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class RestrictionTube extends TubeDefinition
{
	public Icon centerIcon;
	public Icon straightIcon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-restriction-center");
		straightIcon = register.registerIcon("Tubes:tube-restriction");
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
		return new RestrictionTubeLogic(tube);
	}
}
