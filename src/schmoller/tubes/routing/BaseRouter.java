package schmoller.tubes.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import schmoller.tubes.api.Position;
import schmoller.tubes.api.helpers.TubeHelper;

import net.minecraft.world.IBlockAccess;

public abstract class BaseRouter
{
	private HashSet<Position> mVisitedLocations;
	private PriorityQueue<PathLocation> mSearchQueue;

	private IBlockAccess mWorld;
	
	public BaseRouter()
	{
		mVisitedLocations = new HashSet<Position>();
		mSearchQueue = new PriorityQueue<PathLocation>();
	}
	
	protected void setup(IBlockAccess world, Position initialPosition)
	{
		mVisitedLocations.clear();
		mSearchQueue.clear();
		
		mWorld = world;
		mVisitedLocations.add(initialPosition);
		
		getInitialLocations(initialPosition);
	}
	
	public IBlockAccess getWorld()
	{
		return mWorld;
	}
	
	protected void addSearchPoint(PathLocation path)
	{
		if(mVisitedLocations.contains(path.position))
			return;
		
		mSearchQueue.add(path);
	}
	
	/**
	 * Called to add the next search points with addSearchPoint()
	 * Implementing classes should use this to plan where to search next
	 */
	protected abstract void getNextLocations(PathLocation current);

	protected void getInitialLocations(Position position)
	{
		getNextLocations(new PathLocation(position.x, position.y, position.z, 0, 6, 6));
	}
	
	protected abstract void updateState(PathLocation current);
	
	/**
	 * Called to determine if routing is finished
	 */
	protected abstract boolean isTerminator(Position current, int side);

	/**
	 * Find the applicable destination or null if none was found
	 */
	public PathLocation route()
	{
		List<PathLocation> dests = routeAll();
		if(dests.isEmpty())
			return null;
		
		return dests.get(TubeHelper.rand.nextInt(dests.size()));
	}
	
	public List<PathLocation> routeAll()
	{
		ArrayList<PathLocation> paths = new ArrayList<PathLocation>();
		int shortestPath = Integer.MAX_VALUE;
		
		while(!mSearchQueue.isEmpty())
		{
			PathLocation path = mSearchQueue.poll();
			if(path.dist > shortestPath)
				break;
			
			if(!mVisitedLocations.add(path.position))
				continue;
			
			if(isTerminator(path.position, path.dir))
			{
				shortestPath = path.dist;
				paths.add(path);
			}
			else
			{
				updateState(path);
				getNextLocations(path);
			}
		}
		
		return paths;
	}
	
	public static class PathLocation implements Comparable<PathLocation>
	{
		public final Position position;
		
		public int dist;
		public final int dir;
		public final int initialDir;
		public int color = -1;
		
		public PathLocation(PathLocation last, int newDir)
		{
			dist = last.dist + 1;
			dir = newDir;
			initialDir = last.initialDir;
			
			position = last.position.copy().offset(newDir,  1);
			color = last.color;
		}
		
		public PathLocation(Position position, int direction)
		{
			this.position = position.copy().offset(direction, 1);
			dist = 1;
			dir = initialDir = direction;
		}
		
		public PathLocation(int x, int y, int z, int dist, int dir, int initialDir)
		{
			position = new Position(x, y, z);
			
			this.dist = dist;
			
			this.dir = dir;
			
			this.initialDir = initialDir;
		}
		
		@Override
		public int compareTo( PathLocation other )
		{
			return dist - other.dist;
		}
		
		@Override
		public String toString()
		{
			return String.format("{Path: %d, %d, %d: %d initial: %d len: %d col: %d}", position.x, position.y, position.z, dir, initialDir, dist, color); 
		}
	}
}
