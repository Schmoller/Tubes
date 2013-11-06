package schmoller.tubes.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Should be used in an ExtContainer.
 * A slot that does not take items, but ghosts them.
 */
public abstract class FakeSlot extends Slot
{
	private boolean mHidden = false;
	public FakeSlot(ItemStack initial, int x, int y)
	{
		super(new InventoryBasic("", false, 1), 0, x, y);
		inventory.setInventorySlotContents(0, initial);
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
	
	@Override
	public void putStack( ItemStack item )
	{
		inventory.setInventorySlotContents(0, item);
		setValue(item);
	}
	
	protected abstract ItemStack getValue();
	protected abstract void setValue(ItemStack item);

	public int getMaxSize() { return 64; }
	public int getMinSize() { return 0; }
}
