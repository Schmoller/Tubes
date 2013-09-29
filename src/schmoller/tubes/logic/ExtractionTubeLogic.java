package schmoller.tubes.logic;

import schmoller.tubes.ITube;
import schmoller.tubes.TubeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class ExtractionTubeLogic extends TubeLogic implements ISidedInventory
{
	private ITube mTube;
	
	public ExtractionTubeLogic(ITube tube)
	{
		mTube = tube;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot( int i )
	{
		return null;
	}

	@Override
	public ItemStack decrStackSize( int i, int j )
	{
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int i )
	{
		return null;
	}

	@Override
	public void setInventorySlotContents( int i, ItemStack itemstack )
	{
		mTube.addItem(itemstack, -1);
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
		return 64;
	}

	@Override
	public void onInventoryChanged()
	{
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer entityplayer )
	{
		return false;
	}

	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}

	@Override
	public boolean isStackValidForSlot( int i, ItemStack itemstack )
	{
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide( int var1 )
	{
		return new int[] {0};
	}

	@Override
	public boolean canInsertItem( int i, ItemStack itemstack, int j )
	{
		return true;
	}

	@Override
	public boolean canExtractItem( int i, ItemStack itemstack, int j )
	{
		return false;
	}

	@Override
	public boolean canPathThrough()
	{
		return false;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item, int side )
	{
		return false;
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return false;
	}
}
