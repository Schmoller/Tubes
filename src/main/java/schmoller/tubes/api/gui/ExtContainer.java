package schmoller.tubes.api.gui;

import java.util.ArrayList;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.interfaces.IFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
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
	public boolean canDragIntoSlot( Slot slot )
	{
		if(slot instanceof FakeSlot)
			return false;

		return super.canDragIntoSlot(slot);
	}
	
	public void dropItem( int slotId, int mouseButton, int modifier, ItemStack held )
	{
		if(slotId >= 0 && slotId < inventorySlots.size())
		{
			if(inventorySlots.get(slotId) instanceof FakeSlot)
				handleFakeSlot((FakeSlot)inventorySlots.get(slotId), mouseButton, modifier, held);
		}
		
		detectAndSendChanges();
	}
	
	private void handleFakeSlot( FakeSlot slot, int mouseButton, int modifier, ItemStack held)
	{
		if(mouseButton != 2 || !slot.resetFilter())
		{
			boolean shift = (modifier & 1) != 0;
			boolean ctrl = (modifier & 2) != 0;
			
			IFilter existing = slot.getFilter();
			IFilter newFilter = FilterRegistry.getInstance().createFilter(held, existing, mouseButton, shift, ctrl, slot.filterNeedsPayload());
			
			if(newFilter == null && existing != null)
			{
				if(mouseButton == 0) // Decrease
					existing.decrease(shift);
				else if(mouseButton == 1) // Increase
					existing.increase(slot.shouldRespectSizes(), shift);
				
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
	}
	
	@Override
	public ItemStack slotClick( int slotId, int mouseButton, int modifier, EntityPlayer player )
	{
		if(slotId >= 0 && slotId < inventorySlots.size())
		{
			if(inventorySlots.get(slotId) instanceof FakeSlot)
			{
				ItemStack existingItem = ((Slot)inventorySlots.get(slotId)).getStack();
				if(existingItem != null)
					existingItem = existingItem.copy();
				
				handleFakeSlot((FakeSlot)inventorySlots.get(slotId), mouseButton, modifier, player.inventory.getItemStack());
				
				return existingItem;
			}
		}
		
		return super.slotClick(slotId, mouseButton, modifier, player);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		for (int i = 0; i < inventorySlots.size(); ++i)
        {
			Slot rawSlot = (Slot)this.inventorySlots.get(i);
            ItemStack current = rawSlot.getStack();
            ItemStack old = (ItemStack)this.inventoryItemStacks.get(i);

            boolean changed = false;
            if(rawSlot instanceof FakeSlot)
            {
            	IFilter oldFilter = FakeSlot.fromItem(old);
            	IFilter currentFilter = ((FakeSlot)rawSlot).getFilter();
            	
            	if((currentFilter != null && (oldFilter == null || !currentFilter.equals(oldFilter))) || (currentFilter == null && oldFilter != null))
            		changed = true;
            }
            
            if (changed || !ItemStack.areItemStacksEqual(old, current))
            {
                old = current == null ? null : current.copy();
                inventoryItemStacks.set(i, old);

                for (int j = 0; j < crafters.size(); ++j)
                {
                    ((ICrafting)crafters.get(j)).sendSlotContents(this, i, old);
                }
            }
        }
	}
}
