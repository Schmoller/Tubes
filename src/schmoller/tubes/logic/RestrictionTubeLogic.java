package schmoller.tubes.logic;

import schmoller.tubes.ITube;

public class RestrictionTubeLogic extends TubeLogic
{
	public RestrictionTubeLogic( ITube tube )
	{
		super(tube);
	}
	
	@Override
	public int getRouteWeight()
	{
		return 5000;
	}
}
