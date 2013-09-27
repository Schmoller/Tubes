package schmoller.tubes;

import java.util.HashSet;
import java.util.PriorityQueue;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public class TubeHelper
{
	public static boolean isTubeConnectable(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if(ent == null)
			return false;

		if(ent instanceof ITubeConnectable)
			return (((ITubeConnectable)ent).getConnectableMask() & (1 << side)) != 0;
		else if(ent instanceof ISidedInventory)
			return ((ISidedInventory)ent).getAccessibleSlotsFromSide(side).length != 0;
		else if (ent instanceof IInventory)
			return true;
		
		return false;
	}
	
	public static int getConnectivity(IBlockAccess world, ChunkPosition position)
	{
		return getConnectivity(world, position.x, position.y, position.z);
	}
	
	public static int getConnectivity(IBlockAccess world, int x, int y, int z)
	{
		int map = 0;
		ITubeConnectable tube = CommonHelper.getTileEntity(world, x, y, z, ITubeConnectable.class);
		
		if(tube == null)
			return 0;
		
		int mask = tube.getConnectableMask();
		for(int side = 0; side < 6; ++side)
		{
			if((mask & (1 << side)) != 0 && isTubeConnectable(world, x + ForgeDirection.getOrientation(side).offsetX, y + ForgeDirection.getOrientation(side).offsetY, z + ForgeDirection.getOrientation(side).offsetZ, side ^ 1))
				map |= 1 << side;
		}
		
		return map;
	}
	
	
	
	
	
	public static int findNextDirection(IBlockAccess world, int x, int y, int z, TubeItem item)
	{
		HashSet<ChunkPosition> searchedLocations = new HashSet<ChunkPosition>();
		PriorityQueue<PathLocation> toSearch = new PriorityQueue<PathLocation>();
		
		int conns = getConnectivity(world, x, y, z);
		searchedLocations.add(new ChunkPosition(x, y, z));
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i); 
				PathLocation loc = new PathLocation(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, 1, i, i);
				
				TileEntity ent = CommonHelper.getTileEntity(world, loc.position);
				if(!(ent instanceof ITubeConnectable) || ((ITubeConnectable)ent).canAddItem(item))
				{
					toSearch.add(loc);

					if(ent instanceof ITubeConnectable)
						searchedLocations.add(loc.position);
				}
			}
		}
		
		while(!toSearch.isEmpty())
		{
			PathLocation pos = toSearch.poll();
			
			TileEntity ent = CommonHelper.getTileEntity(world, pos.position);
			
			if(!(ent instanceof ITubeConnectable))
			{
				if(InventoryHelper.canAcceptItem(item.item, world, pos.position, pos.dir))
					return pos.initialDir;
			}
			else if(((ITubeConnectable)ent).canPathThrough())
			{
				conns = getConnectivity(world, pos.position);
				
				for(int i = 0; i < 6; ++i)
				{
					if((conns & (1 << i)) != 0)
					{
						PathLocation loc = new PathLocation(pos, i);
						
						if(!searchedLocations.contains(loc.position))
						{
							ent = CommonHelper.getTileEntity(world, loc.position);
							
							if(!(ent instanceof ITubeConnectable) || ((ITubeConnectable)ent).canAddItem(item))
							{
								toSearch.add(loc);

								if(ent instanceof ITubeConnectable)
								{
									loc.dist += ((ITubeConnectable)ent).getRouteWeight()-1;
									searchedLocations.add(loc.position);
								}
							}
						}
					}
				}
			}
		}
		
		return -1;
	}
	
	private static class PathLocation implements Comparable<PathLocation>
	{
		public final ChunkPosition position;
		
		public int dist;
		public final int dir;
		public final int initialDir;
		
		public PathLocation(PathLocation last, int newDir)
		{
			dist = last.dist + 1;
			dir = newDir;
			initialDir = last.initialDir;
			
			position = new ChunkPosition(last.position.x + ForgeDirection.getOrientation(newDir).offsetX, last.position.y + ForgeDirection.getOrientation(newDir).offsetY, last.position.z + ForgeDirection.getOrientation(newDir).offsetZ);
		}
		
		public PathLocation(int x, int y, int z, int dist, int dir, int initialDir)
		{
			position = new ChunkPosition(x, y, z);
			
			this.dist = dist;
			
			this.dir = dir;
			
			this.initialDir = initialDir;
		}
		
		@Override
		public int compareTo( PathLocation other )
		{
			return dist - other.dist;
		}
	}
}
