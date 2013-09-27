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
	public static boolean isTube(IBlockAccess world, int x, int y, int z)
	{
		return (world.getBlockTileEntity(x, y, z) instanceof TileTube);
	}
	
	public static boolean isTubeConnectable(IBlockAccess world, int x, int y, int z, int side)
	{
		if(isTube(world, x, y, z))
			return true;
		
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if(ent == null)
			return false;
		
		if(ent instanceof ISidedInventory)
		{
			return ((ISidedInventory)ent).getAccessibleSlotsFromSide(side).length != 0;
		}
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
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			if(isTubeConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.ordinal()))
				map |= (1 << dir.ordinal());
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
				toSearch.add(loc);
				TileEntity ent = CommonHelper.getTileEntity(world, loc.position);
				if(ent instanceof TileTube)
					searchedLocations.add(loc.position);
			}
		}
		
		while(!toSearch.isEmpty())
		{
			PathLocation pos = toSearch.poll();
			
			TileEntity ent = CommonHelper.getTileEntity(world, pos.position);
			
			if(!(ent instanceof TileTube))
			{
				if(InventoryHelper.canAcceptItem(item.item, world, pos.position, pos.dir))
					return pos.initialDir;
			}
			else
			{
				conns = getConnectivity(world, pos.position);
				
				for(int i = 0; i < 6; ++i)
				{
					if((conns & (1 << i)) != 0)
					{
						PathLocation loc = new PathLocation(pos, i);
						
						if(!searchedLocations.contains(loc.position))
						{
							toSearch.add(loc);
							
							ent = CommonHelper.getTileEntity(world, loc.position);
							if(ent instanceof TileTube)
								searchedLocations.add(loc.position);
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
		
		public final int dist;
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
