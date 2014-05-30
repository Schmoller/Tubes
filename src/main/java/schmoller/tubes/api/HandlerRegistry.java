package schmoller.tubes.api;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	private static HashMap<Class<? extends Payload>, HandlerMap> mHandlers = new HashMap<Class<? extends Payload>, HandlerMap>();
	
	public static void registerHandler(Class<? extends Payload> payloadClass, Class<?> handledClass, Class<? extends IPayloadHandler> handlerClass)
	{
		HandlerMap map = mHandlers.get(payloadClass);
		if(map == null)
		{
			map = new HandlerMap();
			mHandlers.put(payloadClass, map);
		}
		
		try
		{
			Constructor<? extends IPayloadHandler> constructor = handlerClass.getConstructor(handledClass);
			map.add(handledClass, constructor);
		}
		catch(Exception e)
		{
			System.out.println("Failed to register inventory handler '" + handlerClass.getName() + "' for '" + handledClass.getName() + "'");
			e.printStackTrace();
		}
	}
	
	public static IPayloadHandler<?> getHandler(Class<? extends Payload> payloadType, Object object)
	{
		HandlerMap handlers = mHandlers.get(payloadType);
		
		if(handlers == null)
			return null;
		
		return handlers.getHandler(object);
	}
	
	static
	{
		registerHandler(ItemPayload.class, IInventory.class, BasicInvHandler.class);
		registerHandler(ItemPayload.class, ISidedInventory.class, SidedInvHandler.class);
		
		registerHandler(FluidPayload.class, IFluidHandler.class, BasicFluidHandler.class);
	}
	
	private static class Handler implements Comparable<Handler>
	{
		public final Class<?> handledClass;
		public final Constructor<? extends IPayloadHandler> handlerClass;
		
		public Handler(Class<?> handledClass, Constructor<? extends IPayloadHandler> handlerClass)
		{
			this.handledClass = handledClass;
			this.handlerClass = handlerClass;
		}

		@Override
		public int compareTo( Handler other )
		{
			if(handledClass.equals(other.handledClass))
				return 0;
			if(handledClass.isAssignableFrom(other.handledClass))
				return 1;
			else
				return -1;
		}
		
		@Override
		public String toString()
		{
			return handledClass.getName();
		}
	}
	
	private static class HandlerMap
	{
		private ArrayList<Handler> mHandlers = new ArrayList<Handler>();
		
		public void add(Class<?> handledClass, Constructor<? extends IPayloadHandler> handlerClass)
		{
			mHandlers.add(new Handler(handledClass, handlerClass));
			Collections.sort(mHandlers);
		}
		
		public IPayloadHandler getHandler(Object object)
		{
			try
			{
				for(Handler handler : mHandlers)
				{
					if(handler.handledClass.isInstance(object))
						return handler.handlerClass.newInstance(object);
				}
			}
			catch(Exception e)
			{
				Throwables.propagateIfPossible(e);
				throw new RuntimeException(e);
			}
			
			return null;
		}
	}
}
