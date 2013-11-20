package schmoller.tubes.api.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
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
			
			if(fSlot.getStack() == null && fSlot.getFluidStack() != null)
				drawFluidSlot(fSlot);
			else
				super.drawSlotInventory(slot);
		}
		else 
			super.drawSlotInventory(slot);
	}
}
