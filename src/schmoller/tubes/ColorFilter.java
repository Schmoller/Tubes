package schmoller.tubes;

import java.util.Arrays;
import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.interfaces.IFilter;

public class ColorFilter implements IFilter
{
	private int mColor;
	
	public ColorFilter(int color)
	{
		mColor = color;
	}
	
	@Override
	public String getType()
	{
		return "color";
	}
	
	@Override
	public boolean matches( Payload payload, SizeMode mode )
	{
		return true;
	}

	@Override
	public boolean matches( TubeItem item, SizeMode mode )
	{
		return mColor == item.colour;
	}

	@Override
	public void increase( boolean useMax, boolean shift ) {}

	@Override
	public void decrease( boolean shift ) {}

	@Override
	public int getMax()
	{
		return 1;
	}

	@Override
	public int size()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFilter( int x, int y )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void write( NBTTagCompound tag )
	{
		tag.setInteger("Color", mColor);
	}
	
	@Override
	public void write( MCDataOutput output )
	{
		output.writeShort(mColor);
	}

	public static ColorFilter from(NBTTagCompound tag)
	{
		return new ColorFilter(tag.getInteger("Color"));
	}
	
	public static ColorFilter from(MCDataInput input)
	{
		return new ColorFilter(input.readShort());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip( List<String> current )
	{
		return Arrays.asList((mColor == -1 ? "Any Color" : CommonHelper.getDyeName(mColor)));
	}
}
