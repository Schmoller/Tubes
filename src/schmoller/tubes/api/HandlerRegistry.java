package schmoller.tubes.api;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;

import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.inventory.BasicFluidHandler;
import schmoller.tubes.inventory.BasicInvHandler;
import schmoller.tubes.inventory.SidedInvHandler;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.base.Throwables;

public class HandlerRegistry
{
	private static HashMap<Class<? extends Payload>, HashMap<Class<?>, Constructor<? extends IPayloadHandler>>> mHandlers = new HashMap<Class<? extends Payload>, HashMap<Class<?>,Constructor<? extends IPayloadHandler>>>();
	
	public static void registerHandler(Class<? extends Payload> payloadClass, Class<?> handledClass, Class<? extends IPayloadHandler> handlerClass)
	{
		HashMap<Class<?>, Constructor<? extends IPayloadHandler>> map = mHandlers.get(payloadClass);
		if(map == null)
		{
			map = new HashMap<Class<?>, Constructor<? extends IPayloadHandler>>();
			mHandlers.put(payloadClass, map);
		}
		
		try
		{
			Constructor<? extends IPayloadHandler> constructor = handlerClass.getConstructor(handledClass);
			map.put(handledClass, constructor);
		}
		catch(Exception e)
		{
			System.out.println("Failed to register inventory handler '" + handlerClass.getName() + "' for '" + handledClass.getName() + "'");
			e.printStackTrace();
		}
	}
	
	private static int getDistance(Class<?> from, Class<?> to)
	{
		int dist = 0;
		
		while(from != null && from != Object.class)
		{
			if(from.equals(to))
				return dist;
			
			for(Class<?> clazz : from.getInterfaces())
			{
				if(clazz.equals(to))
					return dist;
			}
			
			from = from.getSuperclass();
			++dist;
		}
		
		return -1;
	}
	
	public static IPayloadHandler<?> getHandler(Class<? extends Payload> payloadType, Object object)
	{
		HashMap<Class<?>, Constructor<? extends IPayloadHandler>> handlers = mHandlers.get(payloadType);
		
		if(handlers == null)
			return null;
		
		int minDist = Integer.MAX_VALUE;
		Constructor<? extends IPayloadHandler> min = null;
		
		for(Entry<Class<?>, Constructor<? extends IPayloadHandler>> entry : handlers.entrySet())
		{
			int dist = getDistance(object.getClass(), entry.getKey());
			
			if(dist != -1 && dist < minDist)
			{
				minDist = dist;
				min = entry.getValue();
			}
		}
		
		if(min != null)
		{
			try
			{
				return min.newInstance(object);
			}
			catch(Exception e)
			{
				Throwables.propagateIfPossible(e);
				throw new RuntimeException(e);
			}
		}
		
		return null;
	}
	
	static
	{
		registerHandler(ItemPayload.class, IInventory.class, BasicInvHandler.class);
		registerHandler(ItemPayload.class, ISidedInventory.class, SidedInvHandler.class);
		
		registerHandler(FluidPayload.class, IFluidHandler.class, BasicFluidHandler.class);
	}
}
