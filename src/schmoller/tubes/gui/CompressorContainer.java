package schmoller.tubes.gui;

import java.util.Arrays;
import java.util.List;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.types.CompressorTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class CompressorContainer extends ExtContainer
{
	private CompressorTube mTube;
	public CompressorContainer(CompressorTube tube, EntityPlayer player)
	{
		addSlotToContainer(new Slot(tube, 0, 80, 25));
		
		addSlotToContainer(new CompressorTargetSlot(tube, 116, 25));
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 67 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 125));
        
        mTube = tube;
	}
	
	@Override
	public boolean canInteractWith( EntityPlayer player )
	{
		return mTube.isUseableByPlayer(player);
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

            if (slotId == 0) // from compressor buffer
            {
                if (!mergeItemStack(stack, 29, 38, false)) // To hotbar
                {
                	if (!mergeItemStack(stack, 2, 29, false)) // To main inventory
                		return null;
                }
            }
            else if(slotId >= 29 && slotId < 38) // From hotbar
            {
                if (!this.mergeItemStack(stack, 0, 1, false)) // To pipe slot
                {
                	if (!mergeItemStack(stack, 2, 29, false)) // To main inventory
                		return null;
                }
            }
            else // From main inventory
            {
            	if (!this.mergeItemStack(stack, 0, 1, false)) // To pipe slot
                {
            		if (!mergeItemStack(stack, 29, 38, false)) // To hotbar
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
	
	private static class CompressorTargetSlot extends FakeSlot
	{
		private CompressorTube mTube;
		
		public CompressorTargetSlot(CompressorTube tube, int x, int y)
		{
			super(tube.getTargetType(), x, y);
			mTube = tube;
		}
		
		@Override
		protected void setValue( IFilter filter )
		{
			if(filter == null)
			{
				filter = new AnyFilter(64, 64);
				setFilter(filter);
			}
			else
				mTube.setTargetType(filter);
		}
		
		@Override
		public List<String> getTooltip(List<String> current)
		{
			String name = null;
			if(current != null && !current.isEmpty())
				name = current.get(0);
			else
				name = "Something";
			
			int count = 64;
			if(getFilter() != null)
				count = getFilter().size();
			
			return Arrays.asList("Compressing " + name + EnumChatFormatting.RESET + " to " + count);
		}
	}

}
