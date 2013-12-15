package schmoller.tubes.api;

import net.minecraft.item.Item;

public enum Items
{
	Tube(5000, "Tube"),
	PlasticDust(5001, "PlasticDust"),
	PlasticSheet(5002, "PlasticSheet"),
	BucketMilkCurd(5003, "MilkCurd"),
	BucketPlastic(5004, "BucketOfPlastic"),
	RedstoneCircuit(5005, "redstoneCircuit"),
	TubeCap(5006, "TubeCap"),
	FluidCircuit(5007, "FluidCircuit");
	
	private int mItemId;
	private Item mItem = null;
	private String mConfigName;
	
	private Items(int defaultId, String configName)
	{
		mItemId = defaultId;
		mConfigName = configName;
	}
	
	public String getConfigName()
	{
		return mConfigName;
	}
	
	public int getItemID()
	{
		return mItemId;
	}
	
	public Item getItem()
	{
		return mItem;
	}
	
	public void initialize(int itemId, Item item)
	{
		if(mItem != null)
			throw new IllegalStateException("Item is already initialized");
		
		mItemId = itemId;
		mItem = item;
	}
}
