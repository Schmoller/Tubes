package schmoller.tubes.inventory;

import java.util.Arrays;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class MapperBasic extends InventoryMapper
{
	private IInventory mInventory;
	public MapperBasic(IInventory inventory)
	{
		mInventory = inventory;
	}
	
	@Override
	public SlotCollection getInsertSlots( ItemStack item, int side )
	{
		Slot[] slots = new Slot[mInventory.getSizeInventory()];
		
		int max = 0;
		
		for(int i = 0; i < mInventory.getSizeInventory(); ++i)
		{
			if(mInventory.isStackValidForSlot(i, item))
				slots[max++] = new Slot(i);
		}
		
		slots = Arrays.copyOf(slots, max);
		return new SlotCollection(mInventory, slots, side);
	}

	@Override
	public SlotCollection getInsertSlots( int side )
	{
		Slot[] slots = new Slot[mInventory.getSizeInventory()];
		
		for(int i = 0; i < mInventory.getSizeInventory(); ++i)
			slots[i] = new Slot(i);
		
		return new SlotCollection(mInventory, slots, side);
	}

	@Override
	public SlotCollection getExtractSlots( int side )
	{
		Slot[] slots = new Slot[mInventory.getSizeInventory()];
		
		for(int i = 0; i < mInventory.getSizeInventory(); ++i)
			slots[i] = new Slot(i);
		
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
		public ItemStack decreaseStack( int amount )
		{
			if(isEmpty())
				return null;
			
			if(amount >= getStack().stackSize)
				amount = getStack().stackSize;
			
			return mInventory.decrStackSize(mIndex, amount);
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
			return canHold(item);
		}

		@Override
		public boolean canExtract( int side )
		{
			return !isEmpty();
		}
		
		@Override
		public boolean isEmpty()
		{
			return getStack() == null;
		}
	}
}
