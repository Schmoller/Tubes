package schmoller.tubes.logic;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.IDirectionalTube;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.inventory.InventoryHelper;

public class ExtractionTubeLogic extends TubeLogic
{
	private ITube mTube;
	public ExtractionTubeLogic(ITube tube)
	{
		mTube = tube;
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return true;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		if(con instanceof ITube)
			return !(((ITube)con).getLogic() instanceof ExtractionTubeLogic);

		return true;
	}
	
	@Override
	public int getConnectableMask()
	{
		int dir = ((IDirectionalTube)mTube).getFacing();
		
		return 63 - (63 & (1 << dir));
	}
	
	@Override
	public int getTickRate()
	{
		return 20;
	}
	
	@Override
	public void onTick()
	{
		ForgeDirection dir = ForgeDirection.getOrientation(((IDirectionalTube)mTube).getFacing());
		
		ItemStack item = InventoryHelper.extractItem(mTube.world(), mTube.x() + dir.offsetX, mTube.y() + dir.offsetY, mTube.z() + dir.offsetZ, dir.ordinal(), null);
		
		if(item != null)
			mTube.addItem(item, dir.ordinal() ^ 1);
	}
}
