package schmoller.tubes.inventory;

import net.minecraft.item.ItemStack;

public abstract class InventoryMapper
{
	public abstract SlotCollection getInsertSlots(ItemStack item, int side);
	public abstract SlotCollection getInsertSlots(int side);
	public abstract SlotCollection getExtractSlots(int side);
	
	
}
