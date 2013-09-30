package schmoller.tubes;

import java.util.logging.Logger;

import codechicken.multipart.MultipartGenerator;

import schmoller.tubes.definitions.InjectionTube;
import schmoller.tubes.definitions.NormalTube;
import schmoller.tubes.definitions.RestrictionTube;
import schmoller.tubes.network.PacketManager;
import schmoller.tubes.network.packets.ModPacketAddItem;
import schmoller.tubes.parts.ItemTubeBase;
import schmoller.tubes.render.RenderHelper;
import net.minecraft.inventory.ISidedInventory;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
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
	
	public static ItemTubeBase itemTube;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		itemTubeId = config.getItem("Tube", 5000).getInt();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SuppressWarnings( "unchecked" )
	@Init
	public void init(FMLInitializationEvent event)
	{
		packetManager.initialize("tubes");
		PacketManager.registerHandler(proxy);
		PacketManager.registerPacket(ModPacketAddItem.class);
		
		RenderHelper.initialize();
		
		proxy.initialize();
		
		itemTube = new ItemTubeBase(itemTubeId);
		GameRegistry.registerItem(itemTube, "tubes:items:tube");
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		TubeRegistry.instance().finalizeTubes();
	}

	@ForgeSubscribe
	public void registerIcons(TextureStitchEvent.Pre event)
	{
		if(event.map.textureType == 0)
			TubeRegistry.instance().registerIcons(event.map);
	}
}
