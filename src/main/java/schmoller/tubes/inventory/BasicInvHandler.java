package schmoller.tubes.inventory;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.ItemFilter;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.helpers.InventoryHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BasicInvHandler implements IPayloadHandler<ItemPayload>
{
	private IInventory mInv;
	public BasicInvHandler(IInventory inventory)
	{
		mInv = inventory;
	}
	
	@Override
	public ItemPayload insert( ItemPayload payload, int side, boolean doAdd )
	{
		ItemStack remaining = ((ItemStack)payload.get()).copy();
		
		// Try merge
		for(int i = 0; i < mInv.getSizeInventory(); ++i)
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
					mInv.onInventoryChanged();
				return null;
			}
		}
		
		// Add the remaining into empty slots
		for(int i = 0; i < mInv.getSizeInventory(); ++i)
		{
			ItemStack existing = mInv.getStackInSlot(i);
			
			if(existing != null)
				continue;
			
			if(mInv.isItemValidForSlot(i, remaining))
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
					mInv.onInventoryChanged();
				return null;
			}
		}
		
		if(remaining.stackSize != payload.size() && doAdd)
			mInv.onInventoryChanged();
			
		
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
		
		for(int i = 0; i < mInv.getSizeInventory(); ++i)
		{
			ItemStack existing = mInv.getStackInSlot(i);
			
			if(existing == null)
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
			mInv.onInventoryChanged();

		return (pulled == null ? null : new ItemPayload(pulled));
	}
	
	@Override
	public boolean isSideAccessable( int side )
	{
		return true;
	}
}
