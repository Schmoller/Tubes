package schmoller.tubes.gui;

import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.types.FilterTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FilterTubeContainer extends ExtContainer
{
	public FilterTubeContainer(FilterTube tube, EntityPlayer player)
	{
		for(int i = 0; i < 2; ++i)
		{
			for(int j = 0; j < 8; ++j)
				addSlotToContainer(new FilterSlot(tube, j + (i * 8), 8 + j * 18, 20 + i * 18));
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
	
	

}
