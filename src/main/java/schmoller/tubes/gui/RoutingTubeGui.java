package schmoller.tubes.gui;

import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.definitions.TypeRoutingTube;
import schmoller.tubes.types.RoutingTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class RoutingTubeGui extends GuiExtContainer
{
	public RoutingTubeGui(RoutingTube tube, EntityPlayer player)
	{
		super(new RoutingTubeContainer(tube, player));
		
		xSize = 176;
		ySize = 220;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int curX, int curY )
	{
		String s = StatCollector.translateToLocal("tubes.routing.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
        super.drawGuiContainerForegroundLayer(curX, curY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		mc.renderEngine.bindTexture(TypeRoutingTube.gui);
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		super.drawGuiContainerBackgroundLayer(f, i, j);
	}

}
