package schmoller.tubes.gui;

import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiColorButton;
import schmoller.tubes.api.gui.GuiCounterButton;
import schmoller.tubes.api.gui.GuiEnumButton;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.types.ManagementTube;
import schmoller.tubes.types.ManagementTube.ManagementMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ManagementTubeContainer extends ExtContainer
{
	public ManagementTubeContainer(ManagementTube tube, EntityPlayer player)
	{
		for(int i = 0; i < 6; ++i)
		{
			for(int j = 0; j < 8; ++j)
				addSlotToContainer(new FilterSlot(tube, i, j, 8 + j * 18, 20 + i * 18));
		}
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 144 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 202));
        
        addButtonToContainer(new GuiEnumButton<ManagementMode>(tube, ManagementTube.PROP_MODE, ManagementMode.class, 153, 19, 176, 0, "gui.managementtube.mode.%s"));
        addButtonToContainer(new GuiCounterButton(tube, ManagementTube.PROP_PRIORITY, 0, 10, 153, 49, "gui.managementtube.priority"));
        addButtonToContainer(new GuiColorButton(tube, ManagementTube.PROP_COLOR, 153, 65));
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

        if (slotId >= 48 && slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ret = stack.copy();

            if(slotId >= 75 && slotId < 84) // From hotbar
            {
                if (!mergeItemStack(stack, 48, 75, false)) // To main inventory
                	return null;
            }
            else // From main inventory
            {
            	if (!mergeItemStack(stack, 75, 84, false)) // To hotbar
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
	
	public class FilterSlot extends FakeSlot
	{
		private ManagementTube mTube;
		private int mRow;
		private int mColumn;
		
		public FilterSlot(ManagementTube tube, int row, int column, int x, int y)
		{
			super(tube.getFilter(column, row), x, y);
			mTube = tube;
			mRow = row;
			mColumn = column;
		}
		
		@Override
		protected void setValue( IFilter item )
		{
			mTube.setFilter(mColumn, mRow, item);
		}
		
		@Override
		public boolean shouldRespectSizes()
		{
			return false;
		}
		
		@Override
		public boolean filterNeedsPayload() { return true; }
	}
}
