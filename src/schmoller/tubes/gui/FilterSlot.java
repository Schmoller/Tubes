package schmoller.tubes.gui;

import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.types.FilterTube;
import net.minecraft.item.ItemStack;

public class FilterSlot extends FakeSlot
{
	private FilterTube mTube;
	private int mIndex;
	
	public FilterSlot(FilterTube tube, int index, int x, int y)
	{
		super(new ItemPayload(tube.getFilter(index)), x, y);
		mTube = tube;
		mIndex = index;
	}
	
	@Override
	protected Payload getValue()
	{
		return new ItemPayload(mTube.getFilter(mIndex));
	}
	
	@Override
	protected void setValue( Payload item )
	{
		mTube.setFilter(mIndex, (ItemStack)item.get());
	}
}
