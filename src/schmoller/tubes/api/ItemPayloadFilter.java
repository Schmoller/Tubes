package schmoller.tubes.api;

import net.minecraft.item.ItemStack;
import schmoller.tubes.api.interfaces.IPayloadFilter;

public class ItemPayloadFilter implements IPayloadFilter
{
	public ItemStack item;
	
	public ItemPayloadFilter(ItemStack item)
	{
		this.item = item;
	}
	
	@Override
	public int compareTo( Payload o )
	{
		return 0;
	}

	@Override
	public boolean matches( Payload payload )
	{
		if(!(payload instanceof ItemPayload))
			return false;
		
		
		// TODO Auto-generated method stub
		return false;
	}

}
