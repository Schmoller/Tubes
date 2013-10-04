package schmoller.tubes.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotCollection
{
	private ISlot[] mSlots;
	private int mSide;
	
	private IInventory mInventory;
	
	public SlotCollection(IInventory inv, ISlot[] slots, int side)
	{
		mSlots = slots;
		mSide = side;
		mInventory = inv;
	}
	
	public ItemStack add(ItemStack item)
	{
		for(ISlot slot : mSlots)
		{
			if(slot.canHold(item) && slot.canInsert(item, mSide))
				slot.addStack(item, true);
			
			if(item.stackSize == 0)
			{
				item = null;
				break;
			}
		}
		
		mInventory.onInventoryChanged();
		
		return item;
	}
	
	public ItemStack getFirst()
	{
		for(ISlot slot : mSlots)
		{
			if(slot.canExtract(mSide) && slot.getStack() != null)
			{
				ItemStack item = slot.getStack();
				slot.setStack(null);

				mInventory.onInventoryChanged();
				return item;
			}
		}
		
		return null;
	}
	
	public ItemStack getFirst(ItemStack template)
	{
		for(ISlot slot : mSlots)
		{
			if(slot.canExtract(mSide) && slot.getStack() != null)
			{
				ItemStack item = slot.getStack();
				if(template == null || InventoryHelper.areItemsEqual(item, template))
				{
					slot.setStack(null);

					mInventory.onInventoryChanged();
					return item;
				}
			}
		}
		
		return null;
	}
	
	public ItemStack simulateAdd(ItemStack item)
	{
		for(ISlot slot : mSlots)
		{
			if(slot.canHold(item) && slot.canInsert(item, mSide))
				slot.addStack(item, false);
			
			if(item.stackSize == 0)
				return null;
		}
		
		return item;
	}
	
	public boolean canAddAny(ItemStack item)
	{
		return (simulateAdd(item.copy()) == null);
	}
	
	public boolean canExtractAny(ItemStack filter)
	{
		for(ISlot slot : mSlots)
		{
			if(slot.canExtract(mSide) && slot.getStack() != null)
			{
				ItemStack item = slot.getStack();
				if(filter == null || InventoryHelper.areItemsEqual(item, filter))
					return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty()
	{
		return mSlots.length == 0;
	}
	
	public int size()
	{
		return mSlots.length;
	}
}
