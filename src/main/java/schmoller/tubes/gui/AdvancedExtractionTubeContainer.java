package schmoller.tubes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.types.AdvancedExtractionTube;

public class AdvancedExtractionTubeContainer extends ExtContainer
{
	private AdvancedExtractionTube mTube;
	
	public AdvancedExtractionTubeContainer(AdvancedExtractionTube tube, EntityPlayer player)
	{
		mTube = tube;
		
		for(int r = 0; r < 3; ++r)
		{
			for(int c = 0; c < 8; ++c)
				addSlotToContainer(new FilterSlot(c + (r * 8), 8 + (18 * c), 20 + (18 * r)));
		}
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 90 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 148));
	}
	
	@Override
	public boolean canInteractWith( EntityPlayer player )
	{
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot( EntityPlayer player, int slotId )
	{
		ItemStack ret = null;
        Slot slot = (Slot)inventorySlots.get(slotId);

        if (slotId >= 24 && slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ret = stack.copy();

            if(slotId >= 51 && slotId < 60) // From hotbar
            {
                if (!mergeItemStack(stack, 24, 51, false)) // To main inventory
                	return null;
            }
            else // From main inventory
            {
            	if (!mergeItemStack(stack, 51, 60, false)) // To hotbar
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

	private class FilterSlot extends FakeSlot
	{
		private int mSlot;
		public FilterSlot(int slot, int x, int y)
		{
			super(mTube.getFilter(slot), x, y);
			mSlot = slot;
		}
		
		@Override
		protected void setValue( IFilter filter )
		{
			mTube.setFilter(mSlot, filter);
		}
		
		@Override
		public boolean filterNeedsPayload()
		{
			return true;
		}
	}
}
