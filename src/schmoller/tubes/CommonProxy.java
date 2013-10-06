
package schmoller.tubes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.world.World;
import codechicken.multipart.MultipartGenerator;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;
import schmoller.tubes.definitions.CompressorTube;
import schmoller.tubes.definitions.EjectionTube;
import schmoller.tubes.definitions.ExtractionTube;
import schmoller.tubes.definitions.FilterTube;
import schmoller.tubes.definitions.InjectionTube;
import schmoller.tubes.definitions.NormalTube;
import schmoller.tubes.definitions.RequestingTube;
import schmoller.tubes.definitions.RestrictionTube;
import schmoller.tubes.gui.CompressorContainer;
import schmoller.tubes.gui.FilterTubeContainer;
import schmoller.tubes.gui.InjectionTubeContainer;
import schmoller.tubes.gui.RequestingTubeContainer;
import schmoller.tubes.logic.CompressorTubeLogic;
import schmoller.tubes.logic.FilterTubeLogic;
import schmoller.tubes.logic.PullMode;
import schmoller.tubes.logic.RequestingTubeLogic;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModBlockPacket;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import schmoller.tubes.network.packets.ModPacketSetPullMode;
import schmoller.tubes.parts.InventoryTubePart;

public class CommonProxy implements IModPacketHandler, IGuiHandler
{
	public void initialize()
	{
		registerTubes();
		registerText();
		
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
		}
		
		return null;
	}
}
