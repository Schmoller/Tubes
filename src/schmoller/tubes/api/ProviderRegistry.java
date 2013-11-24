package schmoller.tubes.api;

import java.util.HashMap;

import schmoller.tubes.api.interfaces.IFluidHandlerProvider;
import schmoller.tubes.api.interfaces.IInventoryProvider;
import schmoller.tubes.inventory.providers.DoubleChestProvider;
import schmoller.tubes.inventory.providers.FurnaceInventoryProvider;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.IFluidHandler;

public class ProviderRegistry
{
	private static HashMap<Class<?>, IInventoryProvider> mProviders = new HashMap<Class<?>, IInventoryProvider>();
	private static HashMap<Class<?>, IFluidHandlerProvider> mFluidProviders = new HashMap<Class<?>, IFluidHandlerProvider>();
	
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
	 * Gets any alternative fluid handlers registered for this object
	 */
	public static IFluidHandler provideFluidHandlerFor(Object object)
	{
		if(object == null)
			return null;
		
		IFluidHandlerProvider provider = mFluidProviders.get(object.getClass());
		
		if(provider != null)
			return provider.provide(object);
		else
		{
			for(Class<?> clazz : mFluidProviders.keySet())
			{
				if(clazz.isAssignableFrom(object.getClass()))
					return mFluidProviders.get(object.getClass()).provide(object);
			}
		}
			
		return null;
	}
	
	/**
	 * Sometimes blocks,parts, or entities have different interfaces than you want. In these cases you can register an inventory provider
	 * that is used instead of the one present on it normally.
	 * @param clazz The class of the object to be provided for
	 * @param provider The class that provides inventories to use
	 */
	public static void registerProvider(Class<?> clazz, IInventoryProvider provider)
	{
		mProviders.put(clazz, provider);
	}
	
	/**
	 * Sometimes blocks,parts, or entities have different interfaces than you want. In these cases you can register a fluid handler provider
	 * that is used instead of the one present on it normally.
	 * @param clazz The class of the object to be provided for
	 * @param provider The class that provides fluid handler to use
	 */
	public static void registerProvider(Class<?> clazz, IFluidHandlerProvider provider)
	{
		mFluidProviders.put(clazz, provider);
	}
	
}
