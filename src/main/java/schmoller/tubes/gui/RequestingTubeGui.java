package schmoller.tubes.gui;

import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.definitions.TypeRequestingTube;
import schmoller.tubes.types.RequestingTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class RequestingTubeGui extends GuiExtContainer
{
	public RequestingTubeGui(RequestingTube tube, EntityPlayer player)
	{
		super(new RequestingTubeContainer(tube, player));
		
		xSize = 176;
		ySize = 154;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int curX, int curY )
	{
		String s = StatCollector.translateToLocal("tubes.requesting.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
        super.drawGuiContainerForegroundLayer(curX, curY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		mc.renderEngine.bindTexture(TypeRequestingTube.gui);
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		super.drawGuiContainerBackgroundLayer(f, i, j);
	}

}
