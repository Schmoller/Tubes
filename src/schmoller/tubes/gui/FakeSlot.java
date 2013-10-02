package schmoller.tubes.gui;

import schmoller.tubes.logic.FilterTubeLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FakeSlot extends Slot
{
	private FilterTubeLogic mTube;
	private int mIndex;
	
	public FakeSlot( FilterTubeLogic tube, int index, int x, int y )
	{
		super(new InventoryBasic("", false, 1), 0, x, y);
		mTube = tube;
		mIndex = index;
		inventory.setInventorySlotContents(0, tube.getFilter(index));
	}
	
	@Override
	public boolean canTakeStack( EntityPlayer player )
	{
		return false;
	}
	
	@Override
	public ItemStack decrStackSize( int amount )
	{
		return super.decrStackSize(amount);
	}
	
	@Override
	public int getSlotStackLimit()
	{
		if(getHasStack())
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
		mTube.setFilter(mIndex, item);
	}

}
