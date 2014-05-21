package schmoller.tubes.api.helpers;

import java.util.Random;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IImportSource;
import schmoller.tubes.api.interfaces.IImportController;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.routing.ImportSourceFinder;

import codechicken.multipart.TileMultipart;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class TubeHelper
{
	public static final Random rand = new Random();
	public static ITubeConnectable getTubeConnectable(IBlockAccess world, int x, int y, int z)
	{
		return getTubeConnectable(world.getTileEntity(x, y, z));
	}
	public static ITubeConnectable getTubeConnectable(TileEntity entity)
	{
		if(entity instanceof TileMultipart)
		{
			if(((TileMultipart)entity).partMap(6) instanceof ITubeConnectable)
					return ((ITubeConnectable)((TileMultipart)entity).partMap(6));
		}
		else if(entity instanceof ITubeConnectable)
			return ((ITubeConnectable)entity);
		
		return null;
	}
	
	/**
	 * Checks if the block at x,y,z in world can connect with other
	 * @param other
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side This is the direction you're attempting to connect to from other. 
	 * @return
	 */
	public static boolean isTubeConnectable(ITubeConnectable other, IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getTileEntity(x, y, z);

		ITubeConnectable con = getTubeConnectable(ent);
		if(con != null)
		{
			if((con.getConnectableMask() & (1 << side)) == 0)
				return false;
				
			if(other instanceof ITube)
			{
				if(!((ITube)other).canConnectTo(con))
					return false;
				
				if(con instanceof ITube)
				{
					if(!((ITube)con).canConnectTo(other))
						return false;
				}
			}
			
			return true;
		}
		else
		{
			if(other instanceof ITube)
			{
				if(!((ITube)other).canConnectToInventories())
					return false;
			}
			
			return InteractionHandler.isInteractable(world, x, y, z, side ^ 1);
		}
	}
	
	public static int getConnectivity(IBlockAccess world, Position position)
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
	
	/**
	 * Returns the next direction that the item must travel to reach its destination.
	 * @return The direction or -1 if there is no path
	 */
	public static int findNextDirection(IBlockAccess world, int x, int y, int z, TubeItem item)
	{
		BaseRouter.PathLocation path = null;
		
		if(item.state == TubeItem.NORMAL)
			path = TubesAPI.instance.getOutputRouter(world, new Position(x, y, z), item).route();
		else if(item.state == TubeItem.IMPORT)
		{
			path = TubesAPI.instance.getImportRouter(world, new Position(x, y, z), item).route();
			if(path == null)
			{
				path = TubesAPI.instance.getOutputRouter(world, new Position(x, y, z), item).route();
				item.state = TubeItem.NORMAL;
			}
		}
		else if(item.state == TubeItem.BLOCKED)
		{
			path = TubesAPI.instance.getOutputRouter(world, new Position(x,y,z), item).route();
			
			if(path == null)
				path = TubesAPI.instance.getOverflowRouter(world, new Position(x,y,z), item).route();
			else
				item.state = TubeItem.NORMAL;
		}
		
		if(path != null)
			return path.initialDir;
		return -1;
	}
	
	/**
	 * Returns whether to render the specified connection has an inventory connection
	 * NOTE: You should not offset the x,y,z coords to the specified side
	 */
	public static boolean renderAsInventoryConnection(IBlockAccess world, int x, int y, int z, int side)
	{
		ITubeConnectable con = getTubeConnectable(world, x + ForgeDirection.getOrientation(side).offsetX, y + ForgeDirection.getOrientation(side).offsetY, z + ForgeDirection.getOrientation(side).offsetZ);
		
		if(con != null)
			return con.showInventoryConnection(side ^ 1);
		
		return InteractionHandler.isInteractable(world, x + ForgeDirection.getOrientation(side).offsetX, y + ForgeDirection.getOrientation(side).offsetY, z + ForgeDirection.getOrientation(side).offsetZ, side);
	}
	
	/**
	 * Attempts to find a place to import something that matches the filter from. If it succeeds, it will add the item to the tube next to it. 
	 * @param world The world
	 * @param position The location it should go to (ie. the object doing the requesting)
	 * @param side The side of the requesting object to search from
	 * @param filter The filter to match, may be null
	 * @param mode The size comparison to perform
	 * @param color The color to paint items when they are pulled. -1 for no color
	 * @param callback A callback that can be used to pick and choose valid import sources
	 * @return The TubeItem that was requested or null if the request failed.
	 */
	public static TubeItem requestImport(IBlockAccess world, Position position, int side, IFilter filter, SizeMode mode, int color, IImportController controller)
	{
		if(filter == null)
			filter = new AnyFilter(0);
		
		// Disallow any oversize requests
		int size = Math.min(filter.size(), filter.getMax()); 
		
		PathLocation source = new ImportSourceFinder(world, position, side, filter, mode, color).setImportControl(controller).route();
		
		if(source == null)
			return null;
		
		IPayloadHandler handler = InteractionHandler.getHandler(filter.getPayloadType(), world, source.position);
		IImportSource importSource = CommonHelper.getInterface(world, source.position, IImportSource.class);
		Payload extracted = null;
		
		if(importSource != null)
			extracted = importSource.pullItem(filter, source.dir ^ 1, size, mode, false);
		else if(handler != null)
			extracted = handler.extract(filter, source.dir ^ 1, size, mode, false);
		
		if(extracted != null)
		{
			TubeItem tItem = new TubeItem(extracted);
			tItem.state = TubeItem.IMPORT;
			tItem.direction = source.dir ^ 1;
			tItem.colour = color;
			
			PathLocation tubeLoc = new PathLocation(source, source.dir ^ 1);
			TileEntity tile = CommonHelper.getTileEntity(world, tubeLoc.position);
			ITubeConnectable con = TubeHelper.getTubeConnectable(tile);
			if(con != null)
			{
				if(con.addItem(tItem, true))
				{
					// Finalize
					if(importSource != null)
						importSource.pullItem(filter, source.dir ^ 1, size, mode, true);
					else if(handler != null)
						handler.extract(filter, source.dir ^ 1, size, mode, true);
					
					return tItem;
				}
			}
			
			return null;
		}
		
		return null;
	}
}
