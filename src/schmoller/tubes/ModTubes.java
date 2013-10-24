package schmoller.tubes;

import java.util.logging.Logger;

import schmoller.tubes.network.PacketManager;
import schmoller.tubes.network.packets.ModPacketSetColor;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import schmoller.tubes.network.packets.ModPacketSetPullMode;
import schmoller.tubes.network.packets.ModPacketSetRoutingOptions;
import schmoller.tubes.parts.ItemTubeBase;
import schmoller.tubes.render.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;

import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.Instance;

@Mod(name="Tubes", version="1.0.0", modid = "Tubes", dependencies="required-after:Forge; required-before:CCMultipart")
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
	
	public int itemTubeId;
	public int itemDustPlasticId;
	public int itemSheetPlasticId;
	public int itemMilkCurdBucketId;
	public int itemBucketPlasticId;
	
	public int blockPlasticId;
	
	public static ItemTubeBase itemTube;
	
	public static Item itemDustPlastic;
	public static Item itemSheetPlastic;
	public static Item itemMilkCurdBucket;
	public static Item itemBucketPlastic;
	public static Block blockPlastic;
	
	public static final int GUI_INJECTION_TUBE = 0;
	public static final int GUI_FILTER_TUBE = 1;
	public static final int GUI_COMPRESSOR_TUBE = 2;
	public static final int GUI_REQUESTING_TUBE = 3;
	public static final int GUI_ROUTING_TUBE = 4;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		itemTubeId = config.getItem("Tube", 5000).getInt();
		itemDustPlasticId = config.getItem("PlasticDust", 5001).getInt();
		itemSheetPlasticId = config.getItem("PlasticSheet", 5002).getInt();
		itemMilkCurdBucketId = config.getItem("MilkCurd", 5003).getInt();
		itemBucketPlasticId = config.getItem("BucketOfPlastic", 5004).getInt();
		
		blockPlasticId = config.getBlock("PlasticBlock", 1027).getInt();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SuppressWarnings( "unchecked" )
	@Init
	public void init(FMLInitializationEvent event)
	{
		packetManager.initialize("tubes");
		PacketManager.registerHandler(proxy);
		PacketManager.registerPacket(ModPacketSetFilterMode.class);
		PacketManager.registerPacket(ModPacketSetPullMode.class);
		PacketManager.registerPacket(ModPacketSetRoutingOptions.class);
		PacketManager.registerPacket(ModPacketSetColor.class);
		RenderHelper.initialize();
		
		proxy.initialize();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.registerOreRecipes();
		TubeRegistry.instance().finalizeTubes();
	}

	@ForgeSubscribe
	public void registerIcons(TextureStitchEvent.Pre event)
	{
		if(event.map.textureType == 0)
			TubeRegistry.instance().registerIcons(event.map);
	}
}
