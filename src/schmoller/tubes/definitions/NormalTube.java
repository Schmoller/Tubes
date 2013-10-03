package schmoller.tubes.definitions;

import schmoller.tubes.ITube;
import schmoller.tubes.logic.NormalTubeLogic;
import schmoller.tubes.logic.TubeLogic;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class NormalTube extends TubeDefinition
{
	public static Icon centerIcon;
	public static Icon straightIcon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		centerIcon = register.registerIcon("Tubes:tube-center");
		straightIcon = register.registerIcon("Tubes:tube");
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
		return new NormalTubeLogic();
	}
}
