package schmoller.tubes.inventory;

import schmoller.tubes.Position;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class InventoryHelper
{
	public static InventoryMapper getMapper(Object object)
	{
		IInventory inventory = InventoryProvider.provideFor(object);
		
		if(inventory == null)
		{
			if(object instanceof IInventory)
				inventory = (IInventory)object;
		}
		
		if(inventory == null)
			return null;
		
		if(inventory instanceof ISidedInventory)
			return new MapperISided((ISidedInventory)inventory);
		return new MapperBasic(inventory);
	}
	
	public static boolean areItemsEqual(ItemStack item1, ItemStack item2)
	{
		return (item1 == null && item2 == null) || (item1 != null && item2 != null && item1.isItemEqual(item2) && ItemStack.areItemStackTagsEqual(item1, item2));
	}
	
	public static boolean canAcceptItem(ItemStack item, IBlockAccess world, Position pos, int side)
	{
		return canAcceptItem(item, world, pos.x, pos.y, pos.z, side);
	}
	public static boolean canAcceptItem(ItemStack item, IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		InventoryMapper mapper = getMapper(ent);
		if(mapper == null)
			return false;
		
		SlotCollection col = mapper.getInsertSlots(item, side ^ 1);
		return col.canAddAny(item);
	}
	
	public static void mergeItemStackSimulate(IInventory inv, int slot, ItemStack item)
	{
		if (!inv.isStackValidForSlot(slot, item))
			return;
		
		ItemStack existing = inv.getStackInSlot(slot);
		if(existing == null)
		{
			if(item.stackSize >= inv.getInventoryStackLimit())
				item.splitStack(inv.getInventoryStackLimit());
			else
				item.stackSize = 0;
		}
		else if(existing.isItemEqual(item) && ItemStack.areItemStackTagsEqual(existing, item))
		{
			int toAdd = Math.min(inv.getInventoryStackLimit(), Math.min(item.stackSize, existing.getMaxStackSize() - existing.stackSize));
			item.stackSize -= toAdd;
		}
	}
	
	public static void mergeItemStack(IInventory inv, int slot, ItemStack item)
	{
		if (!inv.isStackValidForSlot(slot, item))
			return;
		
		ItemStack existing = inv.getStackInSlot(slot);
		if(existing == null)
		{
			if(item.stackSize >= inv.getInventoryStackLimit())
				inv.setInventorySlotContents(slot, item.splitStack(inv.getInventoryStackLimit()));
			else
			{
				inv.setInventorySlotContents(slot, item.copy());
				item.stackSize = 0;
			}
			inv.onInventoryChanged();
		}
		else if(existing.isItemEqual(item) && ItemStack.areItemStackTagsEqual(existing, item))
		{
			int toAdd = Math.min(inv.getInventoryStackLimit(), Math.min(item.stackSize, existing.getMaxStackSize() - existing.stackSize));
			existing.stackSize += toAdd;
			item.stackSize -= toAdd;
			inv.onInventoryChanged();
		}
	}
	
	public static void insertItem(ItemStack item, IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		InventoryMapper mapper = getMapper(ent);
		
		if(mapper == null)
			return;
		
		mapper.getInsertSlots(item, side ^ 1).add(item);
	}
	
	public static boolean canExtractItem(ItemStack item, IBlockAccess world, Position pos, int side)
	{
		return canExtractItem(item, world, pos.x, pos.y, pos.z, side);
	}
	
	public static boolean canExtractItem(ItemStack item, IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		InventoryMapper mapper = getMapper(ent);
		
		if(mapper == null)
			return false;
		
		return mapper.getExtractSlots(side ^ 1).canExtractAll(item);
	}
	
	public static ItemStack extractItem(IBlockAccess world, int x, int y, int z, int side, ItemStack filter)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		InventoryMapper mapper = getMapper(ent);
		
		if(mapper == null)
			return null;
		
		return mapper.getExtractSlots(side ^ 1).getAll(filter);
	}
}
