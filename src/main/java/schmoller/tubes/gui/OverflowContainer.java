package schmoller.tubes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.TubeItem;

public class OverflowContainer extends Container
{
	public OverflowContainer(OverflowBuffer buffer)
	{
		InventoryBasic inv = new InventoryBasic("", false, 1);
		populateInventory(inv, buffer);
		addSlotToContainer(new Slot(inv, 0, Integer.MIN_VALUE, Integer.MIN_VALUE));
	}
	
	private void populateInventory(InventoryBasic inv, OverflowBuffer buffer)
	{
		NBTTagCompound root = new NBTTagCompound();
		NBTTagList items = new NBTTagList();
		for(TubeItem item : buffer.peekItems())
		{
			NBTTagCompound tag = new NBTTagCompound();
			item.writeToNBT(tag);
			items.appendTag(tag);
		}
		
		root.setTag("Stuck", items);
		
		ItemStack item = new ItemStack(Blocks.stone);
		item.setTagCompound(root);
		
		inv.setInventorySlotContents(0, item);
	}

	@Override
	public boolean canInteractWith( EntityPlayer var1 )
	{
		return true;
	}
}
