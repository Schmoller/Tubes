package schmoller.tubes.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.ItemFilter;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.helpers.InventoryHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class SidedInvHandler implements IPayloadHandler<ItemPayload>
{
	private ISidedInventory mInv;
	
	public SidedInvHandler(ISidedInventory inventory)
	{
		mInv = inventory;
	}
	
	@Override
	public ItemPayload insert( ItemPayload payload, int side, boolean doAdd )
	{
		ItemStack remaining = ((ItemStack)payload.get()).copy();
		int[] slots = mInv.getAccessibleSlotsFromSide(side);
		
		// Try merge
		for(int i : slots)
		{
			ItemStack existing = mInv.getStackInSlot(i);
			
			if(existing == null)
				continue;
			
			if(InventoryHelper.areItemsEqual(existing, remaining))
			{
				int toAdd = Math.min(remaining.stackSize, Math.min(mInv.getInventoryStackLimit(), existing.getMaxStackSize() - existing.stackSize));
				
				if(toAdd > 0)
				{
					remaining.stackSize -= toAdd;
					if(doAdd)
						existing.stackSize += toAdd;
				}
			}
			
			if(remaining.stackSize <= 0)
			{
				if(doAdd)
					mInv.markDirty();
				return null;
			}
		}
		
		// Add the remaining into empty slots
		for(int i : slots)
		{
			ItemStack existing = mInv.getStackInSlot(i);
			
			if(existing != null)
				continue;
			
			if(mInv.canInsertItem(i, (ItemStack)payload.get(), side) && mInv.isItemValidForSlot(i, remaining))
			{
				int toAdd = Math.min(remaining.stackSize, mInv.getInventoryStackLimit());
				
				if(toAdd > 0)
				{
					remaining.stackSize -= toAdd;
					if(doAdd)
					{
						existing = remaining.copy();
						existing.stackSize = toAdd;
						mInv.setInventorySlotContents(i, existing);
					}
				}
			}
			
			if(remaining.stackSize <= 0)
			{
				if(doAdd)
					mInv.markDirty();
				return null;
			}
		}
		
		if(remaining.stackSize != payload.size() && doAdd)
			mInv.markDirty();
			
		
		// Some was left over
		return new ItemPayload(remaining);
	}

	@Override
	public ItemPayload extract( IFilter template, int side, boolean doExtract )
	{
		return extract(template, side, 0, SizeMode.Max, doExtract);
	}

	@Override
	public ItemPayload extract( IFilter template, int side, int count, SizeMode mode, boolean doExtract )
	{
		assert(template != null);
		assert(template instanceof AnyFilter || template instanceof ItemFilter);
		ItemStack pulled = null;
		
		// We just use ourself to test whether the extract would have been successful before we start doing it so we dont need to track state
		if(doExtract)
		{
			if(extract(template, side, count, mode, false) == null)
				return null;
		}
		
		int[] slots = mInv.getAccessibleSlotsFromSide(side);
		
		for(int i : slots)
		{
			ItemStack existing = mInv.getStackInSlot(i);
			
			if(existing == null || !mInv.canExtractItem(i, existing, side))
				continue;
			
			if(pulled != null && !InventoryHelper.areItemsEqual(pulled, existing))
				continue;
			
			if(template instanceof AnyFilter || template.matches(new ItemPayload(existing), SizeMode.Max))
			{
				int toGrab = 0;
				
				switch(mode)
				{
				case Max:
				case GreaterEqual:
					toGrab = Math.min(existing.stackSize, (pulled != null ? existing.getMaxStackSize() - pulled.stackSize : existing.getMaxStackSize()));
					break;
				case Exact:
				case LessEqual:
					toGrab = Math.min(existing.stackSize, (pulled != null ? count - pulled.stackSize : count));
					break;
				}

				if(pulled == null)
				{
					pulled = existing.copy();
					pulled.stackSize = toGrab;
				}
				else
					pulled.stackSize += toGrab;
				
				if(doExtract)
					mInv.decrStackSize(i, toGrab);
			}
		}

		
		if(pulled != null && !doExtract)
		{
			if(mode == SizeMode.Exact)
			{
				if(pulled.stackSize != count)
					pulled = null;
			}
			else if(mode == SizeMode.GreaterEqual)
			{
				if(pulled.stackSize < count)
					pulled = null;
			}
			else if(mode == SizeMode.LessEqual)
			{
				if(pulled.stackSize > count)
					pulled = null;
			}
		}
		
		if(pulled != null && doExtract)
			mInv.markDirty();

		return (pulled == null ? null : new ItemPayload(pulled));
	}

	private static boolean isNullOrEmpty(int[] array)
	{
		if(array == null)
			return true;
		
		return array.length == 0;
	}
	
	@Override
	public boolean isSideAccessable( int side )
	{
		return !isNullOrEmpty(mInv.getAccessibleSlotsFromSide(side));
	}
	
	@Override
	public Collection<ItemPayload> listContents( IFilter filter, int side )
	{
		assert(filter != null);
		assert(side >= 0 && side < 6);
		assert(filter instanceof AnyFilter || filter instanceof ItemFilter);
		
		ArrayList<ItemPayload> payloads = new ArrayList<ItemPayload>();
		int[] slots = mInv.getAccessibleSlotsFromSide(side);
		if(slots == null)
			return Collections.emptyList();
		
		for(int i = 0; i < slots.length; ++i)
		{
			ItemStack item = mInv.getStackInSlot(slots[i]);
			if(item != null)
			{
				ItemPayload payload = new ItemPayload(item);
				if(filter.matches(payload, SizeMode.Max))
					payloads.add(payload.copy());
			}
		}
		
		return payloads;
	}
	
	@Override
	public Collection<ItemPayload> listContents( int side )
	{
		return listContents(new AnyFilter(64), side);
	}
}
