package schmoller.tubes;

import java.util.logging.Logger;

import schmoller.tubes.network.PacketManager;
import schmoller.tubes.network.packets.ModPacketNEIDragDrop;
import schmoller.tubes.network.packets.ModPacketSetColor;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import schmoller.tubes.network.packets.ModPacketSetPullMode;
import schmoller.tubes.network.packets.ModPacketSetRoutingOptions;
import schmoller.tubes.parts.ItemTubeBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;

import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(name="Tubes", version="@{mod.version}", modid = "Tubes", dependencies="required-after:Forge; required-before:ForgeMultipart")
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
	public int itemRedstoneCircuitId;
	
	public int blockPlasticId;
	
	public int plasticYield;
	
	public static ItemTubeBase itemTube;
	
	public static Item itemDustPlastic;
	public static Item itemSheetPlastic;
	public static Item itemMilkCurdBucket;
	public static Item itemBucketPlastic;
	public static Item itemRedstoneCircuit;
	public static Block blockPlastic;
	
	public static Fluid fluidPlastic;
	
	public static final int GUI_INJECTION_TUBE = 0;
	public static final int GUI_FILTER_TUBE = 1;
	public static final int GUI_COMPRESSOR_TUBE = 2;
	public static final int GUI_REQUESTING_TUBE = 3;
	public static final int GUI_ROUTING_TUBE = 4;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		itemTubeId = config.getItem("Tube", 5000).getInt();
		itemDustPlasticId = config.getItem("PlasticDust", 5001).getInt();
		itemSheetPlasticId = config.getItem("PlasticSheet", 5002).getInt();
		itemMilkCurdBucketId = config.getItem("MilkCurd", 5003).getInt();
		itemBucketPlasticId = config.getItem("BucketOfPlastic", 5004).getInt();
		itemRedstoneCircuitId = config.getItem("redstoneCircuit", 5005).getInt();
		
		blockPlasticId = config.getBlock("PlasticBlock", 1027).getInt();
		
		Property prop =  config.get("general", "plasticYield", 2);
		prop.comment = "How much the base plastic recipe gives. Next level recipe is 4 times this. Default 2.";
		plasticYield = prop.getInt();
		
		config.save();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SuppressWarnings( "unchecked" )
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		packetManager.initialize("tubes");
		PacketManager.registerHandler(proxy);
		PacketManager.registerPacket(ModPacketSetFilterMode.class);
		PacketManager.registerPacket(ModPacketSetPullMode.class);
		PacketManager.registerPacket(ModPacketSetRoutingOptions.class);
		PacketManager.registerPacket(ModPacketSetColor.class);
		PacketManager.registerPacket(ModPacketNEIDragDrop.class);

		proxy.initialize();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.registerOreRecipes();
		TubeRegistry.instance().finalizeTubes();
		
		event.buildSoftDependProxy("BuildCraft|Core", BuildcraftProxy.class.getName());
	}

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void registerIcons(TextureStitchEvent.Pre event)
	{
		if(event.map.textureType == 0)
		{
			TubeRegistry.instance().registerIcons(event.map);
			if(fluidPlastic != null)
				fluidPlastic.setIcons(event.map.registerIcon("tubes:fluidPlastic"));
		}
	}
}
