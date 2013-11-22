package schmoller.tubes.api.gui;

import java.util.ArrayList;

import schmoller.tubes.api.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
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
				
				if((slot.getStack() != null && slot.getFluidStack() == null) || held != null)
				{
					ItemStack existing = slot.getStack();
					if(existing != null)
						existing = existing.copy();
					
					if(mouseButton == 2) // Middle Click (Clear Slot)
						slot.putStack(null);
					else if(mouseButton == 0) // Left Click
					{
						if(held != null && (existing == null || (!held.isItemEqual(slot.getStack()) || !ItemStack.areItemStackTagsEqual(held, slot.getStack())))) // Replace with this one
						{
							ItemStack put = held.copy();
							put.stackSize = Math.min(put.stackSize, slot.getSlotStackLimit());
							slot.putStack(put);
						}
						else if(slot.getStack() != null) //  Decrease Slot
						{
							// If the same container is put twice, convert to the fluid itself
							if(InventoryHelper.areItemsEqual(held, existing) && slot.canAcceptLiquid() && FluidContainerRegistry.isContainer(held))
							{
								FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(held);
								slot.putFluidStack(fluid);
							}
							else
							{
								int amount = (modifier == 1 ? 10 : 1); // Shift?
								ItemStack item = slot.getStack();
								item.stackSize -= amount;
								if(item.stackSize <= 0)
									slot.putStack(null);
								else
									slot.putStack(item);
							}
						}
					}
					else if(mouseButton == 1) // Right Click
					{
						if(held != null && (existing == null || (!held.isItemEqual(slot.getStack()) || !ItemStack.areItemStackTagsEqual(held, slot.getStack())))) // Replace with this one
						{
							held = held.copy();
							held.stackSize = 1;
							slot.putStack(held);
						}
						else if(slot.getStack() != null) //  Increase Slot
						{
							// If the same container is put twice, convert to the fluid itself
							if(InventoryHelper.areItemsEqual(held, existing) && slot.canAcceptLiquid() && FluidContainerRegistry.isContainer(held))
							{
								FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(held);
								slot.putFluidStack(fluid);
							}
							else
							{
								int amount = (modifier == 1 ? 10 : 1); // Shift?
								ItemStack item = slot.getStack();
								item.stackSize += amount;
								if(item.stackSize >= slot.getSlotStackLimit())
									item.stackSize = slot.getSlotStackLimit();
								
								slot.putStack(item);
							}
						}
					}
					
					slot.onSlotChanged();
					
					return existing;
				}
				else if(slot.getFluidStack() != null)
				{
					FluidStack existing = slot.getFluidStack();
					
					if(mouseButton == 2) // Middle Click (Clear Slot)
						slot.putStack(null);
					else if(mouseButton == 0) // Left Click (Decrease Slot)
					{
						int amount = (modifier == 1 ? 250 : 125); // Shift?
						FluidStack fluid = existing.copy();
						fluid.amount -= amount;
						if(fluid.amount <= 0)
							slot.putStack(null);
						else
							slot.putFluidStack(fluid);
					}
					else if(mouseButton == 1) // Right Click (Increase Slot)
					{
						int amount = (modifier == 1 ? 250 : 125); // Shift?
						FluidStack fluid = existing.copy();
						fluid.amount += amount;
						if(fluid.amount >= 1000)
							fluid.amount = 1000;
						
						slot.putFluidStack(fluid);
					}
					
					slot.onSlotChanged();
					
					return null;
				}
			}
		}
		
		return super.slotClick(slotId, mouseButton, modifier, player);
	}
}
