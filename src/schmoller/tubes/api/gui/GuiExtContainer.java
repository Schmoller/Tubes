package schmoller.tubes.api.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import schmoller.tubes.api.interfaces.IFilter;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

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
	protected void drawSlotInventory( Slot slot )
	{
		if(slot instanceof FakeSlot)
		{
			FakeSlot fSlot = (FakeSlot)slot;

			IFilter filter = fSlot.getFilter();
			
			int x = slot.xDisplayPosition;
	        int y = slot.yDisplayPosition;

	        zLevel = 100.0F;
	        itemRenderer.zLevel = 100.0F;

	        if(filter == null)
	        {
	            Icon icon = slot.getBackgroundIconIndex();

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

	        itemRenderer.zLevel = 0.0F;
	        zLevel = 0.0F;
		}
		else 
			super.drawSlotInventory(slot);
	}
	
	@Override
	protected void drawItemStackTooltip( ItemStack item, int x, int y )
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
				drawHoveringText(tooltip, x, y, fontRenderer);
		}
		else
			super.drawItemStackTooltip(item, x, y);
	}
}
