package schmoller.tubes.definitions;

import schmoller.tubes.ITube;
import schmoller.tubes.logic.TubeLogic;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;


public abstract class TubeDefinition
{
	public void registerIcons(IconRegister register) {}
	
	public abstract Icon getCenterIcon();
	public abstract Icon getStraightIcon();
	
	public abstract TubeLogic getTubeLogic(ITube tube);
}
