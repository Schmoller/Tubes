package schmoller.tubes.gui;

import java.util.Arrays;
import java.util.List;

import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.types.CompressorTube;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

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
		protected Payload getValue()
		{
			return mTube.getTargetType();
		}
		
		@Override
		protected void setValue( Payload item )
		{
			if(item == null)
			{
				item = new ItemPayload(new ItemStack(0, 64, 0));
				putStack((ItemStack)item.get());
			}
			else
				mTube.setTargetType(item);
		}
		
		@Override
		public int getMaxSize()
		{
			if(getHasStack())
				return getStack().getMaxStackSize();
			return 64;
		}
		
		@Override
		public int getMinSize()
		{
			return 2;
		}
		
		@Override
		public boolean canAcceptLiquid()
		{
			return true;
		}
		
		@Override
		public List<String> getTooltip()
		{
			Payload payload = getValue();
			if(payload instanceof ItemPayload)
			{
				ItemStack item = (ItemStack)payload.get();
				if(item.itemID == 0)
					return Arrays.asList("Compressing any stack to " + item.stackSize + " items.");
				else
					return Arrays.asList("Compressing " + item.getDisplayName() + " to " + item.stackSize + " items.");
			}
			else if(payload instanceof FluidPayload)
			{
				FluidStack fluid = (FluidStack)payload.get();
				
				return Arrays.asList("Compressing " + I18n.getString(fluid.getFluid().getUnlocalizedName()) + " to " + fluid.amount + "MB.");
			}
			
			return null;
		}
	}

}
