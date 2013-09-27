package schmoller.tubes;

import java.util.logging.Logger;

import schmoller.tubes.network.PacketManager;
import schmoller.tubes.network.packets.ModPacketAddItem;

import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;

import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.Mod.Instance;

@Mod(name="Tubes", version="1.0.0", modid = "Tubes", dependencies="required-after:Forge")
@NetworkMod(clientSideRequired=true, serverSideRequired=true)
public class ModTubes
{
	@Instance("Tubes")
    public static ModTubes instance;
	
	@SidedProxy(clientSide="schmoller.tubes.ClientProxy", serverSide="schmoller.tubes.CommonProxy")
	public static CommonProxy proxy;
	
	@SidedProxy(clientSide = "schmoller.tubes.network.ClientPacketManager", serverSide = "schmoller.tubes.network.PacketManager")
	public static PacketManager packetManager;
	
	public static Logger logger = Logger.getLogger("Tubes");
	
	private int mTubeBlockId;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		// TODO: Load configurations etc.
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		mTubeBlockId = config.getBlock("TubeBlock", 2000).getInt();
	}
	
	@Init
	public void init(FMLInitializationEvent event)
	{
		packetManager.initialize("tubes");
		PacketManager.registerPacket(ModPacketAddItem.class);
		
		proxy.initialize();
		
		packetManager.registerHandler(proxy);
		
		BlockTube tube = new BlockTube(mTubeBlockId);
		GameRegistry.registerBlock(tube, "tubes:tube");
		GameRegistry.registerTileEntity(TileTube.class, "Tubes:Tube");
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		// TODO: Mod compatability
		
	}
}
