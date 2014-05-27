package schmoller.tubes.api.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.network.packets.ModPacketClickButton;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import java.util.Iterator;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;

public abstract class GuiExtContainer extends GuiContainer
{
	public GuiExtContainer(ExtContainer container)
	{
		super(container);
	}
	
	public int getLeft()
	{
		return guiLeft;
	}
	
	public int getTop()
	{
		return guiTop;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		for(GuiBaseButton button : ((ExtContainer)inventorySlots).buttons)
		{
			button.displayX = button.x + guiLeft;
			button.displayY = button.y + guiTop;
		}
	}
	
	protected boolean isMouseOverButton(GuiBaseButton button, int x, int y)
	{
		int left = guiLeft + button.x;
		int top = guiTop + button.y;
		
		return (x >= left && x < left + 14 && y >= top && y < top + 14);
	}
	
	protected GuiBaseButton getButtonAtPosition(int x, int y)
	{
		for(GuiBaseButton button : ((ExtContainer)inventorySlots).buttons)
		{
			if(isMouseOverButton(button, x, y))
				return button;
		}
		
		return null;
	}
	
	@Override
	protected void mouseClicked( int x, int y, int mouseButton )
	{
		GuiBaseButton button = getButtonAtPosition(x, y);
		if(button != null)
		{
			ModPacketClickButton packet = new ModPacketClickButton(inventorySlots.windowId, button.buttonNumber, mouseButton, 0);
			ModTubes.packetManager.sendPacketToServer(packet);
		}
		else
			super.mouseClicked(x, y, mouseButton);
	}
	
	@Override
	protected void handleMouseClick( Slot slot, int par2, int par3, int modifiers )
	{
		if(slot instanceof FakeSlot)
		{
			modifiers = 0;
			if(GuiScreen.isShiftKeyDown())
				modifiers |= 1;
			if(CommonHelper.isCtrlPressed())
				modifiers |= 2;
		}

		super.handleMouseClick(slot, par2, par3, modifiers);
	}
	
	
	public boolean drawSlotInventory( Slot slot )
	{
		if(slot instanceof FakeSlot)
		{
			FakeSlot fSlot = (FakeSlot)slot;

			IFilter filter = fSlot.getFilter();
			
			int x = slot.xDisplayPosition;
	        int y = slot.yDisplayPosition;

	        zLevel = 100.0F;
	        itemRender.zLevel = 100.0F;
	        
	        GL11.glEnable(GL11.GL_DEPTH_TEST);

	        if(filter == null)
	        {
	            IIcon icon = slot.getBackgroundIconIndex();

	            if (icon != null)
	            {
	                GL11.glDisable(GL11.GL_LIGHTING);
	                mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
	                drawTexturedModelRectFromIcon(x, y, icon, 16, 16);
	                GL11.glEnable(GL11.GL_LIGHTING);
	            }
	        }
	        else
	            filter.renderFilter(x, y);

	        itemRender.zLevel = 0.0F;
	        zLevel = 0.0F;
	        return true;
		}
		else 
			return false;
	}
	
	@Override
	protected void renderToolTip( ItemStack item, int x, int y )
	{
		Slot slot = null;
		
		int rx = x - guiLeft;
		int ry = y - guiTop;
		
		for(Slot s : (List<Slot>)inventorySlots.inventorySlots)
		{
			if(rx >= s.xDisplayPosition - 1 && rx <= s.xDisplayPosition + 16 && ry >= s.yDisplayPosition - 1 && ry <= s.yDisplayPosition + 16)
			{
				slot = s;
				break;
			}
		}
		
		if(slot instanceof FakeSlot)
		{
			List<String> tooltip = null;
			
			if(((FakeSlot)slot).getFilter() != null)
				tooltip = ((FakeSlot)slot).getFilter().getTooltip(null);
			
			tooltip = ((FakeSlot)slot).getTooltip(tooltip);
			
			if(tooltip != null)
				drawHoveringText(tooltip, x, y, fontRendererObj);
		}
		else
			super.renderToolTip(item, x, y);
	}
	
	// NOTE: This has been brought up here because TMI overrides GuiContainer, and does not provider this method causing a crash
	protected void drawHoveringText( List text, int x, int y, FontRenderer font )
	{
		if (!text.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = text.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = x + 12;
            int j1 = y - 12;
            int k1 = 8;

            if (text.size() > 1)
            {
                k1 += 2 + (text.size() - 1) * 10;
            }

            if (i1 + k > this.width)
            {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > this.height)
            {
                j1 = this.height - k1 - 6;
            }

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < text.size(); ++k2)
            {
                String s1 = (String)text.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }
            
            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float var1, int var2, int var3 )
	{
		for(GuiBaseButton button : ((ExtContainer)inventorySlots).buttons)
			button.drawButton(mc.renderEngine, fontRendererObj);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int x, int y )
	{
		GuiBaseButton button = getButtonAtPosition(x, y);
		if(button != null)
		{
			int oldWidth = width;
			width -= guiLeft;
			ArrayList<String> tooltip = new ArrayList<String>();
			button.getTooltip(tooltip);
			if(!tooltip.isEmpty())
			{
				schmoller.tubes.api.helpers.RenderHelper.wrapTooltip(tooltip);
				drawHoveringText(tooltip, x - guiLeft, y - guiTop, fontRendererObj);
			}
			width = oldWidth;
		}
		
		RenderHelper.enableGUIStandardItemLighting();

		super.drawGuiContainerForegroundLayer(x, y);
	}
}
