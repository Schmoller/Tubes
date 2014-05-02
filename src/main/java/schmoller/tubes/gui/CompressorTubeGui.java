package schmoller.tubes.gui;

import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.definitions.TypeCompressorTube;
import schmoller.tubes.types.CompressorTube;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class CompressorTubeGui extends GuiExtContainer
{
	public CompressorTubeGui(CompressorTube tube, EntityPlayer player)
	{
		super(new CompressorContainer(tube, player));
		xSize = 176;
		ySize = 149;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int curX, int curY )
	{
		String s = StatCollector.translateToLocal("tubes.compressor.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
		super.drawGuiContainerForegroundLayer(curX, curY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture(TypeCompressorTube.gui);
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
