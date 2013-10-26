package schmoller.tubes.inventory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;

import schmoller.tubes.Position;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import com.google.common.base.Throwables;

public class InventoryHandlers
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
	
	public static IInventoryHandler getHandlerFor(IBlockAccess world, int x, int y, int z)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		
		return getHandler(ent);
	}
	
	public static IInventoryHandler getHandlerFor(IBlockAccess world, Position position)
	{
		TileEntity ent = world.getBlockTileEntity(position.x, position.y, position.z);
		
		return getHandler(ent);
	}
	
	public static IInventoryHandler getHandler(Object object)
	{
		IInventory alternate = InventoryProvider.provideFor(object);
		if(alternate != null)
			object = alternate;
		
		for(Entry<Class<?>, Constructor<? extends IInventoryHandler>> entry : mHandlers.entrySet())
		{
			if(entry.getKey().isInstance(object))
			{
				try
				{
					return entry.getValue().newInstance(object);
				}
				catch(Exception e)
				{
					Throwables.propagateIfPossible(e);
					throw new RuntimeException(e);
				}
			}
		}
		
		// This one is hard coded so that it does not get accidently tested first preventing a more relevant one to be used 
		if(object instanceof IInventory)
			return new BasicInvHandler((IInventory)object);
		
		return null;
	}
	
	static
	{
		registerHandler(ISidedInventory.class, SidedInvHandler.class);
	}
}
