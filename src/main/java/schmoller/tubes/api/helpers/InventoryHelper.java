package schmoller.tubes.api.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryHelper
{
	public static boolean areItemsEqual(ItemStack item1, ItemStack item2)
	{
		return (item1 == null && item2 == null) || (item1 != null && item2 != null && item1.isItemEqual(item2) && ItemStack.areItemStackTagsEqual(item1, item2));
	}
	
	public static void mergeItemStackSimulate(IInventory inv, int slot, ItemStack item)
	{
		if (!inv.isItemValidForSlot(slot, item))
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
		if (!inv.isItemValidForSlot(slot, item))
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
			inv.markDirty();
		}
		else if(existing.isItemEqual(item) && ItemStack.areItemStackTagsEqual(existing, item))
		{
			int toAdd = Math.min(inv.getInventoryStackLimit(), Math.min(item.stackSize, existing.getMaxStackSize() - existing.stackSize));
			existing.stackSize += toAdd;
			item.stackSize -= toAdd;
			inv.markDirty();
		}
	}
}
