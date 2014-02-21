package schmoller.tubes.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketManager implements IPacketHandler
{
	protected String mChannel;
	
	protected static HashMap<Class<? extends ModPacket>, Integer> mTypeMap = new HashMap<Class<? extends ModPacket>, Integer>();
	protected static HashMap<Integer, Class<? extends ModPacket>> mTypeMapRev = new HashMap<Integer, Class<? extends ModPacket>>();
	
	private static int sNextId = 0;

	private static HashMap<IModPacketHandler, Class<? extends ModPacket>[]> mHandlerFilters = new HashMap<IModPacketHandler, Class<? extends ModPacket>[]>();
	private static HashSet<IModPacketHandler> mHandlers = new HashSet<IModPacketHandler>();
	
	public static int registerPacket(Class<? extends ModPacket> type)
	{
		if(mTypeMap.containsKey(type))
			throw new RuntimeException("Packet already registered!");
		
		mTypeMap.put(type, sNextId);
		mTypeMapRev.put(sNextId, type);
		sNextId++;
		
		return sNextId-1;
	}
	
	@SuppressWarnings( "unchecked" )
	public static void registerHandler(IModPacketHandler handler, Class<? extends ModPacket>... filter)
	{
		mHandlers.add(handler);
		
		if(filter != null && filter.length != 0)
			mHandlerFilters.put(handler, filter);
	}
	
	public static void deregisterHandler(IModPacketHandler handler)
	{
		mHandlers.remove(handler);
		mHandlerFilters.remove(handler);
	}
	
	public void initialize(String channel)
	{
		mChannel = channel;
		NetworkRegistry.instance().registerChannel(this, channel);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	protected Packet250CustomPayload toPacket(ModPacket packet)
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(bytes);
		
		if(!mTypeMap.containsKey(packet.getClass()))
			throw new RuntimeException("ModPacket '" + packet.getClass().getName() +"' has not been registered!");
		
		int id = mTypeMap.get(packet.getClass());
		try
		{
			output.write(id);
			packet.write(output);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		Packet250CustomPayload finalPacket = new Packet250CustomPayload();
		finalPacket.channel = mChannel;
		finalPacket.isChunkDataPacket = false;
		finalPacket.data = bytes.toByteArray();
		finalPacket.length = finalPacket.data.length;
		
		return finalPacket;
	}
	
	
	public void sendPacketToServer(ModPacket packet) {}

	public void sendPacketToClient(ModPacket packet, EntityPlayer player) 
	{
		PacketDispatcher.sendPacketToPlayer(toPacket(packet), (Player)player);
	}
	
	public void sendPacketToAllClients(ModPacket packet) 
	{
		PacketDispatcher.sendPacketToAllPlayers(toPacket(packet));
	}
	
	public void sendPacketForBlock(ModBlockPacket packet, World world)
	{
		PacketDispatcher.sendPacketToAllAround(packet.xCoord, packet.yCoord, packet.zCoord, 200, world.provider.dimensionId, toPacket(packet));
	}
	
	public void sendPacketToWorld(ModPacket packet, World world) 
	{
		PacketDispatcher.sendPacketToAllInDimension(toPacket(packet), world.provider.dimensionId);
	}
	
	@ForgeSubscribe
	private void onServerStop(FMLServerStoppingEvent event)
	{
		mHandlers.clear();
		mHandlerFilters.clear();
	}
	
	
	private ModPacket getPacket(int id)
	{
		try
		{
			Class<? extends ModPacket> clazz = mTypeMapRev.get(id);
			
			if(clazz == null)
				return null;
			
			return clazz.newInstance();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch ( InstantiationException e )
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void onPacketData( INetworkManager manager,	Packet250CustomPayload packet, Player player )
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(packet.data);
		DataInputStream input = new DataInputStream(stream);
		
		try
		{
			// Read the packet
			int id = input.readByte();
			
			ModPacket modPacket = getPacket(id);
			
			if(modPacket == null)
			{
				FMLLog.warning(mChannel + " got bad packet id %d", id);
				return;
			}
			
			modPacket.read(input);
			
			// Send to handlers
			Iterator<IModPacketHandler> it = mHandlers.iterator();
			
			while(it.hasNext())
			{
				IModPacketHandler handler = it.next();
				
				// Check the filter
				Class<? extends ModPacket>[] filter = mHandlerFilters.get(handler);
				if(filter != null)
				{
					boolean found = false;
					for(Class<? extends ModPacket> clazz : filter)
					{
						if(clazz.isInstance(modPacket))
						{
							found = true;
							break;
						}
					}
					if(!found)
						continue;
				}
				
				// Try handle
				if(handler.onPacketArrive(modPacket, player))
					break;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
	}

}
