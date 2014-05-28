package schmoller.tubes.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import schmoller.tubes.RedstoneMode;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiColorButton;
import schmoller.tubes.api.gui.GuiEnumButton;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.types.AdvancedExtractionTube;
import schmoller.tubes.types.AdvancedExtractionTube.PullMode;

public class AdvancedExtractionTubeContainer extends ExtContainer
{
	private AdvancedExtractionTube mTube;
	private int mLastNext = -1;
	
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
        
        addButtonToContainer(new GuiEnumButton<RedstoneMode>(tube, AdvancedExtractionTube.PROP_REDSTONEMODE, RedstoneMode.class, 153, 19, 176, 0, "gui.redstonemode.%s"));
        addButtonToContainer(new GuiEnumButton<PullMode>(tube, AdvancedExtractionTube.PROP_PULLMODE, PullMode.class, 153, 35, 190, 0, "gui.pullmode.%s"));
        addButtonToContainer(new GuiEnumButton<SizeMode>(tube, AdvancedExtractionTube.PROP_SIZEMODE, SizeMode.class, 153, 51, 204, 0, "gui.requestingtube.size.%s"));
        addButtonToContainer(new GuiColorButton(tube, AdvancedExtractionTube.PROP_COLOR, 153, 67));
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
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		int next = mTube.getNext();
		if(mLastNext != next)
		{
			mLastNext = next;
			for (int j = 0; j < crafters.size(); ++j)
                ((ICrafting)crafters.get(j)).sendProgressBarUpdate(this, 0, next);
		}
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void updateProgressBar( int id, int value )
	{
		if(id == 0)
		{
			mTube.setNext(value);
		}
		else
			super.updateProgressBar(id, value);
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
