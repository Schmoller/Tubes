package schmoller.tubes.gui;

import schmoller.tubes.logic.FilterTubeLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FilterTubeContainer extends Container
{
	public FilterTubeContainer(FilterTubeLogic tube, EntityPlayer player)
	{
		for(int i = 0; i < 2; ++i)
		{
			for(int j = 0; j < 8; ++j)
				addSlotToContainer(new FakeSlot(tube, j + (i * 8), 8 + j * 18, 20 + i * 18));
		}
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 72 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 130));
	}
	
	@Override
	public boolean canInteractWith( EntityPlayer entityplayer )
	{
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot( EntityPlayer player, int slotId )
	{
		ItemStack ret = null;
        Slot slot = (Slot)inventorySlots.get(slotId);

        if (slotId >= 16 && slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ret = stack.copy();

            if(slotId >= 43 && slotId < 52) // From hotbar
            {
                if (!mergeItemStack(stack, 16, 43, false)) // To main inventory
                	return null;
            }
            else // From main inventory
            {
            	if (!mergeItemStack(stack, 43, 52, false)) // To hotbar
                	return null;
            }

            if (stack.stackSize == 0)
                slot.putStack((ItemStack)null);
            else
                slot.onSlotChanged();

            if (ret.stackSize == stack.stackSize)
                return null;

            slot.onPickupFromSlot(player, ret);
        }

        return ret;
	}
	
	@Override
	public ItemStack slotClick( int slotId, int mouseButton, int modifier, EntityPlayer player )
	{
		if(slotId >= 0 && slotId < 16)
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
					slot.putStack(held.copy());
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
		else
			return super.slotClick(slotId, mouseButton, modifier, player);
	}

}
