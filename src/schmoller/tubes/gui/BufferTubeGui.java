package schmoller.tubes.gui;

import schmoller.tubes.definitions.TypeBufferTube;
import schmoller.tubes.types.BufferTube;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

public class BufferTubeGui extends GuiContainer
{
	public BufferTubeGui(InventoryPlayer inventory, BufferTube tube)
	{
		super(new BufferTubeContainer(inventory, tube));
		
		xSize = 176;
		ySize = 171;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int par1, int par2 )
	{
		String s = "Buffer Tube";
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture(TypeBufferTube.gui);
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}
