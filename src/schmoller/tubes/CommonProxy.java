
package schmoller.tubes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.MultipartGenerator;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import schmoller.tubes.definitions.CompressorTube;
import schmoller.tubes.definitions.EjectionTube;
import schmoller.tubes.definitions.ExtractionTube;
import schmoller.tubes.definitions.FilterTube;
import schmoller.tubes.definitions.InjectionTube;
import schmoller.tubes.definitions.NormalTube;
import schmoller.tubes.definitions.RequestingTube;
import schmoller.tubes.definitions.RestrictionTube;
import schmoller.tubes.definitions.RoutingTube;
import schmoller.tubes.gui.CompressorContainer;
import schmoller.tubes.gui.FilterTubeContainer;
import schmoller.tubes.gui.InjectionTubeContainer;
import schmoller.tubes.gui.RequestingTubeContainer;
import schmoller.tubes.gui.RoutingTubeContainer;
import schmoller.tubes.items.BasicItem;
import schmoller.tubes.logic.CompressorTubeLogic;
import schmoller.tubes.logic.FilterTubeLogic;
import schmoller.tubes.logic.PullMode;
import schmoller.tubes.logic.RequestingTubeLogic;
import schmoller.tubes.logic.RoutingTubeLogic;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModBlockPacket;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import schmoller.tubes.network.packets.ModPacketSetPullMode;
import schmoller.tubes.network.packets.ModPacketSetRoutingOptions;
import schmoller.tubes.parts.InventoryTubePart;
import schmoller.tubes.parts.ItemTubeBase;

public class CommonProxy implements IModPacketHandler, IGuiHandler
{
	public void initialize()
	{
		registerTubes();
		registerItems();
		registerText();
		registerRecipes();
		
		OreDictionary.registerOre("dustPlastic", ModTubes.itemDustPlastic);
		OreDictionary.registerOre("itemPlastic", ModTubes.itemSheetPlastic);
		OreDictionary.registerOre("blockPlastic", ModTubes.blockPlastic);
		
		MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(ModTubes.blockPlastic, 0), "tile.tubes.blockPlastic");
		FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%d@%d", ModTubes.blockPlastic.blockID, 0 ));
		
		NetworkRegistry.instance().registerGuiHandler(ModTubes.instance, this);
	}
	
	private void registerTubes()
	{
		MultipartGenerator.registerPassThroughInterface(ISidedInventory.class.getName(), true, true);
		
		TubeRegistry.registerTube(new NormalTube(), "basic");
		TubeRegistry.registerTube(new RestrictionTube(), "restriction");
		TubeRegistry.registerTube(new InjectionTube(), "injection");
		TubeRegistry.registerTube(new EjectionTube(), "ejection");
		TubeRegistry.registerTube(new FilterTube(), "filter");
		TubeRegistry.registerTube(new CompressorTube(), "compressor");
		TubeRegistry.registerTube(new ExtractionTube(), "extraction");
		TubeRegistry.registerTube(new RequestingTube(), "requesting");
		TubeRegistry.registerTube(new RoutingTube(), "routing");
	}
	
	private void registerItems()
	{

		ModTubes.itemDustPlastic = new BasicItem(ModTubes.instance.itemDustPlasticId).setUnlocalizedName("dustPlastic");
		ModTubes.itemSheetPlastic = new BasicItem(ModTubes.instance.itemSheetPlasticId).setUnlocalizedName("sheetPlastic");
		ModTubes.itemMilkCurdBucket = new BasicItem(ModTubes.instance.itemMilkCurdBucketId).setUnlocalizedName("milkCurd").setContainerItem(Item.bucketEmpty).setCreativeTab(CreativeTabs.tabMisc).setMaxStackSize(1);
		ModTubes.itemBucketPlastic = new BasicItem(ModTubes.instance.itemBucketPlasticId).setUnlocalizedName("bucketOfPlastic").setContainerItem(Item.bucketEmpty).setCreativeTab(CreativeTabs.tabMisc).setMaxStackSize(1);
		
		ModTubes.itemTube = new ItemTubeBase(ModTubes.instance.itemTubeId);
		
		GameRegistry.registerItem(ModTubes.itemDustPlastic, "dustPlastic");
		GameRegistry.registerItem(ModTubes.itemSheetPlastic, "sheetPlastic");
		GameRegistry.registerItem(ModTubes.itemMilkCurdBucket, "milkCurd");
		GameRegistry.registerItem(ModTubes.itemBucketPlastic, "bucketOfPlastic");
		GameRegistry.registerItem(ModTubes.itemTube, "tubes:items:tube");
		
		ModTubes.blockPlastic = new Block(ModTubes.instance.blockPlasticId, Material.piston).setCreativeTab(CreativeTabs.tabBlock).setUnlocalizedName("Tubes:blockPlastic");
		GameRegistry.registerBlock(ModTubes.blockPlastic, "blockPlastic");
	}
	
	private void registerText()
	{
		LanguageRegistry.instance().addStringLocalization("tubes.basic.name", "Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.restriction.name", "Restriction Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.injection.name", "Injection Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.ejection.name", "Ejection Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.filter.name", "Filter Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.extraction.name", "Extraction Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.compressor.name", "Compressor Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.routing.name", "Routing Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.requesting.name", "Requesting Tube");
		
		LanguageRegistry.addName(ModTubes.itemDustPlastic, "Plastic Pellets");
		LanguageRegistry.addName(ModTubes.itemSheetPlastic, "Plastic");
		LanguageRegistry.addName(ModTubes.itemMilkCurdBucket, "Milk Curd");
		LanguageRegistry.addName(ModTubes.itemBucketPlastic, "Plastic");
		LanguageRegistry.addName(ModTubes.blockPlastic, "Block Of Plastic");
	}
	
	private void registerRecipes()
	{
		GameRegistry.addSmelting(ModTubes.itemDustPlastic.itemID, new ItemStack(ModTubes.itemSheetPlastic), 0);
		GameRegistry.addSmelting(Item.bucketMilk.itemID, new ItemStack(ModTubes.itemMilkCurdBucket), 0);
		GameRegistry.addShapelessRecipe(new ItemStack(ModTubes.itemDustPlastic, 2), ModTubes.itemMilkCurdBucket, new ItemStack(Item.coal, 1, OreDictionary.WILDCARD_VALUE), Item.gunpowder, new ItemStack(Item.potion, 1, 0));
		
		
		
		GameRegistry.addShapelessRecipe(ModTubes.itemTube.createForType("restriction"), ModTubes.itemTube.createForType("basic"), Item.ingotIron);
		GameRegistry.addShapedRecipe(ModTubes.itemTube.createForType("compressor"), "ipi", "ptp", "ipi", 'i', Item.ingotIron, 'p', Block.pistonBase, 't', ModTubes.itemTube.createForType("basic"));
		GameRegistry.addShapelessRecipe(ModTubes.itemTube.createForType("injection"), ModTubes.itemTube.createForType("basic"), Block.chest);
		GameRegistry.addShapedRecipe(ModTubes.itemTube.createForType("extraction"), " h ", " t ", " p ", 't', ModTubes.itemTube.createForType("basic"), 'h', Block.hopperBlock, 'p', Block.pistonStickyBase);
		GameRegistry.addShapedRecipe(ModTubes.itemTube.createForType("requesting"), " t ", " e ", " f ", 't', ModTubes.itemTube.createForType("basic"), 'e', ModTubes.itemTube.createForType("extraction"), 'f', ModTubes.itemTube.createForType("filter"));
		GameRegistry.addShapedRecipe(ModTubes.itemTube.createForType("filter"), "iei", "btb", "iei", 'i', Item.ingotIron, 'b', Block.fenceIron, 't', ModTubes.itemTube.createForType("basic"), 'e', Item.eyeOfEnder);
		GameRegistry.addShapelessRecipe(ModTubes.itemTube.createForType("ejection"), ModTubes.itemTube.createForType("basic"), Block.glass);
	}

	public void registerOreRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(ModTubes.itemTube.createForType("basic", 8), "pgp", 'p', "itemPlastic", 'g', Block.glass));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTubes.blockPlastic), "pp","pp", 'p', "itemPlastic"));
	}
	
	
	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
		if(packet instanceof ModBlockPacket)
		{
			ITube tube = CommonHelper.getMultiPart(((EntityPlayer)sender).worldObj, ((ModBlockPacket)packet).xCoord, ((ModBlockPacket)packet).yCoord, ((ModBlockPacket)packet).zCoord, ITube.class);
			
			if(packet instanceof ModPacketSetFilterMode && tube != null && tube.getLogic() instanceof FilterTubeLogic)
			{
				ModPacketSetFilterMode mode = (ModPacketSetFilterMode)packet;
				
				FilterTubeLogic logic = (FilterTubeLogic)tube.getLogic();
				
				if(mode.mode != null)
					logic.setMode(mode.mode);
				else
					logic.setComparison(mode.comparison);
				
				//tube.updateState();
				
				return true;
			}
			else if(packet instanceof ModPacketSetPullMode && tube != null && tube.getLogic() instanceof RequestingTubeLogic)
			{
				PullMode mode = ((ModPacketSetPullMode)packet).mode;
				
				((RequestingTubeLogic)tube.getLogic()).setMode(mode);
				
				return true;
			}
			else if(packet instanceof ModPacketSetRoutingOptions && tube != null && tube.getLogic() instanceof RoutingTubeLogic)
			{
				ModPacketSetRoutingOptions options = (ModPacketSetRoutingOptions)packet;
				RoutingTubeLogic logic = (RoutingTubeLogic)tube.getLogic();
				
				if(options.hasColour)
					logic.setColour(options.column, options.colour);
				else
					logic.setDirection(options.column, options.direction);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Object getClientGuiElement( int ID, EntityPlayer player, World world, int x, int y, int z ) { return null; }
	
	@Override
	public Object getServerGuiElement( int ID, EntityPlayer player, World world, int x, int y, int z )
	{
		switch(ID)
		{
		case ModTubes.GUI_INJECTION_TUBE:
			return new InjectionTubeContainer(CommonHelper.getMultiPart(world, x, y, z, InventoryTubePart.class), player);
		case ModTubes.GUI_FILTER_TUBE:
			return new FilterTubeContainer((FilterTubeLogic)CommonHelper.getMultiPart(world, x, y, z, ITube.class).getLogic(), player);
		case ModTubes.GUI_COMPRESSOR_TUBE:
			return new CompressorContainer((CompressorTubeLogic)CommonHelper.getMultiPart(world, x, y, z, ITube.class).getLogic(), player);
		case ModTubes.GUI_REQUESTING_TUBE:
			return new RequestingTubeContainer((RequestingTubeLogic)CommonHelper.getMultiPart(world, x, y, z, ITube.class).getLogic(), player);
		case ModTubes.GUI_ROUTING_TUBE:
			return new RoutingTubeContainer((RoutingTubeLogic)CommonHelper.getMultiPart(world, x, y, z, ITube.class).getLogic(), player);
		}
		
		return null;
	}
}
