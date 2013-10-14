package schmoller.tubes.types;

import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.inventory.InventoryHelper;

public class ExtractionTube extends DirectionalBasicTube implements IRedstonePart
{
	private boolean mIsPowered;
	
	public ExtractionTube()
	{
		super("extraction");
		mIsPowered = false;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		if(con instanceof ExtractionTube)
			return false;
		
		return super.canConnectTo(con);
	}

	@Override
	public int getTickRate()
	{
		return 20;
	}
	
	@Override
	public void onTick()
	{
		if(mIsPowered)
			return;
		
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		
		ItemStack item = InventoryHelper.extractItem(world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ, dir.ordinal(), null);
		
		if(item != null)
			addItem(item, dir.ordinal() ^ 1);
	}
	
	private int getPower()
	{
		int current = 0;
		for(int side = 0; side < 6; ++side)
			current = Math.max(current, RedstoneInteractions.getPowerTo(world(), x(), y(), z(), side, 0x1f));
		
		return current;
	}
	
	@Override
	public void onWorldJoin()
	{
		mIsPowered = getPower() > 0;
	}
	
	@Override
	public void update()
	{
		mIsPowered = getPower() > 0;
		super.update();
	}
	
	@Override
	public boolean canConnectRedstone( int side ) { return true; }

	@Override
	public int strongPowerLevel( int side ) { return 0; }

	@Override
	public int weakPowerLevel( int side ) { return 0; }
}
