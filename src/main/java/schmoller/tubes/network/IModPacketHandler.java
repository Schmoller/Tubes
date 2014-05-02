package schmoller.tubes.network;

import net.minecraft.entity.player.EntityPlayer;

public interface IModPacketHandler
{
	public boolean onPacketArrive(ModPacket packet, EntityPlayer player);
}
