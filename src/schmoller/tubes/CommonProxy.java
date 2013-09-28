
package schmoller.tubes;

import cpw.mods.fml.common.network.Player;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModPacket;

public class CommonProxy implements IModPacketHandler
{
	public void initialize()
	{
		
	}

	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
		return false;
	}
}
