package schmoller.tubes.api.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.FluidStack;

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
	
	private void drawFluidSlot(FakeSlot slot)
	{
		int i = slot.xDisplayPosition;
        int j = slot.yDisplayPosition;
        FluidStack fluid = slot.getFluidStack();

        this.zLevel = 100.0F;
        itemRenderer.zLevel = 100.0F;

        if (fluid == null)
        {
            Icon icon = slot.getBackgroundIconIndex();

            if (icon != null)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                this.drawTexturedModelRectFromIcon(i, j, icon, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }
        else
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            
            Icon icon = fluid.getFluid().getIcon(fluid);
            mc.getTextureManager().bindTexture(mc.getTextureManager().getResourceLocation(fluid.getFluid().getSpriteNumber()));
            
            drawTexturedModelRectFromIcon(i, j, icon, 16, 16);
            
            // TODO: Draw amount
            
        }

        itemRenderer.zLevel = 0.0F;
        this.zLevel = 0.0F;
	}
	
	@Override
	protected void drawSlotInventory( Slot slot )
	{
		if(slot instanceof FakeSlot)
		{
			FakeSlot fSlot = (FakeSlot)slot;
			
			if(fSlot.getFluidStack() != null)
				drawFluidSlot(fSlot);
			else
				super.drawSlotInventory(slot);
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
			List<String> tooltip = ((FakeSlot)slot).getTooltip();
			
			if(tooltip != null)
			{
				drawHoveringText(tooltip, x, y, fontRenderer);
				return;
			}
		}
		super.drawItemStackTooltip(item, x, y);
	}
}
