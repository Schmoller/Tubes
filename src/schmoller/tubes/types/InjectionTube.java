package schmoller.tubes.types;

import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ModTubes;
import schmoller.tubes.TubeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public class InjectionTube extends BaseTube implements ISidedInventory
{
	public InjectionTube()
	{
		super("injection");
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
		addItem(itemstack, -1);
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
	public boolean isUseableByPlayer( EntityPlayer player )
	{
		return (player.getDistanceSq(x(), y(), z()) <= 25);
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
	public boolean canAddItem( ItemStack item, int direction )
	{
		return false;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		return false;
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return false;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return !(con instanceof InjectionTube);
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_INJECTION_TUBE, world(), x(), y(), z());
		return true;
	}
}
