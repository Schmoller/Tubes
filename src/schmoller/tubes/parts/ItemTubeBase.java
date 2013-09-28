package schmoller.tubes.parts;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import codechicken.core.vec.BlockCoord;
import codechicken.core.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;

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
		return MultiPartRegistry.createPart("schmoller_tube", false);
	}

	
}
