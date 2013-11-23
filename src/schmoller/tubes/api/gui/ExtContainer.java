package schmoller.tubes.api.gui;

import java.util.ArrayList;

import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.interfaces.IFilter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Extended container provides the ability to use fake slots
 */
public abstract class ExtContainer extends Container
{
	private ArrayList<FluidStack> inventoryFluidStacks = new ArrayList<FluidStack>();
	
	@Override
	protected Slot addSlotToContainer( Slot par1Slot )
	{
		inventoryFluidStacks.add(null);
		return super.addSlotToContainer(par1Slot);
	}
	
	@Override
	public ItemStack slotClick( int slotId, int mouseButton, int modifier, EntityPlayer player )
	{
		if(slotId >= 0 && slotId < inventorySlots.size())
		{
			if(inventorySlots.get(slotId) instanceof FakeSlot)
			{
				FakeSlot slot = (FakeSlot)inventorySlots.get(slotId);
				ItemStack held = player.inventory.getItemStack();
				
				ItemStack existingItem = slot.getStack();
				if(existingItem != null)
					existingItem = existingItem.copy();
				
				if(mouseButton != 2 || !slot.resetFilter())
				{
					IFilter existing = slot.getFilter();
					IFilter newFilter = FilterRegistry.getInstance().createFilter(held, existing, mouseButton, GuiScreen.isShiftKeyDown(), GuiScreen.isCtrlKeyDown());
					
					if(newFilter == null && existing != null)
					{
						if(mouseButton == 0) // Decrease
							existing.decrease(GuiScreen.isShiftKeyDown());
						else if(mouseButton == 1) // Increase
							existing.increase(slot.shouldRespectSizes(), GuiScreen.isShiftKeyDown());
						
						if(existing.size() == 0)
						{
							if(!slot.resetFilter())
								slot.setFilter(null);
						}
					}
					else if(newFilter != null)
						slot.setFilter(newFilter);
					
					slot.onSlotChanged();
				}
				return existingItem;
			}
		}
		
		return super.slotClick(slotId, mouseButton, modifier, player);
	}
}
