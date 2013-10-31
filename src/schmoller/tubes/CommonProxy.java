
package schmoller.tubes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
//import codechicken.multipart.MultipartGenerator;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import schmoller.tubes.definitions.TypeCompressorTube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.definitions.TypeExtractionTube;
import schmoller.tubes.definitions.TypeFilterTube;
import schmoller.tubes.definitions.TypeInjectionTube;
import schmoller.tubes.definitions.TypeNormalTube;
import schmoller.tubes.definitions.TypeRequestingTube;
import schmoller.tubes.definitions.TypeRestrictionTube;
import schmoller.tubes.definitions.TypeRoutingTube;
import schmoller.tubes.gui.CompressorContainer;
import schmoller.tubes.gui.ExtContainer;
import schmoller.tubes.gui.FakeSlot;
import schmoller.tubes.gui.FilterTubeContainer;
import schmoller.tubes.gui.InjectionTubeContainer;
import schmoller.tubes.gui.RequestingTubeContainer;
import schmoller.tubes.gui.RoutingTubeContainer;
import schmoller.tubes.items.BasicItem;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModBlockPacket;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.network.packets.ModPacketNEIDragDrop;
import schmoller.tubes.network.packets.ModPacketSetColor;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import schmoller.tubes.network.packets.ModPacketSetPullMode;
import schmoller.tubes.network.packets.ModPacketSetRoutingOptions;
import schmoller.tubes.parts.ItemTubeBase;
import schmoller.tubes.types.CompressorTube;
import schmoller.tubes.types.FilterTube;
import schmoller.tubes.types.InjectionTube;
import schmoller.tubes.types.RequestingTube;
import schmoller.tubes.types.RoutingTube;

public class CommonProxy implements IModPacketHandler, IGuiHandler
{
	public void initialize()
	{
		registerTubes();
		registerItems();
		registerText();
		registerRecipes();
		
		OreDictionary.registerOre("dustPlastic", ModTubes.itemDustPlastic);
		OreDictionary.registerOre("sheetPlastic", ModTubes.itemSheetPlastic);
		OreDictionary.registerOre("blockPlastic", ModTubes.blockPlastic);
		
		MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(ModTubes.blockPlastic, 0), "tile.tubes.blockPlastic");
		FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%d@%d", ModTubes.blockPlastic.blockID, 0 ));
		
		NetworkRegistry.instance().registerGuiHandler(ModTubes.instance, this);
	}
	
	private void registerTubes()
	{
		// FIXME: This does not seem to work after obfuscation
		//MultipartGenerator.registerPassThroughInterface(ISidedInventory.class.getName());
		
		TubeRegistry.registerTube(new TypeNormalTube(), "basic");
		TubeRegistry.registerTube(new TypeRestrictionTube(), "restriction");
		TubeRegistry.registerTube(new TypeInjectionTube(), "injection");
		TubeRegistry.registerTube(new TypeEjectionTube(), "ejection");
		TubeRegistry.registerTube(new TypeFilterTube(), "filter");
		TubeRegistry.registerTube(new TypeCompressorTube(), "compressor");
		TubeRegistry.registerTube(new TypeExtractionTube(), "extraction");
		TubeRegistry.registerTube(new TypeRequestingTube(), "requesting");
		TubeRegistry.registerTube(new TypeRoutingTube(), "routing");
	}
	
	private void registerItems()
	{

		ModTubes.itemDustPlastic = new BasicItem(ModTubes.instance.itemDustPlasticId).setUnlocalizedName("dustPlastic");
		ModTubes.itemSheetPlastic = new BasicItem(ModTubes.instance.itemSheetPlasticId).setUnlocalizedName("sheetPlastic");
		ModTubes.itemMilkCurdBucket = new BasicItem(ModTubes.instance.itemMilkCurdBucketId).setUnlocalizedName("milkCurd").setContainerItem(Item.bucketEmpty).setCreativeTab(CreativeTabs.tabMisc).setMaxStackSize(1);
		ModTubes.itemBucketPlastic = new BasicItem(ModTubes.instance.itemBucketPlasticId).setUnlocalizedName("bucketOfPlastic").setContainerItem(Item.bucketEmpty).setCreativeTab(CreativeTabs.tabMisc).setMaxStackSize(1);
		ModTubes.itemRedstoneCircuit = new BasicItem(ModTubes.instance.itemRedstoneCircuitId).setUnlocalizedName("redstoneCircuit").setCreativeTab(CreativeTabs.tabMisc);
		
		ModTubes.itemTube = new ItemTubeBase(ModTubes.instance.itemTubeId);
		
		GameRegistry.registerItem(ModTubes.itemDustPlastic, "dustPlastic");
		GameRegistry.registerItem(ModTubes.itemSheetPlastic, "sheetPlastic");
		GameRegistry.registerItem(ModTubes.itemMilkCurdBucket, "milkCurd");
		GameRegistry.registerItem(ModTubes.itemBucketPlastic, "bucketOfPlastic");
		GameRegistry.registerItem(ModTubes.itemTube, "tubes:items:tube");
		GameRegistry.registerItem(ModTubes.itemRedstoneCircuit, "redstoneCircuit");
		
		ModTubes.blockPlastic = new Block(ModTubes.instance.blockPlasticId, Material.piston)
			.setCreativeTab(CreativeTabs.tabBlock)
			.setUnlocalizedName("Tubes:blockPlastic")
			.setTextureName("tubes:blockPlastic")
			.setHardness(2.5f);
		
		MinecraftForge.setBlockHarvestLevel(ModTubes.blockPlastic, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(ModTubes.blockPlastic, "axe", 0);
		
		GameRegistry.registerBlock(ModTubes.blockPlastic, "blockPlastic");
		
		ModTubes.fluidPlastic = new Fluid("plastic").setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(ModTubes.fluidPlastic);
		ModTubes.fluidPlastic = FluidRegistry.getFluid("plastic");
		
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("plastic", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(ModTubes.itemBucketPlastic), new ItemStack(Item.bucketEmpty));
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
		
		LanguageRegistry.instance().addStringLocalization("fluid.plastic", "Plastic");
		
		LanguageRegistry.addName(ModTubes.itemDustPlastic, "Plastic Pellets");
		LanguageRegistry.addName(ModTubes.itemSheetPlastic, "Plastic");
		LanguageRegistry.addName(ModTubes.itemMilkCurdBucket, "Milk Curd");
		LanguageRegistry.addName(ModTubes.itemBucketPlastic, "Plastic");
		LanguageRegistry.addName(ModTubes.blockPlastic, "Block Of Plastic");
		LanguageRegistry.addName(ModTubes.itemRedstoneCircuit, "Redstone Circuit");
	}
	
	private void registerRecipes()
	{
		GameRegistry.addSmelting(ModTubes.itemDustPlastic.itemID, new ItemStack(ModTubes.itemSheetPlastic), 0);
		GameRegistry.addSmelting(Item.bucketMilk.itemID, new ItemStack(ModTubes.itemMilkCurdBucket), 0);
		GameRegistry.addRecipe(new SpecialShapelessRecipe(new ItemStack(ModTubes.itemDustPlastic, ModTubes.instance.plasticYield * 4), ModTubes.itemMilkCurdBucket, new ItemStack(Item.coal, 1, OreDictionary.WILDCARD_VALUE), Item.gunpowder, FluidRegistry.getFluid("water")));
		GameRegistry.addRecipe(new SpecialShapelessRecipe(new ItemStack(ModTubes.itemDustPlastic, ModTubes.instance.plasticYield * 4), ModTubes.itemMilkCurdBucket, new ItemStack(Item.coal, 1, OreDictionary.WILDCARD_VALUE), Item.gunpowder, FluidRegistry.getFluid("water")));
		GameRegistry.addRecipe(new SpecialShapedRecipe(new ItemStack(ModTubes.itemDustPlastic, ModTubes.instance.plasticYield), " c ", "sCs", " c ", 'c', new ItemStack(Item.coal, 1, OreDictionary.WILDCARD_VALUE), 'C', Item.clay, 's', Block.sand));
		
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("basic", 8), "pgp", 'p', "sheetPlastic", 'g', Block.glass));
		GameRegistry.addRecipe(new SpecialShapedRecipe(new ItemStack(ModTubes.blockPlastic), "pp","pp", 'p', "sheetPlastic"));
		
		GameRegistry.addRecipe(new SpecialShapelessRecipe(ModTubes.itemTube.createForType("restriction"), ModTubes.itemTube.createForType("basic"), Item.ingotIron));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("compressor"), "ipi", "ptp", "ipi", 'i', Item.ingotIron, 'p', Block.pistonBase, 't', ModTubes.itemTube.createForType("basic")));
		// TODO: Put this back in when the passthrough interface works again
		//GameRegistry.addRecipe(new SpecialShapelessRecipe(ModTubes.itemTube.createForType("injection"), ModTubes.itemTube.createForType("basic"), Block.chest));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("extraction"), "h", "t", "p", 't', ModTubes.itemTube.createForType("basic"), 'h', Block.hopperBlock, 'p', Block.pistonStickyBase));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("requesting"), "t", "e", "f", 't', ModTubes.itemTube.createForType("basic"), 'e', ModTubes.itemTube.createForType("extraction"), 'f', ModTubes.itemTube.createForType("filter")));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("filter"), "ici", "ctc", "ici", 'i', Item.ingotIron, 't', ModTubes.itemTube.createForType("basic"), 'c', ModTubes.itemRedstoneCircuit));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("routing"), "iti", "tft", "iti", 'i', Item.ingotIron, 't', ModTubes.itemTube.createForType("basic"), 'f', ModTubes.itemTube.createForType("filter")));
		GameRegistry.addRecipe(new SpecialShapelessRecipe(ModTubes.itemTube.createForType("ejection"), ModTubes.itemTube.createForType("basic"), Block.glass));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModTubes.itemRedstoneCircuit, 4), "igi", "rrr", "igi", 'i', Item.ingotIron, 'g', Item.ingotGold, 'r', Item.redstone);
	}

	public void registerOreRecipes()
	{
		
	}
	
	
	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
		if(packet instanceof ModBlockPacket)
		{
			ITube tube = CommonHelper.getMultiPart(((EntityPlayer)sender).worldObj, ((ModBlockPacket)packet).xCoord, ((ModBlockPacket)packet).yCoord, ((ModBlockPacket)packet).zCoord, ITube.class);
			
			if(packet instanceof ModPacketSetFilterMode && tube instanceof FilterTube)
			{
				ModPacketSetFilterMode mode = (ModPacketSetFilterMode)packet;
				
				FilterTube logic = (FilterTube)tube;
				
				if(mode.mode != null)
					logic.setMode(mode.mode);
				else
					logic.setComparison(mode.comparison);
				
				//tube.updateState();
				
				return true;
			}
			else if(packet instanceof ModPacketSetPullMode && tube instanceof RequestingTube)
			{
				PullMode mode = ((ModPacketSetPullMode)packet).mode;
				
				((RequestingTube)tube).setMode(mode);
				
				return true;
			}
			else if(packet instanceof ModPacketSetRoutingOptions && tube instanceof RoutingTube)
			{
				ModPacketSetRoutingOptions options = (ModPacketSetRoutingOptions)packet;
				RoutingTube logic = (RoutingTube)tube;
				
				if(options.hasColour)
					logic.setColour(options.column, options.colour);
				else
					logic.setDirection(options.column, options.direction);
				return true;
			}
			else if(packet instanceof ModPacketSetColor)
			{
				int color = ((ModPacketSetColor)packet).color;
				
				if(tube instanceof RequestingTube)
					((RequestingTube)tube).setColour((short)color);
				else if(tube instanceof FilterTube)
					((FilterTube)tube).setColour((short)color);
			}
		}
		else if(packet instanceof ModPacketNEIDragDrop)
		{
			EntityPlayer player = (EntityPlayer)sender;
			ItemStack item = ((ModPacketNEIDragDrop)packet).item;
			int slotId = ((ModPacketNEIDragDrop)packet).slot;
			
			if(player.openContainer instanceof ExtContainer && slotId >= 0 && slotId < player.openContainer.inventorySlots.size())
			{
				Slot slot = (Slot)player.openContainer.inventorySlots.get(slotId);
				if(slot instanceof FakeSlot)
					slot.putStack(item);
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
			return new InjectionTubeContainer(CommonHelper.getMultiPart(world, x, y, z, InjectionTube.class), player);
		case ModTubes.GUI_FILTER_TUBE:
			return new FilterTubeContainer(CommonHelper.getMultiPart(world, x, y, z, FilterTube.class), player);
		case ModTubes.GUI_COMPRESSOR_TUBE:
			return new CompressorContainer(CommonHelper.getMultiPart(world, x, y, z, CompressorTube.class), player);
		case ModTubes.GUI_REQUESTING_TUBE:
			return new RequestingTubeContainer(CommonHelper.getMultiPart(world, x, y, z, RequestingTube.class), player);
		case ModTubes.GUI_ROUTING_TUBE:
			return new RoutingTubeContainer(CommonHelper.getMultiPart(world, x, y, z, RoutingTube.class), player);
		}
		
		return null;
	}
}
