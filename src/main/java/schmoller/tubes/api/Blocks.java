package schmoller.tubes.api;

import net.minecraft.block.Block;

public enum Blocks
{
	BlockPlastic("PlasticBlock");
	
	private Block mBlock = null;
	private String mConfigName;
	
	private Blocks(String configName)
	{
		mConfigName = configName;
	}
	
	public String getConfigName()
	{
		return mConfigName;
	}
	
	public Block getBlock()
	{
		return mBlock;
	}
	
	public void initialize(Block block)
	{
		if(mBlock != null)
			throw new IllegalStateException("Block is already initialized");
		
		mBlock = block;
	}
}
