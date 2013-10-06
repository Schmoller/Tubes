package schmoller.tubes.inventory;

import net.minecraft.item.ItemStack;

public interface ISlot
{
	public ItemStack getStack();
	public ItemStack decreaseStack(int amount);
	
	public void setStack(ItemStack item);
	
	public void addStack(ItemStack item, boolean doAdd);
	
	public boolean canHold(ItemStack item);
	
	public boolean canInsert(ItemStack item, int side);
	
	public boolean canExtract(int side);
	
	public boolean isEmpty();
}