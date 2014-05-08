package schmoller.tubes.api;

import java.util.LinkedList;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.Utilities;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IFilterFactory;

public class FilterRegistry
{
	private static FilterRegistry instance;
	
	public static FilterRegistry getInstance()
	{
		if(instance == null)
			instance = new FilterRegistry();
		
		return instance;
	}
	
	
	private LinkedList<IFilterFactory> mFactories = new LinkedList<IFilterFactory>();
	
	
	public static void registerFilterFactory(IFilterFactory factory)
	{
		getInstance().mFactories.add(factory);
	}
	
	
	public IFilter createFilter( ItemStack heldItem, IFilter existing, int button, boolean shift, boolean ctrl, boolean mustHavePayload )
	{
		for(IFilterFactory factory : mFactories)
		{
			IFilter filter = factory.getFilterFrom(heldItem, existing, button, shift, ctrl, mustHavePayload);
			if(filter != null)
				return filter;
		}
		
		return null;
	}
	
	
	
	public void writeFilter(IFilter filter, NBTTagCompound tag)
	{
		tag.setString("FilterType", filter.getType());
		filter.write(tag);
	}
	
	public void writeFilter(IFilter filter, MCDataOutput output)
	{
		Utilities.writeString(output, filter.getType());
		filter.write(output);
	}
	
	public IFilter readFilter(NBTTagCompound tag)
	{
		String type = tag.getString("FilterType");
		
		for(IFilterFactory factory : mFactories)
		{
			IFilter filter = factory.loadFilter(type, tag);
			if(filter != null)
				return filter;
		}
		
		return null;
	}
	
	public IFilter readFilter(MCDataInput input)
	{
		String type = Utilities.readString(input);
		
		for(IFilterFactory factory : mFactories)
		{
			IFilter filter = factory.loadFilter(type, input);
			if(filter != null)
				return filter;
		}
		
		throw new IllegalArgumentException("Unknown filter type '" + type + "'");
	}
}
