package schmoller.tubes.gui;

import schmoller.tubes.types.BufferTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BufferTubeContainer extends Container
{
	private BufferTube mTube;
	
	public BufferTubeContainer(InventoryPlayer inventory, BufferTube tube)
	{
		mTube = tube;
		
		for(int r = 0; r < 3; ++r)
		{
			for(int c = 0; c < 3; ++c)
				addSlotToContainer(new Slot(tube, c + r * 3, 62 + 18 * c, 19 + 18 * r));
		}
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 89 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 147));
		
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

            if (slotId >= 0 && slotId < 9) // From buffer
            {
                if (!mergeItemStack(stack, 36, 45, false)) // To hotbar
                {
                	if (!mergeItemStack(stack, 9, 36, false)) // To main inventory
                		return null;
                }
            }
            else if(slotId >= 36 && slotId < 45) // From hotbar
            {
                if (!mergeItemStack(stack, 0, 9, false)) // To buffer
                {
                	if (!mergeItemStack(stack, 9, 36, false)) // To main inventory
                		return null;
                }
            }
            else // From main inventory
            {
            	if (!mergeItemStack(stack, 0, 9, false)) // To buffer
                {
            		if (!mergeItemStack(stack, 36, 45, false)) // To hotbar
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
	
	@Override
	public boolean canInteractWith( EntityPlayer entityplayer )
	{
		return mTube.isUseableByPlayer(entityplayer);
	}

}
