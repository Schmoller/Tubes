
package schmoller.tubes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.world.World;
import codechicken.multipart.MultipartGenerator;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;
import schmoller.tubes.definitions.EjectionTube;
import schmoller.tubes.definitions.InjectionTube;
import schmoller.tubes.definitions.NormalTube;
import schmoller.tubes.definitions.RestrictionTube;
import schmoller.tubes.gui.InjectionTubeContainer;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModPacket;
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
	}
	
	private void registerText()
	{
		LanguageRegistry.instance().addStringLocalization("tubes.basic.name", "Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.restriction.name", "Restriction Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.injection.name", "Injection Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.ejection.name", "Ejection Tube");
	}

	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
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
		}
		
		return null;
	}
}
