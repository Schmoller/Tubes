package schmoller.tubes.gui;

import java.util.Arrays;

import schmoller.tubes.ModTubes;
import schmoller.tubes.logic.PullMode;
import schmoller.tubes.logic.RequestingTubeLogic;
import schmoller.tubes.network.packets.ModPacketSetPullMode;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class RequestingTubeGui extends GuiContainer
{
	private RequestingTubeLogic mTube;
	public RequestingTubeGui(RequestingTubeLogic tube, EntityPlayer player)
	{
		super(new RequestingTubeContainer(tube, player));
		
		mTube = tube;
		
		xSize = 176;
		ySize = 154;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int curX, int curY )
	{
		String s = "Requesting Tube";
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
        super.drawGuiContainerForegroundLayer(curX, curY);

		int xx = curX - (width - xSize) / 2;
		int yy = curY - (height - ySize) / 2;
		
		int old = width;
		width -= (xx + curX); 
		if(xx >= 153 && xx <= 167 && yy >= 19 && yy <= 33)
		{
			String text = "";
			switch(mTube.getMode())
			{
			case Constant:
				text = "Always pull";
				break;
			case RedstoneConstant:
				text = "Pull while redstone is active";
				break;
			case RedstoneSingle:
				text = "Pull once per restone pulse";
				break;
			}
			drawHoveringText(Arrays.asList(text), xx, yy, fontRenderer);
		}
		width = old;
	}
	
	@Override
	protected void mouseClicked( int x, int y, int button )
	{
		int xx = x - (width - xSize) / 2;
		int yy = y - (height - ySize) / 2;
		
		
		super.mouseClicked(x, y, button);
		if(xx >= 153 && xx <= 167 && yy >= 19 && yy <= 33)
		{
			PullMode current = mTube.getMode();
			int i = current.ordinal();
			if(button == 0)
				++i;
			else if(button == 1)
				--i;
			
			if(i < 0)
				i = PullMode.values().length - 1;
			else if(i >= PullMode.values().length)
				i = 0;
			
			mTube.setMode(PullMode.values()[i]);
			ModTubes.packetManager.sendPacketToServer(new ModPacketSetPullMode(mTube.getTube().x(), mTube.getTube().y(), mTube.getTube().z(), PullMode.values()[i]));
		}
	}
	
	
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture("/mods/Tubes/textures/gui/requesterTube.png");
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		drawTexturedModalRect(x + 153, y + 19, 176, mTube.getMode().ordinal() * 14, 14, 14);
	}

}
