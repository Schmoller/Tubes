package schmoller.tubes.gui;

import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.types.RoutingTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RoutingTubeContainer extends ExtContainer
{
	private RoutingTube mTube;
	
	public RoutingTubeContainer(RoutingTube tube, EntityPlayer player)
	{
		mTube = tube;
		for(int i = 0; i < 4; ++i)
		{
			for(int j = 0; j < 9; ++j)
				addSlotToContainer(new FilterSlot(j, i, 8 + j * 18, 20 + i * 18));
		}
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 138 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 196));
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
	
	private class FilterSlot extends FakeSlot
	{
		private int mColumn;
		private int mRow;
		public FilterSlot(int column, int row, int x, int y)
		{
			super(mTube.getFilter(column, row), x, y);
			mColumn = column;
			mRow = row;
		}
		
		@Override
		protected void setValue( IFilter filter )
		{
			mTube.setFilter(mColumn, mRow, filter);
		}
		
		@Override
		public int getSlotStackLimit()
		{
			return 1;
		}
		
		@Override
		public boolean filterNeedsPayload() { return false; }
	}

}
