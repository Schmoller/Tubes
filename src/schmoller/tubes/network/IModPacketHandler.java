package schmoller.tubes.network;

import cpw.mods.fml.common.network.Player;

public interface IModPacketHandler
{
	public boolean onPacketArrive(ModPacket packet, Player sender);
}
