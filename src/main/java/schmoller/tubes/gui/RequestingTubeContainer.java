package schmoller.tubes.gui;

import schmoller.tubes.PullMode;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiColorButton;
import schmoller.tubes.api.gui.GuiEnumButton;
import schmoller.tubes.api.interfaces.IFilter;
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
        
        addButtonToContainer(new GuiEnumButton<PullMode>(tube, RequestingTube.PROP_MODE, PullMode.class, 153, 19, 176, 0, "gui.requestingtube.mode.%s"));
        addButtonToContainer(new GuiEnumButton<SizeMode>(tube, RequestingTube.PROP_SIZEMODE, SizeMode.class, 153, 35, 190, 0, "gui.requestingtube.size.%s"));
        addButtonToContainer(new GuiColorButton(tube, RequestingTube.PROP_COLOR, 153, 51));
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
			super(tube.getFilter(index), x, y);
			mTube = tube;
			mIndex = index;
		}

		@Override
		protected void setValue( IFilter filter )
		{
			mTube.setFilter(mIndex, filter);
		}
		
	}
	

}
