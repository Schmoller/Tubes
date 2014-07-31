
package schmoller.tubes;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.microblock.handler.MicroblockProxy;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import schmoller.tubes.api.Blocks;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Items;
import schmoller.tubes.api.PayloadRegistry;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.definitions.TypeAdvancedExtractionTube;
import schmoller.tubes.definitions.TypeBufferTube;
import schmoller.tubes.definitions.TypeColoringTube;
import schmoller.tubes.definitions.TypeCompressorTube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.definitions.TypeExtractionTube;
import schmoller.tubes.definitions.TypeFilterTube;
import schmoller.tubes.definitions.TypeFluidExtractionTube;
import schmoller.tubes.definitions.TypeInjectionTube;
import schmoller.tubes.definitions.TypeManagementTube;
import schmoller.tubes.definitions.TypeNormalTube;
import schmoller.tubes.definitions.TypeRequestingTube;
import schmoller.tubes.definitions.TypeRestrictionTube;
import schmoller.tubes.definitions.TypeRoundRobinTube;
import schmoller.tubes.definitions.TypeRoutingTube;
import schmoller.tubes.definitions.TypeTankTube;
import schmoller.tubes.definitions.TypeValveTube;
import schmoller.tubes.gui.AdvancedExtractionTubeContainer;
import schmoller.tubes.gui.BufferTubeContainer;
import schmoller.tubes.gui.CompressorContainer;
import schmoller.tubes.gui.FilterTubeContainer;
import schmoller.tubes.gui.InjectionTubeContainer;
import schmoller.tubes.gui.ManagementTubeContainer;
import schmoller.tubes.gui.OverflowContainer;
import schmoller.tubes.gui.RequestingTubeContainer;
import schmoller.tubes.gui.RoutingTubeContainer;
import schmoller.tubes.items.BasicBlock;
import schmoller.tubes.items.BasicItem;
import schmoller.tubes.items.ItemDiagnosticTool;
import schmoller.tubes.items.ItemTubeBase;
import schmoller.tubes.items.ItemTubeCap;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.network.packets.ModPacketClickButton;
import schmoller.tubes.network.packets.ModPacketNEIDragDrop;
import schmoller.tubes.parts.TubeCap;
import schmoller.tubes.types.AdvancedExtractionTube;
import schmoller.tubes.types.BufferTube;
import schmoller.tubes.types.CompressorTube;
import schmoller.tubes.types.FilterTube;
import schmoller.tubes.types.InjectionTube;
import schmoller.tubes.types.ManagementTube;
import schmoller.tubes.types.RequestingTube;
import schmoller.tubes.types.RoutingTube;

public class CommonProxy implements IModPacketHandler, IGuiHandler, IPartFactory
{
	public void initialize()
	{
		registerTubes();
		registerItems();
		registerRecipes();
		
		OreDictionary.registerOre("dustPlastic", Items.PlasticDust.getItem());
		OreDictionary.registerOre("sheetPlastic", Items.PlasticSheet.getItem());
		OreDictionary.registerOre("blockPlastic", Blocks.BlockPlastic.getBlock());
		
		MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(Blocks.BlockPlastic.getBlock(), 0), "tile.tubes.blockPlastic");
		FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%s@%d", Blocks.BlockPlastic.getBlock().getUnlocalizedName(), 0 ));
		
		NetworkRegistry.INSTANCE.registerGuiHandler(ModTubes.instance, this);
		MultiPartRegistry.registerParts(this, new String[] {"tubeCap"});
	}
	
	private void registerTubes()
	{
		TubeRegistry.registerTube(new TypeNormalTube(), "basic");
		TubeRegistry.registerTube(new TypeRestrictionTube(), "restriction");
		TubeRegistry.registerTube(new TypeInjectionTube(), "injection");
		TubeRegistry.registerTube(new TypeEjectionTube(), "ejection");
		TubeRegistry.registerTube(new TypeFilterTube(), "filter");
		TubeRegistry.registerTube(new TypeCompressorTube(), "compressor");
		TubeRegistry.registerTube(new TypeExtractionTube(), "extraction");
		TubeRegistry.registerTube(new TypeRequestingTube(), "requesting");
		TubeRegistry.registerTube(new TypeRoutingTube(), "routing");
		TubeRegistry.registerTube(new TypeValveTube(), "valve");
		TubeRegistry.registerTube(new TypeColoringTube(), "coloring");
		TubeRegistry.registerTube(new TypeFluidExtractionTube(), "fluidExtraction");
		TubeRegistry.registerTube(new TypeTankTube(), "tank");
		TubeRegistry.registerTube(new TypeBufferTube(), "buffer");
		TubeRegistry.registerTube(new TypeRoundRobinTube(), "roundrobin");
		TubeRegistry.registerTube(new TypeManagementTube(), "management");
		TubeRegistry.registerTube(new TypeAdvancedExtractionTube(), "advancedExtraction");
		
		
		PayloadRegistry.registerPayload(ItemPayload.class, "item", IInventory.class);
		PayloadRegistry.registerPayload(FluidPayload.class, "fluid", IFluidHandler.class);
	}
	
	private void registerItems()
	{
		Items.PlasticDust.initialize(new BasicItem().setUnlocalizedName("plasticPellets"));
		Items.PlasticSheet.initialize(new BasicItem().setUnlocalizedName("sheetPlastic"));
		Items.BucketMilkCurd.initialize(new BasicItem().setUnlocalizedName("milkCurd").setContainerItem(net.minecraft.init.Items.bucket).setCreativeTab(ModTubes.creativeTab).setMaxStackSize(1));
		Items.BucketPlastic.initialize(new BasicItem().setUnlocalizedName("bucketOfPlastic").setContainerItem(net.minecraft.init.Items.bucket).setCreativeTab(ModTubes.creativeTab).setMaxStackSize(1));
		Items.RedstoneCircuit.initialize(new BasicItem().setUnlocalizedName("redstoneCircuit").setCreativeTab(ModTubes.creativeTab));
		Items.FluidCircuit.initialize(new BasicItem().setUnlocalizedName("fluidCircuit").setCreativeTab(ModTubes.creativeTab));
		Items.DiagnosticTool.initialize(new ItemDiagnosticTool().setUnlocalizedName("diagnosticTool").setCreativeTab(ModTubes.creativeTab));
		Items.DiamondineCircuit.initialize(new BasicItem().setUnlocalizedName("diamondineCircuit").setCreativeTab(ModTubes.creativeTab));
		
		ModTubes.itemTube = new ItemTubeBase();
		Items.Tube.initialize(ModTubes.itemTube);
		
		GameRegistry.registerItem(Items.PlasticDust.getItem(), "dustPlastic");
		GameRegistry.registerItem(Items.PlasticSheet.getItem(), "sheetPlastic");
		GameRegistry.registerItem(Items.BucketMilkCurd.getItem(), "milkCurd");
		GameRegistry.registerItem(Items.BucketPlastic.getItem(), "bucketOfPlastic");
		GameRegistry.registerItem(Items.Tube.getItem(), "tube");
		GameRegistry.registerItem(Items.RedstoneCircuit.getItem(), "redstoneCircuit");
		GameRegistry.registerItem(Items.FluidCircuit.getItem(), "fluidCircuit");
		GameRegistry.registerItem(Items.DiagnosticTool.getItem(), "diagnosticTool");
		GameRegistry.registerItem(Items.DiamondineCircuit.getItem(), "diamondineCircuit");
		
		Items.TubeCap.initialize(new ItemTubeCap().setUnlocalizedName("tubeCap").setCreativeTab(ModTubes.creativeTab));
		GameRegistry.registerItem(Items.TubeCap.getItem(), "tubeCap");
		
		Blocks.BlockPlastic.initialize(new BasicBlock(Material.piston)
			.setCreativeTab(ModTubes.creativeTab)
			.setBlockName("blockPlastic")
			.setBlockTextureName("tubes:blockPlastic")
			.setHardness(2.5f));
		
		Blocks.BlockPlastic.getBlock().setHarvestLevel("pickaxe", 0);
		Blocks.BlockPlastic.getBlock().setHarvestLevel("axe", 0);
		
		GameRegistry.registerBlock(Blocks.BlockPlastic.getBlock(), "blockPlastic");
		
		ModTubes.fluidPlastic = new Fluid("plastic").setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(ModTubes.fluidPlastic);
		ModTubes.fluidPlastic = FluidRegistry.getFluid("plastic");
		
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("plastic", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.BucketPlastic.getItem()), new ItemStack(net.minecraft.init.Items.bucket));
	}
	
	private void registerRecipes()
	{
		RecipeSorter.register("tubes:shapeless", SpecialShapelessRecipe.class, Category.SHAPELESS, "before:minecraft:shapeless");
		RecipeSorter.register("tubes:shaped", SpecialShapedRecipe.class, Category.SHAPED, "before:tubes:shapeless");
		
		GameRegistry.addSmelting(Items.PlasticDust.getItem(), new ItemStack(Items.PlasticSheet.getItem()), 0);
		GameRegistry.addSmelting(net.minecraft.init.Items.milk_bucket, new ItemStack(Items.BucketMilkCurd.getItem()), 0);
		GameRegistry.addRecipe(new SpecialShapelessRecipe(new ItemStack(Items.PlasticDust.getItem(), ModTubes.instance.plasticYield * 4), Items.BucketMilkCurd.getItem(), new ItemStack(net.minecraft.init.Items.coal, 1, OreDictionary.WILDCARD_VALUE), net.minecraft.init.Items.gunpowder, FluidRegistry.getFluid("water")));
		GameRegistry.addRecipe(new SpecialShapelessRecipe(new ItemStack(Items.PlasticDust.getItem(), ModTubes.instance.plasticYield * 4), Items.BucketPlastic.getItem(), new ItemStack(net.minecraft.init.Items.coal, 1, OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new SpecialShapedRecipe(new ItemStack(Items.PlasticDust.getItem(), ModTubes.instance.plasticYield), " c ", "sCs", " c ", 'c', new ItemStack(net.minecraft.init.Items.coal, 1, OreDictionary.WILDCARD_VALUE), 'C', net.minecraft.init.Items.clay_ball, 's', net.minecraft.init.Blocks.sand));
		
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("basic", 8), "pgp", 'p', "sheetPlastic", 'g', net.minecraft.init.Blocks.glass));
		GameRegistry.addRecipe(new SpecialShapedRecipe(new ItemStack(Blocks.BlockPlastic.getBlock()), "pp","pp", 'p', "sheetPlastic"));
		
		GameRegistry.addRecipe(new SpecialShapelessRecipe(ModTubes.itemTube.createForType("restriction"), ModTubes.itemTube.createForType("basic"), net.minecraft.init.Items.iron_ingot));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("compressor"), "ipi", "ptp", "ipi", 'i', net.minecraft.init.Items.iron_ingot, 'p', net.minecraft.init.Blocks.piston, 't', ModTubes.itemTube.createForType("basic")));
		
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("injection"), " w ", "wtw", " w ", 'w', "plankWood", 't', ModTubes.itemTube.createForType("basic")));
		
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("extraction"), "h", "t", "p", 't', ModTubes.itemTube.createForType("basic"), 'h', net.minecraft.init.Blocks.hopper, 'p', net.minecraft.init.Blocks.sticky_piston));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("requesting"), "t", "e", "f", 't', ModTubes.itemTube.createForType("basic"), 'e', ModTubes.itemTube.createForType("extraction"), 'f', ModTubes.itemTube.createForType("filter")));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("filter"), "ici", "ctc", "ici", 'i', net.minecraft.init.Items.iron_ingot, 't', ModTubes.itemTube.createForType("basic"), 'c', Items.RedstoneCircuit.getItem()));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("routing"), "iti", "tft", "iti", 'i', net.minecraft.init.Items.iron_ingot, 't', ModTubes.itemTube.createForType("basic"), 'f', ModTubes.itemTube.createForType("filter")));
		GameRegistry.addRecipe(new SpecialShapelessRecipe(ModTubes.itemTube.createForType("ejection"), ModTubes.itemTube.createForType("basic"), net.minecraft.init.Blocks.glass));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("fluidExtraction"), "b", "t", "p", 'p', net.minecraft.init.Blocks.sticky_piston, 't', ModTubes.itemTube.createForType("basic"), 'b', net.minecraft.init.Items.bucket));
		
		TubesAPI.instance.registerShapelessRecipe(ModTubes.itemTube.createForType("valve"), ModTubes.itemTube.createForType("basic"), net.minecraft.init.Items.iron_ingot, net.minecraft.init.Blocks.lever);
		
		GameRegistry.addShapedRecipe(new ItemStack(Items.RedstoneCircuit.getItem(), 4), "igi", "rrr", "igi", 'i', net.minecraft.init.Items.iron_ingot, 'g', net.minecraft.init.Items.gold_ingot, 'r', net.minecraft.init.Items.redstone);
		GameRegistry.addShapedRecipe(new ItemStack(Items.DiamondineCircuit.getItem(), 2), "idi", "crc", "idi", 'i', net.minecraft.init.Items.iron_ingot, 'd', net.minecraft.init.Items.diamond, 'r', net.minecraft.init.Items.redstone, 'c', Items.RedstoneCircuit.getItem());
		GameRegistry.addShapedRecipe(new ItemStack(Items.DiagnosticTool.getItem(), 1), "p", "g", "r", 'p', Items.PlasticSheet.getItem(), 'g', net.minecraft.init.Blocks.glass, 'r', Items.RedstoneCircuit.getItem());
		
		TubesAPI.instance.registerShapelessRecipe(new ItemStack(Items.TubeCap.getItem(), 2, 0), TubesAPI.instance.createTubeForType("basic"), MicroblockProxy.sawStone());
		
		TubesAPI.instance.registerShapelessRecipe(new ItemStack(Items.FluidCircuit.getItem(), 1, 0), new ItemStack(Items.RedstoneCircuit.getItem(), 1, 0), net.minecraft.init.Items.bucket);
		
		TubesAPI.instance.registerShapedRecipe(TubesAPI.instance.createTubeForType("tank", 1), " g ", "gtg", " g ", 'g', net.minecraft.init.Blocks.glass, 't', TubesAPI.instance.createTubeForType("basic"));
		TubesAPI.instance.registerShapedRecipe(TubesAPI.instance.createTubeForType("buffer", 1), "t", "c", "t", 't', TubesAPI.instance.createTubeForType("basic"), 'c', net.minecraft.init.Blocks.chest);
		TubesAPI.instance.registerShapedRecipe(TubesAPI.instance.createTubeForType("roundrobin",1), "iti", "ttt", "iti", 'i', net.minecraft.init.Items.iron_ingot, 't', TubesAPI.instance.createTubeForType("basic"));
		TubesAPI.instance.registerShapedRecipe(TubesAPI.instance.createTubeForType("management",1), "iei", "prp", "pdp", 'i', net.minecraft.init.Items.iron_ingot, 'e', TubesAPI.instance.createTubeForType("advancedExtraction"), 'p', Items.PlasticSheet.getItem(), 'r', TubesAPI.instance.createTubeForType("requesting"), 'd', Items.DiamondineCircuit.getItem());
		TubesAPI.instance.registerShapedRecipe(TubesAPI.instance.createTubeForType("advancedExtraction",1), "ipi", "sds", "sts", 'i', net.minecraft.init.Items.iron_ingot, 'p', net.minecraft.init.Blocks.sticky_piston, 's', Items.PlasticSheet.getItem(), 't', TubesAPI.instance.createTubeForType("filter"), 'd', Items.DiamondineCircuit.getItem());
	}

	public void registerOreRecipes()
	{
		TubesAPI.instance.registerShapedRecipe(ModTubes.itemTube.createForType("coloring"), " d ", "dtd", " d ", 'd', "Tubes$anyDye", 't', ModTubes.itemTube.createForType("basic"));
	}
	
	@Override
	public boolean onPacketArrive( ModPacket packet, EntityPlayer sender )
	{
		if(packet instanceof ModPacketNEIDragDrop)
		{
			EntityPlayer player = sender;
			ModPacketNEIDragDrop drop = (ModPacketNEIDragDrop)packet;
			
			if(player.openContainer instanceof ExtContainer && player.openContainer.windowId == drop.windowId && drop.slot >= 0 && drop.slot < player.openContainer.inventorySlots.size())
			{
				Slot slot = (Slot)player.openContainer.inventorySlots.get(drop.slot);
				if(slot instanceof FakeSlot)
					((ExtContainer)player.openContainer).dropItem(drop.slot, drop.button, drop.modifiers, drop.item);
			}
		}
		else if(packet instanceof ModPacketClickButton)
		{
			ModPacketClickButton click = (ModPacketClickButton)packet;
			
			if(sender.openContainer instanceof ExtContainer && sender.openContainer.windowId == click.windowId && click.buttonId >= 0 && click.buttonId < ((ExtContainer)sender.openContainer).buttons.size())
				((ExtContainer)sender.openContainer).buttonClick(click.buttonId, click.mouseButton, click.modifiers, sender);
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
		case ModTubes.GUI_BUFFER_TUBE:
			return new BufferTubeContainer(player.inventory, CommonHelper.getMultiPart(world, x, y, z, BufferTube.class));
		case ModTubes.GUI_MANAGEMENT_TUBE:
			return new ManagementTubeContainer(CommonHelper.getMultiPart(world, x, y, z, ManagementTube.class), player);
		case ModTubes.GUI_OVERFLOW:
			return new OverflowContainer(CommonHelper.getInterface(world, x, y, z, ITubeOverflowDestination.class).getOverflowContents());
		case ModTubes.GUI_ADV_EXTRACTION:
			return new AdvancedExtractionTubeContainer(CommonHelper.getMultiPart(world, x, y, z, AdvancedExtractionTube.class), player);
		}
		
		return null;
	}

	@Override
	public TMultiPart createPart( String partType, boolean client )
	{
		if(partType.equals("tubeCap"))
			return new TubeCap();
		
		return null;
	}
}
