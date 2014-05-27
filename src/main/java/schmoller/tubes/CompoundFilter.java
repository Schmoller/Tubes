package schmoller.tubes;

import java.util.Collection;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.interfaces.IFilter;

public class CompoundFilter implements IFilter
{
	private IFilter[] mFilters;
	private boolean mInvert;
	
	public CompoundFilter()
	{
		mFilters = new IFilter[0];
		mInvert = false;
	}
	
	public CompoundFilter(IFilter[] filters, boolean invert)
	{
		mFilters = filters;
		mInvert = invert;
	}
	
	public CompoundFilter(Collection<IFilter> filters, boolean invert)
	{
		mFilters = filters.toArray(new IFilter[filters.size()]);
		mInvert = invert;
	}
	
	@Override
	public String getType()
	{
		return "compound";
	}

	@Override
	public Class<? extends Payload> getPayloadType()
	{
		return null;
	}

	@Override
	public boolean matches( Payload payload, SizeMode mode )
	{
		if(mInvert)
		{
			for(IFilter filter : mFilters)
			{
				if(filter.matches(payload, mode))
					return false;
			}
		}
		else
		{
			for(IFilter filter : mFilters)
			{
				if(!filter.matches(payload, mode))
					return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean matches( TubeItem item, SizeMode mode )
	{
		if(mInvert)
		{
			for(IFilter filter : mFilters)
			{
				if(filter.matches(item, mode))
					return false;
			}
		}
		else
		{
			for(IFilter filter : mFilters)
			{
				if(!filter.matches(item, mode))
					return false;
			}
		}
		
		return true;
	}

	@Override
	public void increase( boolean useMax, boolean shift )
	{
	}

	@Override
	public void decrease( boolean shift )
	{
	}

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
	public void write( NBTTagCompound tag )
	{
		NBTTagList list = new NBTTagList();
		for(IFilter filter : mFilters)
		{
			NBTTagCompound sub = new NBTTagCompound();
			FilterRegistry.getInstance().writeFilter(filter, sub);
			list.appendTag(sub);
		}
		
		tag.setTag("Filters", list);
		tag.setBoolean("Invert", mInvert);
	}

	@Override
	public void write( MCDataOutput output )
	{
		output.writeBoolean(mInvert);
		output.writeByte(mFilters.length);
		for(IFilter filter : mFilters)
			FilterRegistry.getInstance().writeFilter(filter, output);
	}

	@Override
	public IFilter copy()
	{
		IFilter[] filters = new IFilter[mFilters.length];
		for(int i = 0; i < filters.length; ++i)
			filters[i] = filters[i].copy();
		
		return new CompoundFilter(filters, mInvert);
	}

	@Override
	@SideOnly( Side.CLIENT )
	public List<String> getTooltip( List<String> current )
	{
		current.add("Compound Filter");
		return current;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void renderFilter( int x, int y )
	{
		// Filter not meant to be seen
	}
	
	public static CompoundFilter from(NBTTagCompound tag)
	{
		NBTTagList list = tag.getTagList("Filters", Constants.NBT.TAG_COMPOUND);
		IFilter[] filters = new IFilter[list.tagCount()];
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound sub = list.getCompoundTagAt(i);
			filters[i] = FilterRegistry.getInstance().readFilter(sub);
		}
		
		return new CompoundFilter(filters, tag.getBoolean("Invert"));
	}
	
	public static CompoundFilter from(MCDataInput input)
	{
		boolean invert = input.readBoolean();
		int count = input.readByte();
		
		IFilter[] filters = new IFilter[count];
		for(int i = 0; i < count; ++i)
			filters[i] = FilterRegistry.getInstance().readFilter(input);
		
		return new CompoundFilter(filters, invert);
	}

}
