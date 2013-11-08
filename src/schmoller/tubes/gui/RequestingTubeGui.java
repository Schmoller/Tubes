package schmoller.tubes.gui;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import schmoller.tubes.ModTubes;
import schmoller.tubes.PullMode;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.definitions.TypeRequestingTube;
import schmoller.tubes.network.packets.ModPacketSetColor;
import schmoller.tubes.network.packets.ModPacketSetRequestingModes;
import schmoller.tubes.types.RequestingTube;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class RequestingTubeGui extends GuiExtContainer
{
	private RequestingTube mTube;
	public RequestingTubeGui(RequestingTube tube, EntityPlayer player)
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
		if(xx >= 153 && xx <= 167)
		{
			if(yy >= 19 && yy <= 33)
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
				RenderHelper.enableGUIStandardItemLighting();
			}
			else if(yy >= 35 && yy <= 49)
			{
				String text = "";
				switch(mTube.getSizeMode())
				{
				case Exact:
					text = "Pull Exact Amount";
					break;
				case GreaterEqual:
					text = "Pull At Least Amount";
					break;
				case LessEqual:
					text = "Pull At Most Amount";
					break;
				case Max:
					text = "Pull Maximum Amount";
					break;
				}
				
				drawHoveringText(Arrays.asList(text), xx, yy, fontRenderer);
				RenderHelper.enableGUIStandardItemLighting();
			}
			else if(yy >= 51 && yy <= 65)
			{
				int colour = mTube.getColour();
				String text = "No Color";
				if(colour != -1)
					text = CommonHelper.getDyeName(colour);
				
				drawHoveringText(Arrays.asList(text), xx, yy, fontRenderer);
				RenderHelper.enableGUIStandardItemLighting();
			}
			
		}
		width = old;
	}
	
	@Override
	protected void mouseClicked( int x, int y, int button )
	{
		int xx = x - (width - xSize) / 2;
		int yy = y - (height - ySize) / 2;
		
		
		super.mouseClicked(x, y, button);
		if(xx >= 153 && xx <= 167)
		{
			if(yy >= 19 && yy <= 33)
			{
				PullMode current = mTube.getMode();
				int i = current.ordinal();
				if(button == 0)
					++i;
				else if(button == 1)
					--i;
				else if(button == 2)
					i = 0;
				
				if(i < 0)
					i = PullMode.values().length - 1;
				else if(i >= PullMode.values().length)
					i = 0;
				
				mTube.setMode(PullMode.values()[i]);
				ModTubes.packetManager.sendPacketToServer(new ModPacketSetRequestingModes(mTube.x(), mTube.y(), mTube.z(), PullMode.values()[i]));
			}
			else if(yy >= 35 && yy <= 49)
			{
				SizeMode current = mTube.getSizeMode();
				int i = current.ordinal();
				if(button == 0)
					++i;
				else if(button == 1)
					--i;
				else if(button == 2)
					i = 0;
				
				if(i < 0)
					i = SizeMode.values().length - 1;
				else if(i >= SizeMode.values().length)
					i = 0;
				
				mTube.setSizeMode(SizeMode.values()[i]);
				ModTubes.packetManager.sendPacketToServer(new ModPacketSetRequestingModes(mTube.x(), mTube.y(), mTube.z(), SizeMode.values()[i]));
			}
			else if(yy >= 51 && yy <= 65)
			{
				int colour = mTube.getColour();
				
				if(button == 0)
					++colour;
				else if(button == 1)
					-- colour;
				else if(button == 2)
					colour = -1;
				
				if(colour > 15)
					colour = -1;
				if(colour < -1)
					colour = 15;
				
				mTube.setColour((short)colour);
				
				ModTubes.packetManager.sendPacketToServer(new ModPacketSetColor(mTube.x(), mTube.y(), mTube.z(), colour));
			}
		}
	}
	
	
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture(TypeRequestingTube.gui);
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		drawTexturedModalRect(x + 153, y + 19, 176, mTube.getMode().ordinal() * 14, 14, 14);
		
		drawTexturedModalRect(x + 153, y + 35, 190, mTube.getSizeMode().ordinal() * 14, 14, 14);
		
		int colour = mTube.getColour();
		
		if(colour != -1)
		{
			drawRect(x + 156, y + 54, x + 164, y + 62, CommonHelper.getDyeColor(colour));
			GL11.glColor4f(1f, 1f, 1f, 1f);
		}
	}

}
