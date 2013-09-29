package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.ITube;
import schmoller.tubes.logic.ExtractionTubeLogic;
import schmoller.tubes.logic.TubeLogic;
import schmoller.tubes.parts.BaseTubePart;
import schmoller.tubes.parts.InventoryTubePart;

public class ExtractionTube extends TubeDefinition
{
	public Icon centerIcon;
	public Icon straightIcon;
	
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
		return new ExtractionTubeLogic(tube);
	}
	
	@Override
	public Class<? extends BaseTubePart> getPartClass()
	{
		return InventoryTubePart.class;
	}
}
