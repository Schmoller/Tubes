package schmoller.tubes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import schmoller.tubes.gui.FilterTubeGui;
import schmoller.tubes.gui.InjectionTubeGui;
import schmoller.tubes.logic.FilterTubeLogic;
import schmoller.tubes.network.ModPacket;
import schmoller.tubes.parts.InventoryTubePart;
import schmoller.tubes.render.EjectionTubeRender;
import schmoller.tubes.render.FilterTubeRender;
import schmoller.tubes.render.InjectionTubeRender;
import schmoller.tubes.render.NormalTubeRender;
import schmoller.tubes.render.RenderTubeItem;
import cpw.mods.fml.common.network.Player;

public class ClientProxy extends CommonProxy
{
	@Override
	public void initialize()
	{
		super.initialize();
		registerRenderers();
	}
	
	private void registerRenderers()
	{
		MinecraftForgeClient.registerItemRenderer(ModTubes.instance.itemTubeId + 256, new RenderTubeItem());
		
		NormalTubeRender normal = new NormalTubeRender();
		TubeRegistry.registerRenderer("basic",normal);
		TubeRegistry.registerRenderer("restriction",normal);
		TubeRegistry.registerRenderer("injection",new InjectionTubeRender());
		TubeRegistry.registerRenderer("ejection", new EjectionTubeRender());
		TubeRegistry.registerRenderer("filter", new FilterTubeRender());
	}
	
	@Override
	public boolean onPacketArrive( ModPacket packet, Player sender )
	{
		return super.onPacketArrive(packet, sender);
	}
	
	@Override
	public Object getClientGuiElement( int ID, EntityPlayer player, World world, int x, int y, int z )
	{
		switch(ID)
		{
		case ModTubes.GUI_INJECTION_TUBE:
			return new InjectionTubeGui(CommonHelper.getMultiPart(world, x, y, z, InventoryTubePart.class), player);
		case ModTubes.GUI_FILTER_TUBE:
			return new FilterTubeGui((FilterTubeLogic)CommonHelper.getMultiPart(world, x, y, z, ITube.class).getLogic(), player);
		}
		
		return null;
	}
}
