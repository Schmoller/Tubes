package schmoller.tubes.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

@Sharable
public class PacketManager extends SimpleChannelInboundHandler<ModPacket>
{
	protected String mChannel;
	private int sNextId = 0;

	private HashMap<IModPacketHandler, Class<? extends ModPacket>[]> mHandlerFilters = new HashMap<IModPacketHandler, Class<? extends ModPacket>[]>();
	private HashSet<IModPacketHandler> mHandlers = new HashSet<IModPacketHandler>();
	private EnumMap<Side, FMLEmbeddedChannel> mChannels;
	
	protected FMLEmbeddedChannel client;
	protected FMLEmbeddedChannel server;
	
	private ModPacketCodec mCodec;
	
	public void registerPacket(Class<? extends ModPacket> type)
	{
		mCodec.addDiscriminator(sNextId++, type);
	}
	
	@SuppressWarnings( "unchecked" )
	public void registerHandler(IModPacketHandler handler, Class<? extends ModPacket>... filter)
	{
		mHandlers.add(handler);
		
		if(filter != null && filter.length != 0)
			mHandlerFilters.put(handler, filter);
	}
	
	public void deregisterHandler(IModPacketHandler handler)
	{
		mHandlers.remove(handler);
		mHandlerFilters.remove(handler);
	}
	
	public void initialize(String channel)
	{
		mChannel = channel;
		mCodec = new ModPacketCodec();
		mChannels = NetworkRegistry.INSTANCE.newChannel(channel, mCodec);
		client = mChannels.get(Side.CLIENT);
		server = mChannels.get(Side.SERVER);
		
		MinecraftForge.EVENT_BUS.register(this);
		
		setupHandler(client);
		setupHandler(server);
	}
	
	private void setupHandler(FMLEmbeddedChannel channel)
	{
		String codec = channel.findChannelHandlerNameForType(ModPacketCodec.class);
		channel.pipeline().addAfter(codec, "Handler", this);
	}
	
	public void sendPacketToServer(ModPacket packet) 
	{
		throw new IllegalStateException("Side is server!");
	}

	public void sendPacketToClient(ModPacket packet, EntityPlayer player) 
	{
		server.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		server.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		server.writeAndFlush(packet);
	}
	
	public void sendPacketToAllClients(ModPacket packet) 
	{
		server.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		server.writeAndFlush(packet);
	}
	
	public void sendPacketToAllAround(ModPacket packet, World world, int x, int y, int z, int range) 
	{
		server.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		server.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, range));
		server.writeAndFlush(packet);
	}
	
	public void sendPacketToWorld(ModPacket packet, World world) 
	{
		server.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		server.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(world.provider.dimensionId);
		server.writeAndFlush(packet);
	}
	
	@SubscribeEvent
	private void onServerStop(FMLServerStoppingEvent event)
	{
		mHandlers.clear();
		mHandlerFilters.clear();
	}
	
	
	@Override
	protected void channelRead0( ChannelHandlerContext ctx, ModPacket packet ) throws Exception
	{
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
					if(clazz.isInstance(packet))
					{
						found = true;
						break;
					}
				}
				if(!found)
					continue;
			}
			
			EntityPlayer player = null;
			
			if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
	            player = ((NetHandlerPlayServer)netHandler).playerEntity;
			}
			
			// Try handle
			if(handler.onPacketArrive(packet, player))
				break;
		}
	}
}
