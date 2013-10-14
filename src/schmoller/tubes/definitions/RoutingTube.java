package schmoller.tubes.definitions;

import net.minecraft.util.Icon;
import schmoller.tubes.ITube;
import schmoller.tubes.logic.RoutingTubeLogic;
import schmoller.tubes.logic.TubeLogic;

public class RoutingTube extends TubeDefinition
{

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
		return new RoutingTubeLogic(tube);
	}
}
