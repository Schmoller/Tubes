
package schmoller.tubes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.microblock.handler.MicroblockProxy;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.MultipartGenerator;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;
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
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeBufferTube;
import schmoller.tubes.definitions.TypeColoringTube;
import schmoller.tubes.definitions.TypeCompressorTube;
import schmoller.tubes.definitions.TypeEjectionTube;
import schmoller.tubes.definitions.TypeExtractionTube;
import schmoller.tubes.definitions.TypeFilterTube;
import schmoller.tubes.definitions.TypeFluidExtractionTube;
import schmoller.tubes.definitions.TypeInjectionTube;
import schmoller.tubes.definitions.TypeNormalTube;
import schmoller.tubes.definitions.TypeRequestingTube;
import schmoller.tubes.definitions.TypeRestrictionTube;
import schmoller.tubes.definitions.TypeRoutingTube;
import schmoller.tubes.definitions.TypeTankTube;
import schmoller.tubes.definitions.TypeValveTube;
import schmoller.tubes.gui.BufferTubeContainer;
import schmoller.tubes.gui.CompressorContainer;
import schmoller.tubes.gui.FilterTubeContainer;
import schmoller.tubes.gui.InjectionTubeContainer;
import schmoller.tubes.gui.RequestingTubeContainer;
import schmoller.tubes.gui.RoutingTubeContainer;
import schmoller.tubes.items.BasicItem;
import schmoller.tubes.items.ItemTubeBase;
import schmoller.tubes.items.ItemTubeCap;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModBlockPacket;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.network.packets.ModPacketNEIDragDrop;
import schmoller.tubes.network.packets.ModPacketSetColor;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import schmoller.tubes.network.packets.ModPacketSetRequestingModes;
import schmoller.tubes.network.packets.ModPacketSetRoutingOptions;
import schmoller.tubes.parts.TubeCap;
import schmoller.tubes.types.BufferTube;
import schmoller.tubes.types.CompressorTube;
import schmoller.tubes.types.FilterTube;
import schmoller.tubes.types.InjectionTube;
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
		FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%d@%d", Blocks.BlockPlastic.getBlockID(), 0 ));
		
		NetworkRegistry.instance().registerGuiHandler(ModTubes.instance, this);
		MultiPartRegistry.registerParts(this, new String[] {"tubeCap"});
	}
	
	private Boolean mCanUseInjection;
	private boolean canUseInjection()
	{
		if(mCanUseInjection != null)
			return mCanUseInjection;
		
		try
		{
			ArtifactVersion version = getMultipartVersion();
			
			if(!VersionRange.createFromVersionSpec("[1.0.0.211,)").containsVersion(version))
			{
				System.err.println("**** WARNING: The version of ForgeMultiPart you are running is too old to support the Injection Tube. As such the Injection Tube has been disabled. Please update to a newer version. ****");
				mCanUseInjection = false;
			}
			else if(VersionRange.createFromVersionSpec("[1.0.0.238,1.0.0.245]").containsVersion(version))
			{
				System.err.println("**** WARNING: The version of ForgeMultiPart you are running is known to have an issue with Tubes. As such the Injection Tube has been disabled. Please update to a newer version. ****");
				mCanUseInjection = false;
			}
			else
				mCanUseInjection = true;
		}
		catch ( InvalidVersionSpecificationException e )
		{
			e.printStackTrace();
			mCanUseInjection = false;
		} 
		
		return mCanUseInjection;
	}
	
	private ArtifactVersion getMultipartVersion()
	{
		ModContainer mod = Loader.instance().getIndexedModList().get("ForgeMultipart");
		return mod.getProcessedVersion();
	}
	
	private void registerTubes()
	{
		if(canUseInjection())
			MultipartGenerator.registerPassThroughInterface("net.minecraft.inventory.ISidedInventory");
		
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
		
		
		PayloadRegistry.registerPayload(ItemPayload.class, "item", IInventory.class);
		PayloadRegistry.registerPayload(FluidPayload.class, "fluid", IFluidHandler.class);
	}
	
	private void registerItems()
	{
		Items.PlasticDust.initialize(Items.PlasticDust.getItemID() + 256, new BasicItem(Items.PlasticDust.getItemID()).setUnlocalizedName("plasticPellets"));
		Items.PlasticSheet.initialize(Items.PlasticSheet.getItemID() + 256, new BasicItem(Items.PlasticSheet.getItemID()).setUnlocalizedName("sheetPlastic"));
		Items.BucketMilkCurd.initialize(Items.BucketMilkCurd.getItemID() + 256, new BasicItem(Items.BucketMilkCurd.getItemID()).setUnlocalizedName("milkCurd").setContainerItem(Item.bucketEmpty).setCreativeTab(ModTubes.creativeTab).setMaxStackSize(1));
		Items.BucketPlastic.initialize(Items.BucketPlastic.getItemID() + 256, new BasicItem(Items.BucketPlastic.getItemID()).setUnlocalizedName("bucketOfPlastic").setContainerItem(Item.bucketEmpty).setCreativeTab(ModTubes.creativeTab).setMaxStackSize(1));
		Items.RedstoneCircuit.initialize(Items.RedstoneCircuit.getItemID() + 256, new BasicItem(Items.RedstoneCircuit.getItemID()).setUnlocalizedName("redstoneCircuit").setCreativeTab(ModTubes.creativeTab));
		Items.FluidCircuit.initialize(Items.FluidCircuit.getItemID() + 256, new BasicItem(Items.FluidCircuit.getItemID()).setUnlocalizedName("fluidCircuit").setCreativeTab(ModTubes.creativeTab));
		
		ModTubes.itemTube = new ItemTubeBase(Items.Tube.getItemID());
		Items.Tube.initialize(Items.Tube.getItemID() + 256, ModTubes.itemTube);
		
		GameRegistry.registerItem(Items.PlasticDust.getItem(), "dustPlastic");
		GameRegistry.registerItem(Items.PlasticSheet.getItem(), "sheetPlastic");
		GameRegistry.registerItem(Items.BucketMilkCurd.getItem(), "milkCurd");
		GameRegistry.registerItem(Items.BucketPlastic.getItem(), "bucketOfPlastic");
		GameRegistry.registerItem(Items.Tube.getItem(), "tubes:items:tube");
		GameRegistry.registerItem(Items.RedstoneCircuit.getItem(), "redstoneCircuit");
		GameRegistry.registerItem(Items.FluidCircuit.getItem(), "fluidCircuit");
		
		Items.TubeCap.initialize(Items.TubeCap.getItemID() + 256, new ItemTubeCap(Items.TubeCap.getItemID()).setUnlocalizedName("tubeCap").setCreativeTab(ModTubes.creativeTab));
		GameRegistry.registerItem(Items.TubeCap.getItem(), "tubeCap");
		
		Blocks.BlockPlastic.initialize(Blocks.BlockPlastic.getBlockID(), new Block(Blocks.BlockPlastic.getBlockID(), Material.piston)
			.setCreativeTab(ModTubes.creativeTab)
			.setUnlocalizedName("blockPlastic")
			.setTextureName("tubes:blockPlastic")
			.setHardness(2.5f));
		
		MinecraftForge.setBlockHarvestLevel(Blocks.BlockPlastic.getBlock(), "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.BlockPlastic.getBlock(), "axe", 0);
		
		GameRegistry.registerBlock(Blocks.BlockPlastic.getBlock(), "blockPlastic");
		
		ModTubes.fluidPlastic = new Fluid("plastic").setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(ModTubes.fluidPlastic);
		ModTubes.fluidPlastic = FluidRegistry.getFluid("plastic");
		
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("plastic", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.BucketPlastic.getItem()), new ItemStack(Item.bucketEmpty));
	}
	
	private void registerRecipes()
	{
		GameRegistry.addSmelting(Items.PlasticDust.getItemID(), new ItemStack(Items.PlasticSheet.getItem()), 0);
		GameRegistry.addSmelting(Item.bucketMilk.itemID, new ItemStack(Items.BucketMilkCurd.getItem()), 0);
		GameRegistry.addRecipe(new SpecialShapelessRecipe(new ItemStack(Items.PlasticDust.getItem(), ModTubes.instance.plasticYield * 4), Items.BucketMilkCurd.getItem(), new ItemStack(Item.coal, 1, OreDictionary.WILDCARD_VALUE), Item.gunpowder, FluidRegistry.getFluid("water")));
		GameRegistry.addRecipe(new SpecialShapelessRecipe(new ItemStack(Items.PlasticDust.getItem(), ModTubes.instance.plasticYield * 4), Items.BucketPlastic.getItem(), new ItemStack(Item.coal, 1, OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new SpecialShapedRecipe(new ItemStack(Items.PlasticDust.getItem(), ModTubes.instance.plasticYield), " c ", "sCs", " c ", 'c', new ItemStack(Item.coal, 1, OreDictionary.WILDCARD_VALUE), 'C', Item.clay, 's', Block.sand));
		
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("basic", 8), "pgp", 'p', "sheetPlastic", 'g', Block.glass));
		GameRegistry.addRecipe(new SpecialShapedRecipe(new ItemStack(Blocks.BlockPlastic.getBlock()), "pp","pp", 'p', "sheetPlastic"));
		
		GameRegistry.addRecipe(new SpecialShapelessRecipe(ModTubes.itemTube.createForType("restriction"), ModTubes.itemTube.createForType("basic"), Item.ingotIron));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("compressor"), "ipi", "ptp", "ipi", 'i', Item.ingotIron, 'p', Block.pistonBase, 't', ModTubes.itemTube.createForType("basic")));
		
		if(canUseInjection())
			GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("injection"), " w ", "wtw", " w ", 'w', "plankWood", 't', ModTubes.itemTube.createForType("basic")));
		
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("extraction"), "h", "t", "p", 't', ModTubes.itemTube.createForType("basic"), 'h', Block.hopperBlock, 'p', Block.pistonStickyBase));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("requesting"), "t", "e", "f", 't', ModTubes.itemTube.createForType("basic"), 'e', ModTubes.itemTube.createForType("extraction"), 'f', ModTubes.itemTube.createForType("filter")));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("filter"), "ici", "ctc", "ici", 'i', Item.ingotIron, 't', ModTubes.itemTube.createForType("basic"), 'c', Items.RedstoneCircuit.getItem()));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("routing"), "iti", "tft", "iti", 'i', Item.ingotIron, 't', ModTubes.itemTube.createForType("basic"), 'f', ModTubes.itemTube.createForType("filter")));
		GameRegistry.addRecipe(new SpecialShapelessRecipe(ModTubes.itemTube.createForType("ejection"), ModTubes.itemTube.createForType("basic"), Block.glass));
		GameRegistry.addRecipe(new SpecialShapedRecipe(ModTubes.itemTube.createForType("fluidExtraction"), "f", "t", "f", 'f', Items.FluidCircuit.getItem(), 't', ModTubes.itemTube.createForType("extraction")));
		
		TubesAPI.instance.registerShapelessRecipe(ModTubes.itemTube.createForType("valve"), ModTubes.itemTube.createForType("basic"), Item.ingotIron, Block.lever);
		
		GameRegistry.addShapedRecipe(new ItemStack(Items.RedstoneCircuit.getItem(), 4), "igi", "rrr", "igi", 'i', Item.ingotIron, 'g', Item.ingotGold, 'r', Item.redstone);
		
		TubesAPI.instance.registerShapelessRecipe(new ItemStack(Items.TubeCap.getItem(), 2, 0), TubesAPI.instance.createTubeForType("basic"), MicroblockProxy.sawStone());
		
		TubesAPI.instance.registerShapelessRecipe(new ItemStack(Items.FluidCircuit.getItem(), 1, 0), new ItemStack(Items.RedstoneCircuit.getItem(), 1, 0), Item.bucketEmpty);
		
		TubesAPI.instance.registerShapedRecipe(TubesAPI.instance.createTubeForType("tank", 1), " g ", "gtg", " g ", 'g', Block.glass, 't', TubesAPI.instance.createTubeForType("basic"));
		TubesAPI.instance.registerShapedRecipe(TubesAPI.instance.createTubeForType("buffer", 1), "t", "c", "t", 't', TubesAPI.instance.createTubeForType("basic"), 'c', Block.chest);
		
	}

	public void registerOreRecipes()
	{
		TubesAPI.instance.registerShapedRecipe(ModTubes.itemTube.createForType("coloring"), " d ", "dtd", " d ", 'd', "Tubes$anyDye", 't', ModTubes.itemTube.createForType("basic"));
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
			else if(packet instanceof ModPacketSetRequestingModes && tube instanceof RequestingTube)
			{
				ModPacketSetRequestingModes mpacket = (ModPacketSetRequestingModes)packet;
				
				if(mpacket.mode != null)
					((RequestingTube)tube).setMode(mpacket.mode);
				else
					((RequestingTube)tube).setSizeMode(mpacket.sizeMode);
				
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
			ModPacketNEIDragDrop drop = (ModPacketNEIDragDrop)packet;
			
			if(player.openContainer instanceof ExtContainer && player.openContainer.windowId == drop.windowId && drop.slot >= 0 && drop.slot < player.openContainer.inventorySlots.size())
			{
				Slot slot = (Slot)player.openContainer.inventorySlots.get(drop.slot);
				if(slot instanceof FakeSlot)
					((ExtContainer)player.openContainer).dropItem(drop.slot, drop.button, drop.modifiers, drop.item);
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
		case ModTubes.GUI_BUFFER_TUBE:
			return new BufferTubeContainer(player.inventory, CommonHelper.getMultiPart(world, x, y, z, BufferTube.class));
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
