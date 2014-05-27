package schmoller.tubes.gui;

import net.minecraft.entity.player.EntityPlayer;
import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.definitions.TypeAdvancedExtractionTube;
import schmoller.tubes.types.AdvancedExtractionTube;

public class AdvancedExtractionTubeGui extends GuiExtContainer
{
	public AdvancedExtractionTubeGui(AdvancedExtractionTube tube, EntityPlayer player)
	{
		super(new AdvancedExtractionTubeContainer(tube, player));

		xSize = 176;
		ySize = 172;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float var1, int var2, int var3 )
	{
		mc.renderEngine.bindTexture(TypeAdvancedExtractionTube.gui);
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
	}

}
