package schmoller.tubes.api;

import net.minecraft.block.Block;

public enum Blocks
{
	BlockPlastic(1027, "PlasticBlock");
	
	private int mBlockId;
	private Block mBlock = null;
	private String mConfigName;
	
	private Blocks(int defaultId, String configName)
	{
		mBlockId = defaultId;
		mConfigName = configName;
	}
	
	public String getConfigName()
	{
		return mConfigName;
	}
	
	public int getBlockID()
	{
		return mBlockId;
	}
	
	public Block getBlock()
	{
		return mBlock;
	}
	
	public void initialize(int blockId, Block block)
	{
		if(mBlock != null)
			throw new IllegalStateException("Block is already initialized");
		
		mBlockId = blockId;
		mBlock = block;
	}
}
