package schmoller.tubes;

import schmoller.tubes.api.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TubeCreativeTab extends CreativeTabs
{
	public TubeCreativeTab()
	{
		super("tubes");
	}
	
	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(Items.Tube.getItem());
	}
}
