package schmoller.tubes.types;

import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.interfaces.ITubeConnectable;

public class ColoringTube extends BasicTube
{
	public ColoringTube()
	{
		super("coloring");
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return true;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		return true;
	}
	
	@Override
	public void simulateEffects( TubeItem item )
	{
		item.colour = getColor();
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		item.colour = getColor();
		return super.onItemJunction(item);
	}
}
