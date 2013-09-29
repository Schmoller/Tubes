package schmoller.tubes.parts;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryTubePart extends BaseTubePart implements ISidedInventory
{
	public InventoryTubePart(String type)
	{
		super(type);
	}
	
	@Override
	public int getSizeInventory()
	{
		return ((ISidedInventory)getLogic()).getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot( int i )
	{
		return ((ISidedInventory)getLogic()).getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize( int i, int j )
	{
		return ((ISidedInventory)getLogic()).decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int i )
	{
		return ((ISidedInventory)getLogic()).getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents( int i, ItemStack itemstack )
	{
		((ISidedInventory)getLogic()).setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName()
	{
		return null;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return ((ISidedInventory)getLogic()).getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged()
	{
		((ISidedInventory)getLogic()).onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer entityplayer )
	{
		return ((ISidedInventory)getLogic()).isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest()
	{
		((ISidedInventory)getLogic()).openChest();
	}

	@Override
	public void closeChest()
	{
		((ISidedInventory)getLogic()).openChest();
	}

	@Override
	public boolean isStackValidForSlot( int i, ItemStack itemstack )
	{
		return ((ISidedInventory)getLogic()).isStackValidForSlot(i, itemstack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide( int var1 )
	{
		return ((ISidedInventory)getLogic()).getAccessibleSlotsFromSide(var1);
	}

	@Override
	public boolean canInsertItem( int i, ItemStack itemstack, int j )
	{
		return ((ISidedInventory)getLogic()).canInsertItem(i, itemstack, j);
	}

	@Override
	public boolean canExtractItem( int i, ItemStack itemstack, int j )
	{
		return ((ISidedInventory)getLogic()).canExtractItem(i, itemstack, j);
	}

}
