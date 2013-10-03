package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.ITube;
import schmoller.tubes.logic.ExtractionTubeLogic;
import schmoller.tubes.logic.TubeLogic;
import schmoller.tubes.parts.BaseTubePart;
import schmoller.tubes.parts.DirectionalTubePart;

public class ExtractionTube extends TubeDefinition
{
	public static Icon icon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		icon = register.registerIcon("Tubes:tube-extraction");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return NormalTube.centerIcon;
	}

	@Override
	public Icon getStraightIcon()
	{
		return NormalTube.straightIcon;
	}

	@Override
	public TubeLogic getTubeLogic( ITube tube )
	{
		return new ExtractionTubeLogic(tube);
	}

	@Override
	public Class<? extends BaseTubePart> getPartClass()
	{
		return DirectionalTubePart.class;
	}
}
