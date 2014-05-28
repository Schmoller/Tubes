package schmoller.tubes.gui;

import net.minecraft.entity.player.EntityPlayer;
import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.definitions.TypeAdvancedExtractionTube;
import schmoller.tubes.types.AdvancedExtractionTube;
import schmoller.tubes.types.AdvancedExtractionTube.PullMode;

public class AdvancedExtractionTubeGui extends GuiExtContainer
{
	private AdvancedExtractionTube mTube;
	public AdvancedExtractionTubeGui(AdvancedExtractionTube tube, EntityPlayer player)
	{
		super(new AdvancedExtractionTubeContainer(tube, player));

		mTube = tube;
		
		xSize = 176;
		ySize = 172;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float var1, int var2, int var3 )
	{
		mc.renderEngine.bindTexture(TypeAdvancedExtractionTube.gui);
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
		if(mTube.getMode() == PullMode.Sequence)
		{
			for(int r = 0; r < 3; ++r)
			{
				for(int c = 0; c < 8; ++c)
				{
					if(mTube.getFilter(c + (r * 8)) == null)
					{
						if(c == 7)
							drawTexturedModalRect(guiLeft + 8 + (18 * c), guiTop + 20 + (18 * r), 16, ySize, 16, 16);
						else
							drawTexturedModalRect(guiLeft + 8 + (18 * c), guiTop + 20 + (18 * r), 0, ySize, 16, 16);
					}
				}
			}
			
			int start = mTube.getNext();
			int next = start;
			IFilter filter = null;
			int count = 0;
			do
			{
				filter = mTube.getFilter(next++);
				if(next >= 24)
					next = 0;
				
				++count;
			}
			while(filter == null && next != start);
			
			if(next == start && count > 1)
				next = 0;
			else
				--next;
			
			drawTexturedModalRect(guiLeft + 5 + (18 * (next % 8)), guiTop + 17 + (18 * (next / 8)), 32, ySize, 22, 22);
		}
	}

}
