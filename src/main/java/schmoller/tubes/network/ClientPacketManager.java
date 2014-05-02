package schmoller.tubes.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.FMLOutboundHandler;

public class ClientPacketManager extends PacketManager
{
	@Override
	public void sendPacketToServer(ModPacket packet)
	{
		client.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		client.writeAndFlush(packet);
	}
	
	@Override
	public void sendPacketToAllClients( ModPacket packet )
	{
		throw new IllegalStateException("Side is client!");
	}
	
	@Override
	public void sendPacketToClient( ModPacket packet, EntityPlayer player )
	{
		throw new IllegalStateException("Side is client!");
	}
	
	@Override
	public void sendPacketToWorld( ModPacket packet, World world )
	{
		throw new IllegalStateException("Side is client!");
	}
	
	@Override
	public void sendPacketToAllAround( ModPacket packet, World world, int x, int y, int z, int range )
	{
		throw new IllegalStateException("Side is client!");
	}
}
