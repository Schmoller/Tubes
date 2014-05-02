package schmoller.tubes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import schmoller.tubes.api.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() 
	{
		return Items.Tube.getItem();
	}
}
