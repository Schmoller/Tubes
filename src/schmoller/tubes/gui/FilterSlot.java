package schmoller.tubes.gui;

import schmoller.tubes.logic.FilterTubeLogic;
import net.minecraft.item.ItemStack;

public class FilterSlot extends FakeSlot
{
	private FilterTubeLogic mTube;
	private int mIndex;
	
	public FilterSlot(FilterTubeLogic tube, int index, int x, int y)
	{
		super(tube.getFilter(index), x, y);
		mTube = tube;
		mIndex = index;
	}
	
	@Override
	protected ItemStack getValue()
	{
		return mTube.getFilter(mIndex);
	}
	
	@Override
	protected void setValue( ItemStack item )
	{
		mTube.setFilter(mIndex, item);
	}
}
