package schmoller.tubes.api.gui;

import java.util.List;

import schmoller.tubes.api.interfaces.IPropertyHolder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.StatCollector;

public class GuiCounterButton extends GuiBaseButton
{
	private int mMin;
	private int mMax;
	private String mName;
	
	public GuiCounterButton(IPropertyHolder holder, int prop, int min, int max, int x, int y, String name)
	{
		super(holder, prop, x, y);
		mMin = min;
		mMax = max;
		mName = name;
	}
	
	@Override
	public int getValue()
	{
		return holder.getProperty(property);
	}

	@Override
	public void setValue( int value )
	{
		if(value < mMin)
			value = mMin;
		if(value >= mMax)
			value = mMax;
		holder.setProperty(property, value);
	}

	@Override
	public void onClickLeft()
	{
		int value = holder.getProperty(property);
		++value;
		if(value >= mMax)
			value = mMin;
		holder.setProperty(property, value);
	}

	@Override
	public void onClickRight()
	{
		int value = holder.getProperty(property);
		--value;
		if(value < mMin)
			value = mMax - 1;
		holder.setProperty(property, value);
	}

	@Override
	public void onReset()
	{
		holder.setProperty(property, 0);
	}

	@Override
	public void drawButton( TextureManager renderEngine, FontRenderer fontRenderer )
	{
		String text = String.valueOf(getValue());
		int width = fontRenderer.getStringWidth(text) / 2;
		fontRenderer.drawStringWithShadow(text, displayX + 7 - width, displayY, 0xFFFFFFFF);
	}

	@Override
	public void getTooltip( List<String> tooltip )
	{
		tooltip.add(StatCollector.translateToLocal(mName));
	}
}
