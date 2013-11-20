package schmoller.tubes.gui;

import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.types.RequestingTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RequestingTubeContainer extends ExtContainer
{
	public RequestingTubeContainer(RequestingTube tube, EntityPlayer player)
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
	
	
	private static class FilterSlot extends FakeSlot
	{
		private RequestingTube mTube;
		private int mIndex;
		public FilterSlot( RequestingTube tube, int index, int x, int y )
		{
			super(new ItemPayload(tube.getFilter(index)), x, y);
			mTube = tube;
			mIndex = index;
		}

		@Override
		protected Payload getValue()
		{
			return new ItemPayload(mTube.getFilter(mIndex));
		}

		@Override
		protected void setValue( Payload item )
		{
			mTube.setFilter(mIndex, (ItemStack)item.get());
		}
		
	}
	

}
