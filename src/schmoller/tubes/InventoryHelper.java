package schmoller.tubes;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;

public class InventoryHelper
{
	public static boolean canAcceptItem(ItemStack item, IBlockAccess world, ChunkPosition pos, int side)
	{
		return canAcceptItem(item, world, pos.x, pos.y, pos.z, side);
	}
	public static boolean canAcceptItem(ItemStack item, IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		if(ent instanceof ISidedInventory)
		{
			ISidedInventory inv = (ISidedInventory)ent;
			
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				if(inv.canInsertItem(slot, item, side ^ 1))
				{
					if(canSlotAccept(inv, slot, item))
						return true;
				}
			}
		}
		else if(ent instanceof IInventory)
		{
			IInventory inv = (IInventory)ent;
			
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				if(canSlotAccept(inv, slot, item))
					return true;
			}
		}
		
		return false;
	}
	
	public static boolean canSlotAccept(IInventory inv, int slot, ItemStack item)
	{
		if (!inv.isStackValidForSlot(slot, item))
			return false;
		
		ItemStack existing = inv.getStackInSlot(slot);
		if(existing == null)
			return true;
		else if(existing.isItemEqual(item) && ItemStack.areItemStackTagsEqual(existing, item))
			return Math.min(inv.getInventoryStackLimit(), Math.min(item.stackSize, existing.getMaxStackSize() - existing.stackSize)) > 0;

		return false;
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
		
		if(ent instanceof ISidedInventory)
		{
			ISidedInventory inv = (ISidedInventory)ent;
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				if(item.stackSize == 0)
					break;
				
				if(inv.canInsertItem(slot, item, side ^ 1))
					mergeItemStack(inv, slot, item);
			}
		}
		else if(ent instanceof IInventory)
		{
			IInventory inv = (IInventory)ent;
			
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				if(item.stackSize == 0)
					break;
				
				mergeItemStack(inv, slot, item);
			}
		}
	}
	
	public static boolean canExtractItem(ItemStack item, IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		if(ent instanceof ISidedInventory)
		{
			ISidedInventory inv = (ISidedInventory)ent;
			
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				ItemStack existing = inv.getStackInSlot(slot);
				
				if(existing != null && inv.canExtractItem(slot, existing, side ^ 1) && (item == null || (existing.isItemEqual(item) && ItemStack.areItemStackTagsEqual(existing, item))))
					return true;
			}
		}
		else if(ent instanceof IInventory)
		{
			IInventory inv = (IInventory)ent;
			
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				ItemStack existing = inv.getStackInSlot(slot);
				
				if(existing != null && (item == null || (existing.isItemEqual(item) && ItemStack.areItemStackTagsEqual(existing, item))))
					return true;
			}
		}
		
		return false;
	}
	
	public static ItemStack extractItem(IBlockAccess world, int x, int y, int z, int side, ItemStack filter)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		if(ent instanceof ISidedInventory)
		{
			ISidedInventory inv = (ISidedInventory)ent;
			
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				ItemStack existing = inv.getStackInSlot(slot);
				
				if(existing != null && inv.canExtractItem(slot, existing, side ^ 1) && (filter == null || (existing.isItemEqual(filter) && ItemStack.areItemStackTagsEqual(existing, filter))))
				{
					inv.setInventorySlotContents(slot, null);
					return existing;
				}
			}
		}
		else if(ent instanceof IInventory)
		{
			IInventory inv = (IInventory)ent;
			
			for(int slot = 0; slot < inv.getSizeInventory(); ++slot)
			{
				ItemStack existing = inv.getStackInSlot(slot);
				
				if(existing != null && (filter == null || (existing.isItemEqual(filter) && ItemStack.areItemStackTagsEqual(existing, filter))))
				{
					inv.setInventorySlotContents(slot, null);
					return existing;
				}
			}
		}
		
		return null;
	}
}
