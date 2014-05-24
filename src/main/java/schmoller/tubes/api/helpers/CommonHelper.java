package schmoller.tubes.api.helpers;

import org.lwjgl.input.Keyboard;

import schmoller.tubes.api.Position;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;

public class CommonHelper
{
	private static final String[] dyes = { "dyeWhite", "dyeOrange", "dyeMagenta", "dyeLightBlue", "dyeYellow", "dyeLime", "dyePink", "dyeGray", "dyeLightGray", "dyeCyan", "dyePurple", "dyeBlue", "dyeBrown", "dyeGreen", "dyeRed", "dyeBlack" };
	private static final String[] colourNames = {"item.fireworksCharge.white", "item.fireworksCharge.orange", "item.fireworksCharge.magenta", "item.fireworksCharge.lightBlue", "item.fireworksCharge.yellow", "item.fireworksCharge.lime", "item.fireworksCharge.pink", "item.fireworksCharge.gray", "item.fireworksCharge.silver", "item.fireworksCharge.cyan", "item.fireworksCharge.purple", "item.fireworksCharge.blue", "item.fireworksCharge.brown", "item.fireworksCharge.green", "item.fireworksCharge.red", "item.fireworksCharge.black"};
	private static int[] dyeIds;
	
	public static <T> T getInterface(IBlockAccess world, Position pos, Class<? extends T> interfaceClass)
	{
		T iface = getMultiPart(world, pos.x, pos.y, pos.z, interfaceClass);
		
		if(iface != null)
			return iface;
		
		iface = getTileEntity(world, pos, interfaceClass);
		
		return iface;
	}
	
	public static <T> T getInterface(IBlockAccess world, int x, int y, int z, Class<? extends T> interfaceClass)
	{
		T iface = getMultiPart(world, x, y, z, interfaceClass);
		
		if(iface != null)
			return iface;
		
		iface = getTileEntity(world, x, y, z, interfaceClass);
		
		return iface;
	}
	
	public static TileEntity getTileEntity(IBlockAccess world, Position pos)
	{
		return world.getTileEntity(pos.x, pos.y, pos.z);
	}
	
	public static <T> T getTileEntity(IBlockAccess world, Position pos, Class<? extends T> tileClass)
	{
		return getTileEntity(world, pos.x, pos.y, pos.z, tileClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getTileEntity(IBlockAccess world, int x, int y, int z, Class<? extends T> tileClass)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
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
		return StatCollector.translateToLocal(colourNames[index]);
	}
	
	public static boolean isCtrlPressed()
	{
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}
}
