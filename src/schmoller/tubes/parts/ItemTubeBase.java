package schmoller.tubes.parts;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import codechicken.core.vec.BlockCoord;
import codechicken.core.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTubeBase extends JItemMultiPart
{

	public ItemTubeBase( int id )
	{
		super(id);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabTransport);
		
		setUnlocalizedName("item.transport.tube");
	}

	
	@Override
	public TMultiPart newPart( ItemStack item, EntityPlayer player, World world, BlockCoord pos, int side, Vector3 hit )
	{
		if(item.getItemDamage() == 1)
			return MultiPartRegistry.createPart("schmoller_restriction_tube", false);
		else
			return MultiPartRegistry.createPart("schmoller_tube", false);
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	@SideOnly( Side.CLIENT )
	public void getSubItems( int id, CreativeTabs tab, List items )
	{
		items.add(new ItemStack(id, 0, 1));
		items.add(new ItemStack(id, 1, 1));
	}

	
}
