package schmoller.tubes.types;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.TubeItem;

public class EjectionTube extends DirectionalBasicTube
{
	public EjectionTube()
	{
		super("ejection");
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
		EntityItem entity = new EntityItem(world(), x() + 0.5 + dir.offsetX * 0.4, y() + 0.5 + dir.offsetY * 0.4, z() + 0.5 + dir.offsetZ * 0.4, item.item);
		
		entity.setVelocity(dir.offsetX * 0.5, dir.offsetY * 0.5, dir.offsetZ * 0.5);
		
		world().spawnEntityInWorld(entity);
		
		return ROUTE_TERM;
	}
}
