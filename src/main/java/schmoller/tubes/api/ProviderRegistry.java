package schmoller.tubes.api;

import java.util.HashMap;

import schmoller.tubes.api.interfaces.IInterfaceProvider;
import schmoller.tubes.inventory.providers.CauldronProvider;
import schmoller.tubes.inventory.providers.DoubleChestProvider;
import schmoller.tubes.inventory.providers.FurnaceInventoryProvider;
import schmoller.tubes.inventory.providers.JukeboxProvider;

import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.IFluidHandler;

public class ProviderRegistry
{
	private static HashMap<Class<?>, HashMap<Object, IInterfaceProvider>> mProviders = new HashMap<Class<?>, HashMap<Object,IInterfaceProvider>>();
	
	static
	{
		// Alters the furnace behaviour so you can extract smartly from any side except top which extracts from all
		registerProvider(IInventory.class, TileEntityFurnace.class, new FurnaceInventoryProvider());
		
		// Allows double chests to be treated as one
		registerProvider(IInventory.class, TileEntityChest.class, new DoubleChestProvider());
		
		// Allows jukeboxes to be interacted with tubes
		registerProvider(IInventory.class, TileEntityJukebox.class, new JukeboxProvider());
		
		registerProvider(IFluidHandler.class, Blocks.cauldron, new CauldronProvider());
	}
	
	/**
	 * Gets any interfaces registered for this object
	 */
	public static Object provideFor(Class<?> interfaceClass, Object object)
	{
		if(object == null)
			return null;
		
		HashMap<Object, IInterfaceProvider> providers = mProviders.get(interfaceClass);
		
		if(providers == null)
			return null;
		
		IInterfaceProvider provider = null;
		
		if(object instanceof BlockInstance)
			provider = providers.get(((BlockInstance)object).getBlock());
		else
			provider = providers.get(object.getClass());
		
		if(provider != null)
			return provider.provide(object);
		else if(!(object instanceof BlockInstance))
		{
			for(Object clazz : providers.keySet())
			{
				if(!(clazz instanceof Class))
					continue;
				
				if(((Class<?>)clazz).isAssignableFrom(object.getClass()))
					return providers.get(clazz).provide(object);
			}
		}
			
		return null;
	}
	
	/**
	 * Sometimes blocks,parts, or entities have different interfaces than you want. In these cases you can register an interface provider
	 * that provides an alternate interface than the one present on it normally.
	 * @param interfaceClass The base interface being provided. Eg. IInventory for inventories, IFluidHandler for tanks
	 * @param classOrBlock The class or block instance to be provided for
	 * @param provider The class that provides interfaces to use
	 */
	public static void registerProvider(Class<?> interfaceClass, Object classOrBlock, IInterfaceProvider provider)
	{
		if(!(classOrBlock instanceof Class) && !(classOrBlock instanceof Block))
			throw new IllegalArgumentException();
		
		HashMap<Object, IInterfaceProvider> providers = mProviders.get(interfaceClass);
		
		if(providers == null)
		{
			providers = new HashMap<Object, IInterfaceProvider>();
			mProviders.put(interfaceClass, providers);
		}
		
		providers.put(classOrBlock, provider);
	}
}
