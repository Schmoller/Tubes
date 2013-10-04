package schmoller.tubes.inventory.providers;

import net.minecraft.inventory.IInventory;

public interface IInventoryProvider
{
	public IInventory provide(Object object);
}
