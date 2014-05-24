package schmoller.tubes.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.interfaces.IFilter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

public class OverflowGui extends GuiContainer
{
	private List<TubeItem> mStuck = null;
	
    private float mScroll;
    private boolean mIsScrolling;
    private boolean mWasClicking;
	
	public OverflowGui(OverflowBuffer buffer)
	{
		super(new OverflowContainer(buffer));
		xSize = 165;
		ySize = 122;
	}
	
	private List<TubeItem> extract(ItemStack item)
	{
		NBTTagCompound root = item.getTagCompound();
		NBTTagList list = root.getTagList("Stuck", Constants.NBT.TAG_COMPOUND);
		
		ArrayList<TubeItem> items = new ArrayList<TubeItem>();
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			items.add(TubeItem.readFromNBT(tag));
		}
		
		return items;
	}
	
	private List<TubeItem> getStuck()
	{
		ItemStack item = inventorySlots.getSlot(0).getStack();
		
		if(item == null)
			return null;
		return extract(item);
	}
	
	@Override
	public void updateScreen()
	{
		if(mStuck == null)
			mStuck = getStuck();

		super.updateScreen();
	}
	
	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();
		int scroll = Mouse.getEventDWheel();
		
		if(mStuck == null)
			return;
		
		if(scroll != 0 && mStuck.size() > 5)
		{
			int scrollSize = (mStuck.size() - 5);
			if(scroll < 0)
				scroll = -1;
			else
				scroll = 1;
			
			mScroll -= scroll / (float)scrollSize;
			
			if(mScroll < 0)
				mScroll = 0;
			else if(mScroll > 1)
				mScroll = 1;
		}
	}
	
	@Override
	public void drawScreen( int mouseX, int mouseY, float par3 )
	{
		if(mStuck.size() > 5)
		{
			boolean mouseClick = Mouse.isButtonDown(0);
			int left = guiLeft + 145;
			int top = guiTop + 14;
			int right = left + 12;
			int bottom = top + 100;
			
			if(!mWasClicking && mouseClick && mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom)
				mIsScrolling = true;
			
			if(!mouseClick)
				mIsScrolling = false;
			
			mWasClicking = mouseClick;
			
			if(mIsScrolling)
			{
				mScroll = ((mouseY - top) - 7.5f) / ((bottom - top) - 15f);
				if(mScroll < 0)
					mScroll = 0;
				else if(mScroll > 1)
					mScroll = 1;
			}
		}
		
		super.drawScreen(mouseX, mouseY, par3);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float var1, int var2, int var3 )
	{
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		mc.renderEngine.bindTexture(ModTubes.overflowGui);
		
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		RenderHelper.enableGUIStandardItemLighting();
		
		if(mStuck != null)
		{
			int start = (int)(mScroll * (mStuck.size() - 5));
			int scrollPos = 14;
			scrollPos += mScroll * (100 - 15);
			
			if(mStuck.size() > 5)
				drawTexturedModalRect(x + 145, y + scrollPos, xSize, 0, 12, 15);
			else
				drawTexturedModalRect(x + 145, y + scrollPos, xSize + 12, 0, 12, 15);
			
			int count = 0;
			int yy = 14;
			for(int i = start; i < mStuck.size() && count < 5; ++i)
			{
				TubeItem item = mStuck.get(i);
				IFilter filter = FilterRegistry.getInstance().createFilter(item.item);
				
				GL11.glDisable(GL11.GL_LIGHTING);
				mc.renderEngine.bindTexture(ModTubes.overflowGui);
				drawTexturedModalRect(x + 8, y + yy, 0, ySize, 134, 20);
				
				List<String> tooltip = filter.getTooltip(new ArrayList<String>());
				String name = (tooltip.isEmpty() ? "" : tooltip.get(0));
				int width = fontRendererObj.getStringWidth(name);
				if(width > 100)
				{
					while(width > 100)
					{
						name = name.substring(0, name.length()-1);
						width = fontRendererObj.getStringWidth(name);
					}
					name += "...";
				}
				
				fontRendererObj.drawStringWithShadow(name, x + 32, y + yy + 6, 0xffffff);
				
				GL11.glEnable(GL11.GL_LIGHTING);
				
		        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				
				filter.renderFilter(x + 10, y + yy + 2);
				
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
				
				yy += 20;
				++count;
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
	{
		String s = StatCollector.translateToLocal("gui.overflow.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 4, 0x404040);
		
		mouseX -= guiLeft;
		mouseY -= guiTop;
		
		if(mStuck != null && mouseX >= 8 && mouseY >= 14 && mouseX < 145 && mouseY < 108)
		{
			int index = (mouseY - 14) / 20;
			int start = (int)(mScroll * (mStuck.size() - 5));
			
			index += start;
			
			if(index >= 0 && index < mStuck.size())
			{
				TubeItem item = mStuck.get(index);
				IFilter filter = FilterRegistry.getInstance().createFilter(item.item);
				List<String> tooltip = filter.getTooltip(new ArrayList<String>());
				
				drawHoveringText(tooltip, mouseX, mouseY, fontRendererObj);
				RenderHelper.enableGUIStandardItemLighting();
			}
		}
	}
}
