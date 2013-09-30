package schmoller.tubes;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import schmoller.tubes.network.ModBlockPacket;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.network.packets.ModPacketAddItem;
import schmoller.tubes.render.NormalTubeRender;
import schmoller.tubes.render.RenderTubeItem;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.Player;

public class ClientProxy extends CommonProxy
{
	@Override
	public void initialize()
	{
		super.initialize();
		registerRenderers();
	}
	
	private void registerRenderers()
	{
		MinecraftForgeClient.registerItemRenderer(ModTubes.instance.itemTubeId + 256, new RenderTubeItem());
		
		NormalTubeRender normal = new NormalTubeRender();
		TubeRegistry.registerRenderer("basic",normal);
		TubeRegistry.registerRenderer("restriction",normal);
		TubeRegistry.registerRenderer("injection",normal);
	}
	
	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
		if(packet instanceof ModBlockPacket)
		{
			TileEntity ent = FMLClientHandler.instance().getClient().theWorld.getBlockTileEntity(((ModBlockPacket)packet).xCoord, ((ModBlockPacket)packet).yCoord, ((ModBlockPacket)packet).zCoord);
			ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
			
			if(con != null && packet instanceof ModPacketAddItem)
			{
				con.addItem(((ModPacketAddItem)packet).item);
				return true;
			}
		}
		return false;
	}
}
