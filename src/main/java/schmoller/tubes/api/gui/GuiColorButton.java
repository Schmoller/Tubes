package schmoller.tubes.api.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.interfaces.IPropertyHolder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.StatCollector;

public class GuiColorButton extends GuiBaseButton
{
	public GuiColorButton(IPropertyHolder holder, int property, int x, int y)
	{
		super(holder, property, x, y);
	}
	
	@Override
	public int getValue()
	{
		return holder.getProperty(property);
	}

	@Override
	public void onClickLeft()
	{
		int color = holder.getProperty(property);
		++color;
		if(color >= 16)
			color = -1;
		
		holder.setProperty(property, color);
	}

	@Override
	public void onClickRight()
	{
		int color = holder.getProperty(property);
		--color;
		if(color < -1)
			color = 15;
		
		holder.setProperty(property, color);
	}

	@Override
	public void onReset()
	{
		holder.setProperty(property, -1);
	}
	
	@Override
	public void setValue( int value )
	{
		if(value >= -1 && value < 16)
			holder.setProperty(property, value);
		else
			holder.setProperty(property, -1);
	}

	@Override
	public void drawButton( TextureManager renderEngine, FontRenderer fontRenderer )
	{
		int color = holder.getProperty(property);
		if(color != -1)
		{
			RenderHelper.renderPlainRect(displayX + 3, displayY + 3, displayX + 11, displayY + 11, CommonHelper.getDyeColor(color));
			GL11.glColor4f(1f, 1f, 1f, 1f);
		}
	}

	@Override
	public void getTooltip( List<String> tooltip )
	{
		int color = holder.getProperty(property);
		
		String text = StatCollector.translateToLocal("gui.colors.none");
		if(color != -1)
			text = CommonHelper.getDyeName(color);
		
		tooltip.add(text);
	}
}
