package schmoller.tubes.gui;

import schmoller.tubes.parts.InventoryTubePart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InjectionTubeContainer extends Container
{
	private InventoryTubePart mTube;
	
	public InjectionTubeContainer(InventoryTubePart tube, EntityPlayer player)
	{
		mTube = tube;
		addSlotToContainer(new Slot(tube, 0, 80, 20));
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 54 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 112));
	}
	@Override
	public boolean canInteractWith( EntityPlayer entityplayer )
	{
		return mTube.isUseableByPlayer(entityplayer);
	}
	
	@Override
	public ItemStack transferStackInSlot( EntityPlayer player, int slotId )
	{
		ItemStack ret = null;
        Slot slot = (Slot)inventorySlots.get(slotId);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ret = stack.copy();

            if (slotId == 0) // from pipe slot
            {
                if (!mergeItemStack(stack, 28, 37, false)) // To hotbar
                {
                	if (!mergeItemStack(stack, 1, 28, false)) // To main inventory
                		return null;
                }
            }
            else if(slotId >= 28 && slotId < 37) // From hotbar
            {
                if (!this.mergeItemStack(stack, 0, 1, false)) // To pipe slot
                {
                	if (!mergeItemStack(stack, 1, 28, false)) // To main inventory
                		return null;
                }
            }
            else // From main inventory
            {
            	if (!this.mergeItemStack(stack, 0, 1, false)) // To pipe slot
                {
            		if (!mergeItemStack(stack, 28, 37, false)) // To hotbar
                		return null;
                }
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
