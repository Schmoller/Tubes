package schmoller.tubes.api.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

import schmoller.tubes.api.interfaces.IPropertyHolder;

public abstract class GuiBaseButton
{
	protected final IPropertyHolder holder;
	protected final int property;
	
	public final int x;
	public final int y;
	
	public int displayX;
	public int displayY;
	
	public int buttonNumber;
	
	public GuiBaseButton(IPropertyHolder holder, int property, int x, int y)
	{
		this.holder = holder;
		this.property = property;
		
		this.x = x;
		this.y = y;
	}
	
	public void getTooltip(List<String> tooltip)
	{
	}
	
	public abstract int getValue();
	public abstract void setValue(int value);
	
	public abstract void onClickLeft();
	public abstract void onClickRight();
	public abstract void onReset();
	
	public abstract void drawButton(TextureManager renderEngine, FontRenderer fontRenderer);
}
