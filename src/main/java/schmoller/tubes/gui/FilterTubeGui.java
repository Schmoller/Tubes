package schmoller.tubes.gui;

import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.definitions.TypeFilterTube;
import schmoller.tubes.types.FilterTube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class FilterTubeGui extends GuiExtContainer
{
	public FilterTubeGui(FilterTube tube, EntityPlayer player)
	{
		super(new FilterTubeContainer(tube, player));
		
		xSize = 176;
		ySize = 154;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int curX, int curY )
	{
		String s = StatCollector.translateToLocal("tubes.filter.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
        super.drawGuiContainerForegroundLayer(curX, curY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		mc.renderEngine.bindTexture(TypeFilterTube.gui);
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		super.drawGuiContainerBackgroundLayer(f, i, j);
	}

}
