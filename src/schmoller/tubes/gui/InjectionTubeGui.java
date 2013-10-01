package schmoller.tubes.gui;

import schmoller.tubes.parts.InventoryTubePart;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class InjectionTubeGui extends GuiContainer
{
	public InjectionTubeGui(InventoryTubePart tube, EntityPlayer player)
	{
		super(new InjectionTubeContainer(tube, player));
		
		xSize = 176;
		ySize = 136;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int par1, int par2 )
	{
		String s = "Injection Tube";
		fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
		super.drawGuiContainerForegroundLayer(par1, par2);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		mc.renderEngine.bindTexture("/mods/Tubes/textures/gui/injectionTube.png");
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}
