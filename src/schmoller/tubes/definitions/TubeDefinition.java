package schmoller.tubes.definitions;

import schmoller.tubes.ITube;
import schmoller.tubes.logic.TubeLogic;
import schmoller.tubes.parts.BaseTubePart;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;


public abstract class TubeDefinition
{
	public void registerIcons(IconRegister register) {}
	
	public abstract Icon getCenterIcon();
	public abstract Icon getStraightIcon();
	
	public abstract TubeLogic getTubeLogic(ITube tube);

	public Class<? extends BaseTubePart> getPartClass() { return BaseTubePart.class; }
}
