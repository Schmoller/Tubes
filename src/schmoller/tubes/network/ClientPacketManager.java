package schmoller.tubes.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.PacketDispatcher;

public class ClientPacketManager extends PacketManager
{
	@Override
	public void sendPacketToServer(ModPacket packet)
	{
		PacketDispatcher.sendPacketToServer(toPacket(packet));
	}
	
	@Override
	public void sendPacketToAllClients( ModPacket packet )
	{
	}
	
	@Override
	public void sendPacketToClient( ModPacket packet, EntityPlayer player )
	{
	}
	
	@Override
	public void sendPacketToWorld( ModPacket packet, World world )
	{
	}
}
