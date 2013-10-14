package schmoller.tubes.types;

public class RestrictionTube extends BasicTube
{
	public RestrictionTube()
	{
		super("restriction");
	}
	
	@Override
	public int getRouteWeight()
	{
		return 5000;
	}
}
