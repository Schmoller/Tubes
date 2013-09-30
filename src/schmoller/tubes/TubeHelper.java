package schmoller.tubes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public class TubeHelper
{
	public static final Random rand = new Random();
	public static ITubeConnectable getTubeConnectable(IBlockAccess world, int x, int y, int z)
	{
		return getTubeConnectable(world.getBlockTileEntity(x, y, z));
	}
	public static ITubeConnectable getTubeConnectable(TileEntity entity)
	{
		if(entity instanceof TileMultipart)
		{
			for(TMultiPart part : ((TileMultipart)entity).jPartList())
			{
				if(part instanceof ITubeConnectable)
					return ((ITubeConnectable)part);
			}
		}
		else if(entity instanceof ITubeConnectable)
			return ((ITubeConnectable)entity);
		
		return null;
	}
	
	public static boolean isTubeConnectable(ITubeConnectable other, IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if(ent == null)
			return false;

		ITubeConnectable con = getTubeConnectable(ent);
		if(con != null)
		{
			if((con.getConnectableMask() & (1 << side)) == 0)
				return false;
				
			if(other instanceof ITube)
			{
				if(!((ITube)other).getLogic().canConnectTo(con))
					return false;
				
				if(con instanceof ITube)
				{
					if(!((ITube)con).getLogic().canConnectTo(other))
						return false;
				}
			}
			
			return true;
		}
		else
		{
			if(other instanceof ITube)
			{
				if(!((ITube)other).getLogic().canConnectToInventories())
					return false;
			}
			
			if(ent instanceof ISidedInventory)
				return ((ISidedInventory)ent).getAccessibleSlotsFromSide(side).length != 0;
			else if (ent instanceof IInventory)
				return true;
		}
		
		return false;
	}
	
	public static int getConnectivity(IBlockAccess world, ChunkPosition position)
	{
		return getConnectivity(world, position.x, position.y, position.z);
	}
	
	public static int getConnectivity(IBlockAccess world, int x, int y, int z)
	{
		int map = 0;
		ITubeConnectable tube = getTubeConnectable(world, x, y, z);
		
		if(tube == null)
			return 0;
		
		int mask = tube.getConnectableMask();
		for(int side = 0; side < 6; ++side)
		{
			if((mask & (1 << side)) != 0 && isTubeConnectable(tube, world, x + ForgeDirection.getOrientation(side).offsetX, y + ForgeDirection.getOrientation(side).offsetY, z + ForgeDirection.getOrientation(side).offsetZ, side ^ 1))
				map |= 1 << side;
		}
		
		return map;
	}
	
	
	
	
	
	public static int findNextDirection(IBlockAccess world, int x, int y, int z, TubeItem item)
	{
		HashSet<ChunkPosition> searchedLocations = new HashSet<ChunkPosition>();
		PriorityQueue<PathLocation> toSearch = new PriorityQueue<PathLocation>();
		
		ArrayList<PathLocation> paths = new ArrayList<PathLocation>();
		int shortestPath = Integer.MAX_VALUE;
		
		int conns = getConnectivity(world, x, y, z);
		searchedLocations.add(new ChunkPosition(x, y, z));
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i); 
				PathLocation loc = new PathLocation(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, 1, i, i);
				
				TileEntity ent = CommonHelper.getTileEntity(world, loc.position);
				ITubeConnectable con = getTubeConnectable(ent);
				if(con == null || con.canAddItem(item))
				{
					toSearch.add(loc);

					if(con != null && con.canPathThrough())
						searchedLocations.add(loc.position);
				}
			}
		}
		
		while(!toSearch.isEmpty())
		{
			PathLocation pos = toSearch.poll();
			
			if(pos.dist > shortestPath)
				break;
			
			TileEntity ent = CommonHelper.getTileEntity(world, pos.position);
			ITubeConnectable con = getTubeConnectable(ent);
			
			if(con == null)
			{
				if(InventoryHelper.canAcceptItem(item.item, world, pos.position, pos.dir))
				{
					shortestPath = pos.dist;
					paths.add(pos);
				}
			}
			else if(con.canPathThrough())
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
							con = getTubeConnectable(ent);
							
							if(con == null || con.canAddItem(item))
							{
								toSearch.add(loc);

								if(con != null && con.canPathThrough())
								{
									loc.dist += con.getRouteWeight()-1;
									searchedLocations.add(loc.position);
								}
							}
						}
					}
				}
			}
			else
			{
				shortestPath = pos.dist;
				paths.add(pos);
			}
				
		}
		
		if(paths.isEmpty())
			return -1;
		
		return paths.get(rand.nextInt(paths.size())).initialDir;
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
