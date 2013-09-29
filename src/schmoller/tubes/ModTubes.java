package schmoller.tubes;

import java.util.logging.Logger;

import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultipartRenderer;
import codechicken.multipart.TMultiPart;

import schmoller.tubes.network.PacketManager;
import schmoller.tubes.network.packets.ModPacketAddItem;
import schmoller.tubes.parts.BaseTubePart;
import schmoller.tubes.parts.ItemTubeBase;
import schmoller.tubes.parts.RestrictionTubePart;

import net.minecraft.item.ItemStack;
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
import cpw.mods.fml.common.Mod.Instance;

@Mod(name="Tubes", version="1.0.0", modid = "Tubes", dependencies="required-after:Forge")
@NetworkMod(clientSideRequired=true, serverSideRequired=true)
public class ModTubes implements IPartFactory
{
	@Instance("Tubes")
    public static ModTubes instance;
	
	@SidedProxy(clientSide="schmoller.tubes.ClientProxy", serverSide="schmoller.tubes.CommonProxy")
	public static CommonProxy proxy;
	
	@SidedProxy(clientSide = "schmoller.tubes.network.ClientPacketManager", serverSide = "schmoller.tubes.network.PacketManager")
	public static PacketManager packetManager;
	
	public static Logger logger = Logger.getLogger("Tubes");
	
	public int itemTubeId;
	
	public static ItemStack itemTube;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		// TODO: Load configurations etc.
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		itemTubeId = config.getItem("Tube", 5000).getInt();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SuppressWarnings( "unchecked" )
	@Init
	public void init(FMLInitializationEvent event)
	{
		packetManager.initialize("tubes");
		PacketManager.registerPacket(ModPacketAddItem.class);
		
		proxy.initialize();
		
		packetManager.registerHandler(proxy);
		
		ItemTubeBase tubeItem = new ItemTubeBase(itemTubeId);
		GameRegistry.registerItem(tubeItem, "tubes:items:tube");
		
		MultiPartRegistry.registerParts(this, new String[] {"schmoller_tube", "schmoller_restriction_tube"});
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		// TODO: Mod compatability
		
	}

	@Override
	public TMultiPart createPart( String id, boolean client )
	{
		if(id.equals("schmoller_tube"))
			return new BaseTubePart();
		else if(id.equals("schmoller_restriction_tube"))
			return new RestrictionTubePart();

		return null;
	}
	
	@ForgeSubscribe
	public void registerIcons(TextureStitchEvent.Pre event)
	{
		if(event.map.textureType == 0)
		{
			BaseTubePart.center = event.map.registerIcon("Tubes:tube-center");
			BaseTubePart.straight = event.map.registerIcon("Tubes:tube");
			RestrictionTubePart.center = event.map.registerIcon("Tubes:tube-restriction-center");
			RestrictionTubePart.straight = event.map.registerIcon("Tubes:tube-restriction");
		}
	}
}
