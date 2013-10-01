package schmoller.tubes;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;

public class CommonHelper
{
	public static TileEntity getTileEntity(IBlockAccess world, ChunkPosition pos)
	{
		return world.getBlockTileEntity(pos.x, pos.y, pos.z);
	}
	
	public static <T> T getTileEntity(IBlockAccess world, ChunkPosition pos, Class<? extends T> tileClass)
	{
		return getTileEntity(world, pos.x, pos.y, pos.z, tileClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getTileEntity(IBlockAccess world, int x, int y, int z, Class<? extends T> tileClass)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tileClass.isInstance(tile))
			return (T)tile;
		
		return null;
	}
	
	@SuppressWarnings( "unchecked" )
	public static <T> T getMultiPart(IBlockAccess world, int x, int y, int z, Class<? extends T> partClass)
	{
		TileMultipart tile = getTileEntity(world, x, y, z, TileMultipart.class);
		
		if(tile == null)
			return null;
		
		TMultiPart part = tile.partMap(6);
		if(partClass.isInstance(part))
			return (T)part;
		
		return null;
	}
}
