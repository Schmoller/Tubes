
package schmoller.tubes;

import net.minecraft.inventory.ISidedInventory;
import codechicken.multipart.MultipartGenerator;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;
import schmoller.tubes.definitions.InjectionTube;
import schmoller.tubes.definitions.NormalTube;
import schmoller.tubes.definitions.RestrictionTube;
import schmoller.tubes.network.IModPacketHandler;
import schmoller.tubes.network.ModPacket;

public class CommonProxy implements IModPacketHandler
{
	public void initialize()
	{
		registerTubes();
		registerText();
	}
	
	private void registerTubes()
	{
		MultipartGenerator.registerPassThroughInterface(ISidedInventory.class.getName(), true, true);
		
		TubeRegistry.registerTube(new NormalTube(), "basic");
		TubeRegistry.registerTube(new RestrictionTube(), "restriction");
		TubeRegistry.registerTube(new InjectionTube(), "injection");
	}
	
	private void registerText()
	{
		LanguageRegistry.instance().addStringLocalization("tubes.basic.name", "Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.restriction.name", "Restriction Tube");
		LanguageRegistry.instance().addStringLocalization("tubes.injection.name", "Injection Tube");
	}

	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
		return false;
	}
}
