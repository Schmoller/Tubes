package schmoller.tubes;

import java.util.Arrays;
import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.interfaces.IFilter;

public class AnyFilter implements IFilter
{
	public static ResourceLocation texture = new ResourceLocation("tubes", "textures/gui/anyFilter.png");
	private int mMax;
	private int mValue;
	
	public AnyFilter(int value) { this(value, 64); }
	
	public AnyFilter(int value, int max)
	{
		assert(max > 0);
		mValue = value;
		mMax = max;
	}
	
	@Override
	public String getType()
	{
		return "any";
	}
	
	@Override
	public boolean matches( Payload payload, SizeMode mode )
	{
		if(mValue > payload.maxSize() && payload.size() == payload.maxSize())
			return true;
		
		switch(mode)
		{
		case Max:
			return true;
		case Exact:
			return payload.size() == mValue;
		case GreaterEqual:
			return payload.size() >= mValue;
		case LessEqual:
			return payload.size() <= mValue;
		}
		
		return true;
	}
	@Override
	public boolean matches( TubeItem item, SizeMode mode )
	{
		return matches(item.item, mode);
	}
	
	@Override
	public void increase( boolean useMax, boolean shift )
	{
		mValue += (shift ? 10 : 1);
		if(mValue > mMax && useMax)
			mValue = mMax;
	}

	@Override
	public void decrease( boolean shift )
	{
		mValue -= (shift ? 10 : 1);
		
		if(mValue < 0)
			mValue = 0;
	}

	@Override
	public int getMax()
	{
		return mMax;
	}
	
	@Override
	public int size()
	{
		return mValue;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFilter( int x, int y )
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		RenderHelper.renderRect(x, y, 16, 16, 0, 0, 16, 16);
	}

	@Override
	public void write( NBTTagCompound tag )
	{
		tag.setInteger("val", mValue);
		tag.setInteger("max", mMax);
	}
	
	@Override
	public void write( MCDataOutput output )
	{
		output.writeShort(mValue);
		output.writeShort(mMax);
	}
	
	public static AnyFilter from(NBTTagCompound tag)
	{
		return new AnyFilter(tag.getInteger("val"), tag.getInteger("max"));
	}
	
	public static AnyFilter from(MCDataInput input)
	{
		return new AnyFilter(input.readShort(), input.readShort());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip( List<String> current )
	{
		return Arrays.asList("Any");
	}

}
