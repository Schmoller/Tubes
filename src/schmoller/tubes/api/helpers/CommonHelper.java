package schmoller.tubes.api.helpers;

import schmoller.tubes.Position;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;

public class CommonHelper
{
	private static final String[] dyes = { "dyeWhite", "dyeOrange", "dyeMagenta", "dyeLightBlue", "dyeYellow", "dyeLime", "dyePink", "dyeGray", "dyeLightGray", "dyeCyan", "dyePurple", "dyeBlue", "dyeBrown", "dyeGreen", "dyeRed", "dyeBlack" };
	private static final String[] colourNames = {"White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
	private static int[] dyeIds;
	
	public static TileEntity getTileEntity(IBlockAccess world, Position pos)
	{
		return world.getBlockTileEntity(pos.x, pos.y, pos.z);
	}
	
	public static <T> T getTileEntity(IBlockAccess world, Position pos, Class<? extends T> tileClass)
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
	
	public static int getDyeIndex(ItemStack dye)
	{
		if(dyeIds == null)
		{
			dyeIds = new int[16];
			for(int i = 0; i < 16; ++i)
				dyeIds[i] = OreDictionary.getOreID(dyes[i]);
		}
		
		int id = OreDictionary.getOreID(dye);
		if(id == -1)
			return -1;
		
		for(int i = 0; i < 16; ++i)
		{
			if(dyeIds[i] == id)
				return i;
		}
		
		return -1;
	}
	
	public static int getDyeColor(int index)
	{
		float[] rgb = EntitySheep.fleeceColorTable[index];
		
		return (255 << 24) | (int)(rgb[0] * 255) << 16 | (int)(rgb[1] * 255) << 8 | (int)(rgb[2] * 255);
	}
	
	public static String getDyeName(int index)
	{
		return colourNames[index];
	}
}
