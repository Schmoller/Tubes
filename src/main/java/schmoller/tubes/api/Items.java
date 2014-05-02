package schmoller.tubes.api;

import net.minecraft.item.Item;

public enum Items
{
	Tube("Tube"),
	PlasticDust("PlasticDust"),
	PlasticSheet("PlasticSheet"),
	BucketMilkCurd("MilkCurd"),
	BucketPlastic("BucketOfPlastic"),
	RedstoneCircuit("redstoneCircuit"),
	TubeCap("TubeCap"),
	FluidCircuit("FluidCircuit");
	
	private Item mItem = null;
	private String mConfigName;
	
	private Items(String configName)
	{
		mConfigName = configName;
	}
	
	public String getConfigName()
	{
		return mConfigName;
	}
	
	public Item getItem()
	{
		return mItem;
	}
	
	public void initialize(Item item)
	{
		if(mItem != null)
			throw new IllegalStateException("Item is already initialized");

		mItem = item;
	}
}
