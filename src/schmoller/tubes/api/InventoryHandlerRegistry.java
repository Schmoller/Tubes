package schmoller.tubes.api;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;

import schmoller.tubes.api.interfaces.IInventoryHandler;
import schmoller.tubes.inventory.BasicInvHandler;
import schmoller.tubes.inventory.SidedInvHandler;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import com.google.common.base.Throwables;

public class InventoryHandlerRegistry
{
	private static HashMap<Class<?>, Constructor<? extends IInventoryHandler>> mHandlers = new HashMap<Class<?>, Constructor<? extends IInventoryHandler>>();
	
	public static void registerHandler(Class<?> handledClass, Class<? extends IInventoryHandler> handlerClass)
	{
		try
		{
			Constructor<? extends IInventoryHandler> constructor = handlerClass.getConstructor(handledClass);
			mHandlers.put(handledClass, constructor);
		}
		catch(Exception e)
		{
			System.out.println("Failed to register inventory handler '" + handlerClass.getName() + "' for '" + handledClass.getName() + "'");
			e.printStackTrace();
		}
	}
	
	public static IInventoryHandler getHandler(IInventory inventory)
	{
		for(Entry<Class<?>, Constructor<? extends IInventoryHandler>> entry : mHandlers.entrySet())
		{
			if(entry.getKey().isInstance(inventory))
			{
				try
				{
					return entry.getValue().newInstance(inventory);
				}
				catch(Exception e)
				{
					Throwables.propagateIfPossible(e);
					throw new RuntimeException(e);
				}
			}
		}
		
		// This one is hard coded so that it does not get accidently tested first preventing a more relevant one to be used 
		return new BasicInvHandler(inventory);
	}
	
	static
	{
		registerHandler(ISidedInventory.class, SidedInvHandler.class);
	}
}
