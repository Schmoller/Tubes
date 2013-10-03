package schmoller.tubes.gui;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import schmoller.tubes.logic.CompressorTubeLogic;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class CompressorTubeGui extends GuiContainer
{
	private CompressorTubeLogic mTube;
	public CompressorTubeGui(CompressorTubeLogic tube, EntityPlayer player)
	{
		super(new CompressorContainer(tube, player));
		xSize = 176;
		ySize = 149;
		
		mTube = tube;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int curX, int curY )
	{
		String s = "Compressor Tube";
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        
		super.drawGuiContainerForegroundLayer(curX, curY);
		
		int xx = curX - (width - xSize) / 2;
		int yy = curY - (height - ySize) / 2;
		
		if(xx >= 116 && xx <= 132 && yy >= 25 && yy <= 41)
		{
			int old = width;
			width -= (xx + curX); 
			ItemStack item = mTube.getTargetType();
			
			if(item.itemID == 0)
				drawHoveringText(Arrays.asList("Compressing any stack to " + item.stackSize + " items."), xx, yy, fontRenderer);
			else
				drawHoveringText(Arrays.asList("Compressing " + item.getDisplayName() + " to " + item.stackSize + " items."), xx, yy, fontRenderer);
			
			width = old;
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture("/mods/Tubes/textures/gui/compressorTube.png");
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		if(mTube.getTargetType().itemID == 0)
			drawTexturedModalRect(x + 114, y + 23, 176, 0, 20, 20);
	}
	
	@Override
	protected void drawItemStackTooltip( ItemStack item, int x, int y )
	{
		super.drawItemStackTooltip(item, x, y);
	}
	
	@Override
	public void drawScreen( int par1, int par2, float par3 )
	{
		if(mTube.getTargetType().itemID == 0)
			((FakeSlot)inventorySlots.inventorySlots.get(1)).setHidden(true);

		super.drawScreen(par1, par2, par3);
		
		if(mTube.getTargetType().itemID == 0)
			((FakeSlot)inventorySlots.inventorySlots.get(1)).setHidden(false);
	}
	@Override
	protected void drawSlotInventory( Slot slot )
	{
		if(slot instanceof FakeSlot && mTube.getTargetType().itemID == 0)
		{
			String size = String.valueOf(mTube.getTargetType().stackSize);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            fontRenderer.drawStringWithShadow(size, slot.xDisplayPosition + 19 - 2 - fontRenderer.getStringWidth(size), slot.yDisplayPosition + 6 + 3, 16777215);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		else
			super.drawSlotInventory(slot);
	}

}
