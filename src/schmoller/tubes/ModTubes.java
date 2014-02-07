package schmoller.tubes;

import java.util.EnumSet;
import java.util.logging.Logger;

import schmoller.tubes.api.Blocks;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.Items;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.helpers.BaseRouter;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.items.ItemTubeBase;
import schmoller.tubes.network.PacketManager;
import schmoller.tubes.network.packets.ModPacketNEIDragDrop;
import schmoller.tubes.network.packets.ModPacketSetColor;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import schmoller.tubes.network.packets.ModPacketSetRequestingModes;
import schmoller.tubes.network.packets.ModPacketSetRoutingOptions;
import schmoller.tubes.parts.TubeCap;
import schmoller.tubes.routing.BlockedRouter;
import schmoller.tubes.routing.ImportSourceFinder;
import schmoller.tubes.routing.InputRouter;
import schmoller.tubes.routing.OutputRouter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.TickType;

import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(name="Tubes", version="@{mod.version}", modid = "Tubes", dependencies="required-after:Forge; required-before:ForgeMultipart")
@NetworkMod(clientSideRequired=true, serverSideRequired=true)
public class ModTubes extends TubesAPI implements ITickHandler
{
	@Instance("Tubes")
    public static ModTubes instance;
	
	@SidedProxy(clientSide="schmoller.tubes.ClientProxy", serverSide="schmoller.tubes.CommonProxy")
	public static CommonProxy proxy;
	
	@SidedProxy(clientSide = "schmoller.tubes.network.ClientPacketManager", serverSide = "schmoller.tubes.network.PacketManager")
	public static PacketManager packetManager;
	
	public static Logger logger = Logger.getLogger("Tubes");
	
	public int plasticYield;
	
	public static ItemTubeBase itemTube;
	
	public static Fluid fluidPlastic;
	
	public static final int GUI_INJECTION_TUBE = 0;
	public static final int GUI_FILTER_TUBE = 1;
	public static final int GUI_COMPRESSOR_TUBE = 2;
	public static final int GUI_REQUESTING_TUBE = 3;
	public static final int GUI_ROUTING_TUBE = 4;
	public static final int GUI_BUFFER_TUBE = 5;
	
	public static TubeCreativeTab creativeTab;
	
	private int mClientTickCounter;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		TubesAPI.instance = this;
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		
		for(Items item : Items.values())
			item.initialize(config.getItem(item.getConfigName(), item.getItemID()).getInt(), null);
		
		for(Blocks block : Blocks.values())
			block.initialize(config.getBlock(block.getConfigName(), block.getBlockID()).getInt(), null);
		
		Property prop =  config.get("general", "plasticYield", 2);
		prop.comment = "How much the base plastic recipe gives. Next level recipe is 4 times this. Default 2.";
		plasticYield = prop.getInt();
		
		config.save();
		
		MinecraftForge.EVENT_BUS.register(this);
		FilterRegistry.registerFilterFactory(new BasicFilterFactory());
		
		creativeTab = new TubeCreativeTab();
	}
	
	@SuppressWarnings( "unchecked" )
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		packetManager.initialize("tubes");
		PacketManager.registerHandler(proxy);
		PacketManager.registerPacket(ModPacketSetFilterMode.class);
		PacketManager.registerPacket(ModPacketSetRequestingModes.class);
		PacketManager.registerPacket(ModPacketSetRoutingOptions.class);
		PacketManager.registerPacket(ModPacketSetColor.class);
		PacketManager.registerPacket(ModPacketNEIDragDrop.class);

		proxy.initialize();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		// Compile all dyes into one name for recipes
		String[] dyes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };
		
		for(String name : dyes)
		{
			for(ItemStack item : OreDictionary.getOres(name))
				OreDictionary.registerOre("Tubes$anyDye", item);
		}
		
		proxy.registerOreRecipes();
		TubeRegistry.instance().finalizeTubes();
		
		event.buildSoftDependProxy("BuildCraft|Core", BuildcraftProxy.class.getName());
		
		TickRegistry.registerTickHandler(this, Side.CLIENT);
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
			
			TubeCap.icon = event.map.registerIcon("tubes:tube-cap");
		}
	}

	@Override
	public void registerShapedRecipe( ItemStack output, Object... input )
	{
		GameRegistry.addRecipe(new SpecialShapedRecipe(output, input));
	}

	@Override
	public void registerShapelessRecipe( ItemStack output, Object... input )
	{
		GameRegistry.addRecipe(new SpecialShapelessRecipe(output, input));
	}

	@Override
	public ItemStack createTubeForType( String type )
	{
		return createTubeForType(type, 1);
	}
	
	@Override
	public ItemStack createTubeForType( String type, int amount )
	{
		return itemTube.createForType(type, amount);
	}

	@Override
	public String getTubeType( ItemStack item )
	{
		return itemTube.getTubeType(item);
	}

	
	@Override
	public BaseRouter getOutputRouter( IBlockAccess world, Position position, TubeItem item )
	{
		return new OutputRouter(world, position, item);
	}

	@Override
	public BaseRouter getOutputRouter( IBlockAccess world, Position position, TubeItem item, int direction )
	{
		return new OutputRouter(world, position, item, direction);
	}

	@Override
	public BaseRouter getImportRouter( IBlockAccess world, Position position, TubeItem item )
	{
		return new InputRouter(world, position, item);
	}

	@Override
	public BaseRouter getImportSourceRouter( IBlockAccess world, Position position, int startDirection, IFilter filter, SizeMode mode)
	{
		return new ImportSourceFinder(world, position, startDirection, filter, mode);
	}

	@Override
	public BaseRouter getOverflowRouter( IBlockAccess world, Position position, TubeItem item )
	{
		return new BlockedRouter(world, position, item);
	}
	
	@Override
	public CreativeTabs getCreativeTab()
	{
		return creativeTab;
	}
	
	public int getCurrentTick()
	{
		if(FMLCommonHandler.instance().getSide() == Side.SERVER)
			return FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
		else
			return mClientTickCounter;
	}

	@Override
	public void tickStart( EnumSet<TickType> type, Object... tickData )
	{
		++mClientTickCounter;
	}

	@Override
	public void tickEnd( EnumSet<TickType> type, Object... tickData )
	{
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel()
	{
		return "ClientTickCounter";
	}
}
