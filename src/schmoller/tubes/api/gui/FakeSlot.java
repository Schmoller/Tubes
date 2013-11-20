package schmoller.tubes.api.gui;

import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Should be used in an ExtContainer.
 * A slot that does not take items, but ghosts them.
 */
public abstract class FakeSlot extends Slot
{
	private boolean mHidden = false;
	private FluidStack mFluid;
	
	public FakeSlot(Payload initial, int x, int y)
	{
		super(new InventoryBasic("", false, 1), 0, x, y);
		
		if(initial instanceof ItemPayload)
			inventory.setInventorySlotContents(0, (ItemStack)initial.get());
		else if(initial instanceof FluidPayload)
			mFluid = (FluidStack)initial.get();
	}
	
	@Override
	public boolean canTakeStack( EntityPlayer player )
	{
		return false;
	}
	
	public void setHidden(boolean hidden)
	{
		mHidden = hidden;
	}
	
	@Override
	public ItemStack getStack() 
	{
		if(mHidden)
			return null;
		else
			return super.getStack();
	}
	
	public FluidStack getFluidStack()
	{
		if(mHidden)
			return null;
		else
			return mFluid;
	}
	
	@Override
	public ItemStack decrStackSize( int amount )
	{
		return super.decrStackSize(amount);
	}
	
	@Override
	public int getSlotStackLimit()
	{
		if(getHasStack() && getStack().itemID != 0)
			return getStack().getMaxStackSize();
		return 64;
	}
	
	@Override
	public boolean isItemValid( ItemStack par1ItemStack )
	{
		return true;
	}
	
	@Override
	public void onPickupFromSlot( EntityPlayer player, ItemStack stack )
	{
		
	}
	
	public void putFluidStack( FluidStack fluid)
	{
		if(canAcceptLiquid())
		{
			setValue(new FluidPayload(fluid));
			inventory.setInventorySlotContents(0, null);
		}
	}
	@Override
	public void putStack( ItemStack item )
	{
		if(canAcceptLiquid() && FluidContainerRegistry.isContainer(item))
		{
			ItemStack existing = getStack();
			FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(item);
			
			// If the same container is put twice, convert to the fluid itself
			if(existing != null && InventoryHelper.areItemsEqual(existing, item))
			{
				setValue(new FluidPayload(fluid));
				inventory.setInventorySlotContents(0, null);
				return;
			}
		}
		inventory.setInventorySlotContents(0, item);
		setValue(new ItemPayload(item));
	}
	
	protected abstract Payload getValue();
	protected abstract void setValue(Payload item);

	public int getMaxSize() { return 64; }
	public int getMinSize() { return 0; }
	
	public boolean canAcceptLiquid() { return false; }
}
