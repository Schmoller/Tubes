package schmoller.tubes.gui;

import java.util.Arrays;

import schmoller.tubes.ModTubes;
import schmoller.tubes.logic.FilterTubeLogic;
import schmoller.tubes.logic.FilterTubeLogic.Comparison;
import schmoller.tubes.logic.FilterTubeLogic.Mode;
import schmoller.tubes.network.packets.ModPacketSetFilterMode;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class FilterTubeGui extends GuiContainer
{
	private FilterTubeLogic mTube;
	public FilterTubeGui(FilterTubeLogic tube, EntityPlayer player)
	{
		super(new FilterTubeContainer(tube, player));
		
		mTube = tube;
		
		xSize = 176;
		ySize = 154;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int curX, int curY )
	{
		String s = "Filter Tube";
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
        super.drawGuiContainerForegroundLayer(curX, curY);

		int xx = curX - (width - xSize) / 2;
		int yy = curY - (height - ySize) / 2;

		if(xx >= 153 && xx <= 167)
		{
			if(yy >= 19 && yy <= 33) // Mode button
			{
				drawHoveringText(Arrays.asList(mTube.getMode() == Mode.Allow ? "Allow specified items" : "Deny specified items"), xx, yy, fontRenderer);
			}
			else if(yy >= 35 && yy <= 49) // Comparison button
			{
				String text = "";
				String mode = mTube.getMode().name();
				
				switch(mTube.getComparison())
				{
				case Any:
					text = mode + " stacks specified with any size";
					break;
				case Exact:
					text = mode + " stacks specified with the same size";
					break;
				case Greater:
					text = mode + " stacks specified with a larger size";
					break;
				case Less:
					text = mode + " stacks specified with a smaller size";
					break;
				}

				int old = width;
				width -= (xx + curX); 
				drawHoveringText(Arrays.asList(text), xx, yy, fontRenderer);
				
				width = old;
			}
		}
		

		
	}
	
	@Override
	protected void mouseClicked( int x, int y, int button )
	{
		int xx = x - (width - xSize) / 2;
		int yy = y - (height - ySize) / 2;
		
		if(xx >= 153 && xx <= 167)
		{
			if(yy >= 19 && yy <= 33) // Mode button
			{
				Mode current = mTube.getMode();
				int i = current.ordinal();
				if(button == 0)
					++i;
				else if(button == 1)
					--i;
				
				if(i < 0)
					i = Mode.values().length - 1;
				else if(i >= Mode.values().length)
					i = 0;
				
				mTube.setMode(Mode.values()[i]);
				ModTubes.packetManager.sendPacketToServer(new ModPacketSetFilterMode(mTube.getTube().x(), mTube.getTube().y(), mTube.getTube().z(), Mode.values()[i]));
			}
			else if(yy >= 35 && yy <= 49) // Comparison button
			{
				Comparison current = mTube.getComparison();
				int i = current.ordinal();
				if(button == 0)
					++i;
				else if(button == 1)
					--i;
				
				if(i < 0)
					i = Comparison.values().length - 1;
				else if(i >= Comparison.values().length)
					i = 0;
				
				mTube.setComparison(Comparison.values()[i]);
				ModTubes.packetManager.sendPacketToServer(new ModPacketSetFilterMode(mTube.getTube().x(), mTube.getTube().y(), mTube.getTube().z(), Comparison.values()[i]));
			}
		}
		super.mouseClicked(x, y, button);
	}
	
	
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture("/mods/Tubes/textures/gui/filterTube.png");
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		drawTexturedModalRect(x + 153, y + 19, xSize, 14 * mTube.getMode().ordinal(), 14, 14);
		drawTexturedModalRect(x + 153, y + 35, xSize + 14, 14 * mTube.getComparison().ordinal(), 14, 14);
		
	}

}
