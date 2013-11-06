package schmoller.tubes.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/**
 * Extended container provides the ability to use fake slots
 */
public abstract class ExtContainer extends Container
{
	@Override
	public ItemStack slotClick( int slotId, int mouseButton, int modifier, EntityPlayer player )
	{
		if(slotId >= 0 && slotId < inventorySlots.size())
		{
			if(inventorySlots.get(slotId) instanceof FakeSlot)
			{
				FakeSlot slot = (FakeSlot)inventorySlots.get(slotId);
				
				ItemStack existing = slot.getStack();
				if(existing != null)
					existing = existing.copy();
				
				if(mouseButton == 2) // Middle Click (Clear Slot)
					slot.putStack(null);
				else if(mouseButton == 0) // Left Click
				{
					ItemStack held = player.inventory.getItemStack(); 
					if(held != null && (existing == null || (!held.isItemEqual(slot.getStack()) || !ItemStack.areItemStackTagsEqual(held, slot.getStack())))) // Replace with this one
					{
						ItemStack put = held.copy();
						put.stackSize = Math.min(put.stackSize, slot.getSlotStackLimit());
						slot.putStack(put);
					}
					else if(slot.getStack() != null) //  Decrease Slot
					{
						int amount = (modifier == 1 ? 10 : 1); // Shift?
						ItemStack item = slot.getStack();
						item.stackSize -= amount;
						if(item.stackSize <= 0)
							slot.putStack(null);
						else
							slot.putStack(item);
					}
				}
				else if(mouseButton == 1) // Right Click
				{
					ItemStack held = player.inventory.getItemStack(); 
					if(held != null && (existing == null || (!held.isItemEqual(slot.getStack()) || !ItemStack.areItemStackTagsEqual(held, slot.getStack())))) // Replace with this one
					{
						held = held.copy();
						held.stackSize = 1;
						slot.putStack(held);
					}
					else if(slot.getStack() != null) //  Increase Slot
					{
						int amount = (modifier == 1 ? 10 : 1); // Shift?
						ItemStack item = slot.getStack();
						item.stackSize += amount;
						if(item.stackSize >= slot.getSlotStackLimit())
							item.stackSize = slot.getSlotStackLimit();
						
						slot.putStack(item);
					}
				}
				
				slot.onSlotChanged();
				
				return existing;
			}
		}
		
		return super.slotClick(slotId, mouseButton, modifier, player);
	}
}
