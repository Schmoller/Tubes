package schmoller.tubes.inventory.providers;

import schmoller.tubes.api.interfaces.IInventoryProvider;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntityChest;

public class DoubleChestProvider implements IInventoryProvider
{
	@Override
	public IInventory provide( Object object )
	{
		if(object instanceof TileEntityChest)
		{
			TileEntityChest chest = (TileEntityChest)object;
			
			TileEntityChest adjacent = null;
			
			if (chest.adjacentChestXNeg != null)
				adjacent = chest.adjacentChestXNeg;  
			
			if (chest.adjacentChestXPos != null)
				adjacent = chest.adjacentChestXPos;  
			
			if (chest.adjacentChestZNeg != null)
				adjacent = chest.adjacentChestZNeg;  
			
			if (chest.adjacentChestZPosition != null)
				adjacent = chest.adjacentChestZPosition;  
			
			if (adjacent != null)
				return new InventoryLargeChest("", chest, adjacent);
		}
		
		return null;
	}

}
