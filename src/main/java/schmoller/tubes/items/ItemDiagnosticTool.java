package schmoller.tubes.items;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDiagnosticTool extends Item
{
	public ItemDiagnosticTool()
	{
		iconString = "tubes:diagnosticTool";
		maxStackSize = 1;
	}
	
	@Override
	public boolean onItemUseFirst( ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		ITubeOverflowDestination dest = CommonHelper.getInterface(world, x, y, z, ITubeOverflowDestination.class);
		
		//return (dest != null);
		return false;
	}
	
	@Override
	public boolean onItemUse( ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		ITubeOverflowDestination dest = CommonHelper.getInterface(world, x, y, z, ITubeOverflowDestination.class);
		
		if(dest != null)
		{
			if (!world.isRemote)
				player.openGui(ModTubes.instance, ModTubes.GUI_OVERFLOW, world, x, y, z);
			
			return true;
		}
		
		return false;
	}
}
