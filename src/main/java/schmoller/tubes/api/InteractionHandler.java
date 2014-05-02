package schmoller.tubes.api;

import java.util.ArrayList;

import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.inventory.AnyHandler;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class InteractionHandler
{
	/**
	 * Can a tube do something with this block?
	 * @param side The side from a block to the specified one
	 */
	public static boolean isInteractable(IBlockAccess world, int x, int y, int z, int side)
	{
		IPayloadHandler handler = getHandler(null, world, x, y, z);
		
		if(handler == null)
			return false;
		
		return handler.isSideAccessable(side ^ 1);
	}

	private static IPayloadHandler buildMultiHandler(IBlockAccess world, int x, int y, int z)
	{
		ArrayList<IPayloadHandler> handlers = new ArrayList<IPayloadHandler>();
		for(Class<? extends Payload> payloadClass : PayloadRegistry.instance().getPayloadTypes())
		{
			IPayloadHandler handler = getHandler(payloadClass, world, x, y, z);
			if(handler != null)
				handlers.add(handler);
		}
		
		return new AnyHandler(handlers);
	}
	
	public static IPayloadHandler getHandler(Class<? extends Payload> payloadClass, IBlockAccess world, Position position )
	{
		return getHandler(payloadClass, world, position.x, position.y, position.z);
	}
	
	public static IPayloadHandler getHandler(Class<? extends Payload> payloadClass, IBlockAccess world, int x, int y, int z)
	{
		if(payloadClass == null)
			return buildMultiHandler(world, x, y, z);
		
		Block block = world.getBlock(x, y, z);
		
		Class<?> interfaceClass = PayloadRegistry.instance().getPayload(payloadClass).interfaceClass;
		
		Object interfaceObject;
		if(block != null)
		{
			BlockInstance object = new BlockInstance(world, x, y, z);
			interfaceObject = ProviderRegistry.provideFor(interfaceClass, object);
			if(interfaceObject != null)
				return HandlerRegistry.getHandler(payloadClass, interfaceObject);
		}
		
		TileEntity ent = world.getTileEntity(x, y, z);
		
		interfaceObject = ProviderRegistry.provideFor(interfaceClass, ent);
		if(interfaceObject != null)
			return HandlerRegistry.getHandler(payloadClass, interfaceObject);
		
		if(ent instanceof TileMultipart)
		{
			TMultiPart part = ((TileMultipart)ent).partMap(6);
			
			if(part != null)
			{
				interfaceObject = ProviderRegistry.provideFor(interfaceClass, part);
				if(interfaceObject != null)
					return HandlerRegistry.getHandler(payloadClass, interfaceObject);
			}
		}
		
		if(interfaceClass.isInstance(ent))
			return HandlerRegistry.getHandler(payloadClass, ent);
		
		return null;
	}
}
