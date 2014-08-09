package schmoller.tubes.types;

import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.routing.GoalRouter;

public class RoundRobinTube extends BaseTube
{
	private int[] mNext;
	
	public RoundRobinTube()
	{
		super("roundrobin");
		
		mNext = new int[6];
	}

	@Override
	protected boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	protected int onDetermineDestination( TubeItem item )
	{
		int fromDir = item.direction^1;
		int connections = getConnections();
		connections -= (connections & (1 << fromDir));
		
		int next = mNext[fromDir];
		
		int index = 0;
		int first = -1;
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				PathLocation dest = new GoalRouter(world(), new Position(x(),y(),z()), item, i, TubesAPI.goalOutput).route();
				if(dest != null)
				{
					if(first == -1)
						first = i;
					
					if(index >= next)
					{
						++mNext[fromDir];
						return i;
					}
					
					++index;
				}
			}
		}
		
		if(first != -1)
		{
			mNext[fromDir] = 1;
			return first;
		}
		
		
		return NO_ROUTE;
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		root.setIntArray("next", mNext);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		mNext = root.getIntArray("next");
	}
}
