package schmoller.tubes.definitions;

import net.minecraft.util.Icon;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.RoutingTube;

public class TypeRoutingTube extends TubeDefinition
{

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
	public BaseTube createTube()
	{
		return new RoutingTube();
	}
}
