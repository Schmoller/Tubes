package schmoller.tubes.definitions;

import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.RestrictionTube;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class TypeRestrictionTube extends TubeDefinition
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
	public BaseTube createTube()
	{
		return new RestrictionTube();
	}
}
