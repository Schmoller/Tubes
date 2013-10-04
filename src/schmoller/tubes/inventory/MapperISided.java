package schmoller.tubes.inventory;

import java.util.Arrays;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class MapperISided extends InventoryMapper
{
	private ISidedInventory mInventory;
	
	public MapperISided(ISidedInventory inventory)
	{
		mInventory = inventory;
	}
	
	@Override
	public SlotCollection getInsertSlots( ItemStack item, int side )
	{
		int[] slotIds = mInventory.getAccessibleSlotsFromSide(side);
		Slot[] slots = new Slot[slotIds.length];
		int max = 0;
		for(int i = 0; i < slotIds.length; ++i)
		{
			if(mInventory.canInsertItem(slotIds[i], item, side))
				slots[max++] = new Slot(slotIds[i]);
		}
		
		slots = Arrays.copyOf(slots, max);
		return new SlotCollection(mInventory, slots, side);
	}

	@Override
	public SlotCollection getInsertSlots( int side )
	{
		int[] slotIds = mInventory.getAccessibleSlotsFromSide(side);
		Slot[] slots = new Slot[slotIds.length];
		for(int i = 0; i < slotIds.length; ++i)
			slots[i] = new Slot(slotIds[i]);
		
		return new SlotCollection(mInventory, slots, side);
	}

	@Override
	public SlotCollection getExtractSlots( int side )
	{
		int[] slotIds = mInventory.getAccessibleSlotsFromSide(side);
		Slot[] slots = new Slot[slotIds.length];
		for(int i = 0; i < slotIds.length; ++i)
			slots[i] = new Slot(slotIds[i]);
		
		return new SlotCollection(mInventory, slots, side);
	}
	
	public class Slot implements ISlot
	{
		private int mIndex;
		
		public Slot(int index)
		{
			mIndex = index;
		}
		
		@Override
		public ItemStack getStack()
		{
			return mInventory.getStackInSlot(mIndex);
		}

		@Override
		public void setStack( ItemStack item )
		{
			mInventory.setInventorySlotContents(mIndex, item);
		}

		@Override
		public void addStack( ItemStack item, boolean doAdd )
		{
			if(doAdd)
				InventoryHelper.mergeItemStack(mInventory, mIndex, item);
			else
				InventoryHelper.mergeItemStackSimulate(mInventory, mIndex, item);
		}

		@Override
		public boolean canHold( ItemStack item )
		{
			return mInventory.isStackValidForSlot(mIndex, item);
		}

		@Override
		public boolean canInsert( ItemStack item, int side )
		{
			return mInventory.canInsertItem(mIndex, item, side);
		}

		@Override
		public boolean canExtract( int side )
		{
			if(isEmpty())
				return false;
			
			return mInventory.canExtractItem(mIndex, getStack(), side);
		}
		
		@Override
		public boolean isEmpty()
		{
			return getStack() == null;
		}
	}

}
