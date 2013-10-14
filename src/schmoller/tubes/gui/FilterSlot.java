package schmoller.tubes.gui;

import schmoller.tubes.types.FilterTube;
import net.minecraft.item.ItemStack;

public class FilterSlot extends FakeSlot
{
	private FilterTube mTube;
	private int mIndex;
	
	public FilterSlot(FilterTube tube, int index, int x, int y)
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
