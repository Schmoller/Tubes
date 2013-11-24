package schmoller.tubes.api;

import schmoller.tubes.api.interfaces.IInventoryHandler;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.IFluidHandler;

public class InteractionHandler
{
	/**
	 * Can a tube do something with this block?
	 * @param side 
	 */
	public static boolean isInteractable(IBlockAccess world, int x, int y, int z, int side)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		
		if(block != null)
		{
			BlockInstance object = new BlockInstance(world, x, z, y);
			if(canAccess(ProviderRegistry.provideFor(object), side))
				return true;
			if(canAccess(ProviderRegistry.provideFluidHandlerFor(object), side))
				return true;
		}
		
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		if(canAccess(ProviderRegistry.provideFor(ent), side))
			return true;
		if(canAccess(ProviderRegistry.provideFluidHandlerFor(ent), side))
			return true;
		
		if(ent instanceof TileMultipart)
		{
			TMultiPart part = ((TileMultipart)ent).partMap(6);
			
			if(part != null)
			{
				if(canAccess(ProviderRegistry.provideFor(part), side))
					return true;
				if(canAccess(ProviderRegistry.provideFluidHandlerFor(part), side))
					return true;
			}
		}
		
		if(ent instanceof IInventory || ent instanceof IFluidHandler)
			return true;
		
		return false;
	}
	
	public static boolean canAccess(Object object, int side)
	{
		if(object instanceof ISidedInventory)
			return ((ISidedInventory)object).getAccessibleSlotsFromSide(side).length > 0;
		
		return object != null;
	}
	
	public static IInventoryHandler getInventoryHandler( IBlockAccess world, Position position )
	{
		return getInventoryHandler(world, position.x, position.y, position.z);
	}
	public static IInventoryHandler getInventoryHandler(IBlockAccess world, int x, int y, int z)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		
		IInventory inv;
		if(block != null)
		{
			BlockInstance object = new BlockInstance(world, x, z, y);
			inv = ProviderRegistry.provideFor(object);
			if(inv != null)
				return InventoryHandlerRegistry.getHandler(inv);
		}
		
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		inv = ProviderRegistry.provideFor(ent);
		if(inv != null)
			return InventoryHandlerRegistry.getHandler(inv);
		
		if(ent instanceof TileMultipart)
		{
			TMultiPart part = ((TileMultipart)ent).partMap(6);
			
			if(part != null)
			{
				inv = ProviderRegistry.provideFor(part);
				if(inv != null)
					return InventoryHandlerRegistry.getHandler(inv);
			}
		}
		
		if(ent instanceof IInventory)
			return InventoryHandlerRegistry.getHandler((IInventory)ent);
		
		return null;
	}
	
	public static IFluidHandler getFluidHandler( IBlockAccess world, Position position )
	{
		return getFluidHandler(world, position.x, position.y, position.z);
	}
	public static IFluidHandler getFluidHandler(IBlockAccess world, int x, int y, int z)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		
		IFluidHandler handler;
		if(block != null)
		{
			BlockInstance object = new BlockInstance(world, x, z, y);
			handler = ProviderRegistry.provideFluidHandlerFor(object);
			if(handler != null)
				return handler;
		}
		
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		handler = ProviderRegistry.provideFluidHandlerFor(ent);
		if(handler != null)
			return handler;
		
		if(ent instanceof TileMultipart)
		{
			TMultiPart part = ((TileMultipart)ent).partMap(6);
			
			if(part != null)
			{
				handler = ProviderRegistry.provideFluidHandlerFor(part);
				if(handler != null)
					return handler;
			}
		}
		
		if(ent instanceof IFluidHandler)
			return (IFluidHandler)ent;
		
		return null;
	}

	
}
