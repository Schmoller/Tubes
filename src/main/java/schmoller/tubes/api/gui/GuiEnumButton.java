package schmoller.tubes.api.gui;

import java.util.EnumSet;
import java.util.List;

import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.interfaces.IPropertyHolder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.StatCollector;

public class GuiEnumButton<T extends Enum<T>> extends GuiBaseButton
{
	private final T[] mValueSet;
	
	private int mSrcX;
	private int mSrcY;
	private String mTransKey;
	
	public GuiEnumButton(IPropertyHolder holder, int property, Class<T> enumClass, int x, int y, int srcX, int srcY, String transKey)
	{
		super(holder, property, x, y);
		
		EnumSet<T> set = EnumSet.allOf(enumClass);
		T[] values = (T[])new Enum[set.size()];
		for(T e : set)
			values[e.ordinal()] = e;
		
		mValueSet = values;
		
		mSrcX = srcX;
		mSrcY = srcY;
		mTransKey = transKey;
	}
	
	@Override
	public int getValue()
	{
		T value = holder.getProperty(property);
		return value.ordinal();
	}

	@Override
	public void onClickLeft()
	{
		T value = holder.getProperty(property);
		int index = value.ordinal() + 1;
		if(index >= mValueSet.length)
			index = 0;
		holder.setProperty(property, mValueSet[index]);
	}

	@Override
	public void onClickRight()
	{
		T value = holder.getProperty(property);
		int index = value.ordinal() - 1;
		if(index < 0)
			index = mValueSet.length - 1;
		holder.setProperty(property, mValueSet[index]);
	}

	@Override
	public void onReset()
	{
		holder.setProperty(property, mValueSet[0]);
	}
	
	@Override
	public void setValue( int value )
	{
		if(value >= 0 && value < mValueSet.length)
			holder.setProperty(property, mValueSet[value]);
		else
			holder.setProperty(property, mValueSet[0]);
	}

	@Override
	public void drawButton( TextureManager renderEngine, FontRenderer fontRenderer )
	{
		T value = holder.getProperty(property);
		float srcY = (mSrcY + value.ordinal() * 14) / 256f;
		RenderHelper.renderRect(displayX, displayY, 14, 14, mSrcX / 256f, srcY, 14/256f, 14/256f);
	}
	
	@Override
	public void getTooltip( List<String> tooltip )
	{
		T value = holder.getProperty(property);
		tooltip.add(StatCollector.translateToLocal(String.format(mTransKey, value.name())));
	}

}
