package schmoller.tubes.api;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

/**
 * This class is used by providers to enable non-tileentity block to have inventories or fluid handlers or whatever
 */
public class BlockInstance
{
	public IBlockAccess world;
	public int x;
	public int y;
	public int z;
	
	public BlockInstance(IBlockAccess world, int x, int y, int z)
	{
		this.world= world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Block getBlock()
	{
		return Block.blocksList[world.getBlockId(x, y, z)];
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof BlockInstance))
			return false;
		
		BlockInstance other = (BlockInstance) obj;
		
		return world.equals(other.world) && x == other.x && y == other.y && z == other.z;
	}

	@Override
	public int hashCode()
	{
		return world.hashCode() ^ (x << 16) ^ (y << 8) ^ z;
	}
	
}
