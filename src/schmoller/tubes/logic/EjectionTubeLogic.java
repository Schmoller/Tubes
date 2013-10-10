package schmoller.tubes.logic;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.IDirectionalTube;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.TubeItem;

public class EjectionTubeLogic extends TubeLogic
{
	public EjectionTubeLogic(ITube tube)
	{
		super(tube);
		mTube = tube;
	}
	
	@Override
	public int getConnectionClass()
	{
		return 20;
	}
	
	@Override
	public boolean canPathThrough()
	{
		return false;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return (con.getConnectionClass() == 0);
	}
	
	@Override
	public int getConnectableMask()
	{
		int dir = ((IDirectionalTube)mTube).getFacing();
		
		return 63 - (63 & (1 << dir));
	}
	
	@Override
	public boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	public int onDetermineDestination( TubeItem item )
	{
		ForgeDirection dir = ForgeDirection.getOrientation(((IDirectionalTube)mTube).getFacing());
		EntityItem entity = new EntityItem(mTube.world(), mTube.x() + 0.5 + dir.offsetX * 0.4, mTube.y() + 0.5 + dir.offsetY * 0.4, mTube.z() + 0.5 + dir.offsetZ * 0.4, item.item);
		
		entity.setVelocity(dir.offsetX * 0.5, dir.offsetY * 0.5, dir.offsetZ * 0.5);
		
		mTube.world().spawnEntityInWorld(entity);
		
		return -2;
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return false;
	}
}
