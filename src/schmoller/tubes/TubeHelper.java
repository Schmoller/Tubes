package schmoller.tubes;

import java.util.Random;

import schmoller.tubes.routing.BaseRouter;
import schmoller.tubes.routing.BlockedRouter;
import schmoller.tubes.routing.InputRouter;
import schmoller.tubes.routing.OutputRouter;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
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
			
			if(ent instanceof ISidedInventory)
				return ((ISidedInventory)ent).getAccessibleSlotsFromSide(side).length != 0;
			else if (ent instanceof IInventory)
				return true;
		}
		
		return false;
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
	
	public static int findNextDirection(IBlockAccess world, int x, int y, int z, TubeItem item)
	{
		BaseRouter.PathLocation path = null;
		
		if(item.state == TubeItem.NORMAL)
			path = new OutputRouter(world, new Position(x, y, z), item).route();
		else if(item.state == TubeItem.IMPORT)
		{
			path = new InputRouter(world, new Position(x, y, z), item).route();
			if(path == null)
			{
				path = new OutputRouter(world, new Position(x, y, z), item).route();
				item.state = TubeItem.NORMAL;
			}
		}
		else if(item.state == TubeItem.BLOCKED)
		{
			path = new OutputRouter(world, new Position(x,y,z), item).route();
			
			if(path == null)
				path = new BlockedRouter(world, new Position(x,y,z), item).route();
			else
				item.state = TubeItem.NORMAL;
		}
		
		if(path != null)
			return path.initialDir;
		return -1;
	}
}
