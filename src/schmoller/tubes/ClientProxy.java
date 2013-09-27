package schmoller.tubes;

import net.minecraft.tileentity.TileEntity;
import schmoller.tubes.network.ModBlockPacket;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.network.packets.ModPacketAddItem;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.Player;

public class ClientProxy extends CommonProxy
{
	@Override
	public void initialize()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileTube.class, new TileTubeRenderer());
		RenderingRegistry.registerBlockHandler(new RenderTube());
	}
	
	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
		if(packet instanceof ModBlockPacket)
		{
			TileEntity ent = FMLClientHandler.instance().getClient().theWorld.getBlockTileEntity(((ModBlockPacket)packet).xCoord, ((ModBlockPacket)packet).yCoord, ((ModBlockPacket)packet).zCoord);
			if(ent instanceof TileTube && packet instanceof ModPacketAddItem)
			{
				((TileTube)ent).addItem(((ModPacketAddItem)packet).item);
				return true;
			}
		}
		return false;
	}
}
