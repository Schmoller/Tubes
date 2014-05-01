package schmoller.tubes.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import schmoller.tubes.api.client.IPayloadRender;

public class PayloadRegistry
{
	private static PayloadRegistry mInstance;
	
	private PayloadRegistry() {}
	
	public static PayloadRegistry instance()
	{
		if(mInstance == null)
			mInstance = new PayloadRegistry();
		
		return mInstance;
	}
	
	private HashMap<Class<? extends Payload>, PayloadType> mTypes = new HashMap<Class<? extends Payload>, PayloadType>();
	private HashMap<Short, Class<? extends Payload>> mIdToPayload = new HashMap<Short, Class<? extends Payload>>();
	private HashBiMap<Class<? extends Payload>, String> mPayloadToName = HashBiMap.create();
	
	
	@SideOnly(Side.CLIENT)
	public static void registerPayloadRenderer(String type, IPayloadRender render)
	{
		assert(render != null);
		instance().getPayload(type).render = render;
	}
	
	public static void registerPayload(Class<? extends Payload> payloadClass, String name, Class<?> interfaceClass)
	{
		PayloadType type = new PayloadType();
		type.interfaceClass = interfaceClass;
		type.payloadClass = payloadClass;
		type.type = name;
		
		instance().mTypes.put(payloadClass, type);
		instance().mIdToPayload.put(type.index, payloadClass);
		instance().mPayloadToName.put(payloadClass, name);
	}
	
	public PayloadType getPayload(Class<? extends Payload> payloadClass)
	{
		return mTypes.get(payloadClass);
	}
	
	public PayloadType getPayload(String type)
	{
		return mTypes.get(mPayloadToName.inverse().get(type));
	}
	
	public PayloadType getPayload(short id)
	{
		return mTypes.get(mIdToPayload.get(id));
	}
	
	public Set<Class<? extends Payload>> getPayloadTypes()
	{
		return Collections.unmodifiableSet(mTypes.keySet());
	}
	
	@SideOnly(Side.CLIENT)
	public IPayloadRender getPayloadRender(Class<? extends Payload> payloadClass)
	{
		return mTypes.get(payloadClass).render;
	}
	
	public static class PayloadType
	{
		private static short nextIndex = 0;
		
		public PayloadType()
		{
			index = nextIndex++;
		}
		
		public Class<?> payloadClass;
		
		public String type;
		
		/**
		 * the interface used for working with this payload.
		 * For example, ItemPayload has IInventory, and FluidPayload has IFluidHandler
		 */
		public Class<?> interfaceClass;
		
		public IPayloadRender render;
		
		public short index = 0;
		
		public Payload newInstance()
		{
			try
			{
				return (Payload)payloadClass.newInstance();
			}
			catch(Exception e)
			{
				Throwables.propagateIfPossible(e);
				throw new RuntimeException(e);
			}
		}
	}
}
