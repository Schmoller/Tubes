package schmoller.tubes;

import net.minecraft.inventory.ISidedInventory;

public class InventoryInfo
{
	public InventoryInfo(ISidedInventory inv, int start)
	{
		inventory = inv;
		this.start = start;
	}
	
	public ISidedInventory inventory;
	public int start;
}
