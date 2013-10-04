package schmoller.tubes.inventory;

import java.util.HashMap;

import schmoller.tubes.inventory.providers.DoubleChestProvider;
import schmoller.tubes.inventory.providers.FurnaceInventoryProvider;
import schmoller.tubes.inventory.providers.IInventoryProvider;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;

public class InventoryProvider
{
	private static HashMap<Class<?>, IInventoryProvider> mProviders = new HashMap<Class<?>, IInventoryProvider>();
	
	static
	{
		// Alters the furnace behaviour so you can extract smartly from any side except top which extracts from all
		registerProvider(TileEntityFurnace.class, new FurnaceInventoryProvider());
		
		// Allows double chests to be treated as one
		registerProvider(TileEntityChest.class, new DoubleChestProvider());
	}
	
	/**
	 * Gets any alternative inventories registered for this object
	 */
	public static IInventory provideFor(Object object)
	{
		if(object == null)
			return null;
		
		IInventoryProvider provider = mProviders.get(object.getClass());
		
		if(provider != null)
			return provider.provide(object);
		else
		{
			for(Class<?> clazz : mProviders.keySet())
			{
				if(clazz.isAssignableFrom(object.getClass()))
					return mProviders.get(object.getClass()).provide(object);
			}
		}
			
		return null;
	}
	
	/**
	 * Sometimes blocks,parts, or entities have different interfaces than you want. In these cases you can register an inventory provider
	 * Than is used instead of the one present on it normally.
	 * @param clazz The class of the object to be provided for
	 * @param provider The class that provides inventories to use
	 */
	public static void registerProvider(Class<?> clazz, IInventoryProvider provider)
	{
		mProviders.put(clazz, provider);
	}
	
}
