package schmoller.tubes.gui;

import org.lwjgl.opengl.GL11;

import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.definitions.TypeCompressorTube;
import schmoller.tubes.types.CompressorTube;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class CompressorTubeGui extends GuiExtContainer
{
	private CompressorTube mTube;
	public CompressorTubeGui(CompressorTube tube, EntityPlayer player)
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
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture(TypeCompressorTube.gui);
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		if(mTube.getTargetType() instanceof ItemPayload && ((ItemStack)mTube.getTargetType().get()).itemID == 0)
			drawTexturedModalRect(x + 114, y + 23, 176, 0, 20, 20);
	}
	
	@Override
	public void drawScreen( int par1, int par2, float par3 )
	{
		if(mTube.getTargetType() instanceof ItemPayload && ((ItemStack)mTube.getTargetType().get()).itemID == 0)
			((FakeSlot)inventorySlots.inventorySlots.get(1)).setHidden(true);

		super.drawScreen(par1, par2, par3);
		
		if(mTube.getTargetType() instanceof ItemPayload && ((ItemStack)mTube.getTargetType().get()).itemID == 0)
			((FakeSlot)inventorySlots.inventorySlots.get(1)).setHidden(false);
	}
	@Override
	protected void drawSlotInventory( Slot slot )
	{
		if(slot instanceof FakeSlot && mTube.getTargetType() instanceof ItemPayload && ((ItemStack)mTube.getTargetType().get()).itemID == 0)
		{
			String size = String.valueOf(((ItemStack)mTube.getTargetType().get()).stackSize);
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
