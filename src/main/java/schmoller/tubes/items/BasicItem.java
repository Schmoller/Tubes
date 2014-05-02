package schmoller.tubes.items;

import schmoller.tubes.ModTubes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class BasicItem extends Item
{
	private String mIconName;
	public BasicItem()
	{
		setCreativeTab(ModTubes.creativeTab);
	}
	
	@Override
	public Item setUnlocalizedName( String name )
	{
		mIconName = "Tubes:" + name;
		return super.setUnlocalizedName(name);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void registerIcons( IIconRegister register )
	{
		itemIcon = register.registerIcon(mIconName);
	}
}
