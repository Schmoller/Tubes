package schmoller.tubes.types;

import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.interfaces.ITubeConnectable;

public class EjectionTube extends DirectionalBasicTube
{
	public EjectionTube()
	{
		super("ejection");
	}
	
	@Override
	public int getHollowSize( int side )
	{
		if(side == getFacing())
			return 10;
		return super.getHollowSize(side);
	}
	
	@Override
	public boolean canPathThrough()
	{
		return false;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		if(con instanceof EjectionTube)
			return false;
		
		return super.canConnectTo(con);
	}
	
	@Override
	public boolean canConnectToInventories() { return false; }
	
	@Override
	protected boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	protected int onDetermineDestination( TubeItem item )
	{
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		
		if(item.item.canSpawnInWorld())
			item.item.spawnInWorld(world(), x() + 0.5 + dir.offsetX * 0.4, y() + 0.5 + dir.offsetY * 0.4, z() + 0.5 + dir.offsetZ * 0.4, dir.offsetX * 0.5f, dir.offsetY * 0.5f, dir.offsetZ * 0.5f);
		
		return ROUTE_TERM;
	}
}
