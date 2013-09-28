package schmoller.tubes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class TubeItem
{
	public TubeItem(ItemStack item)
	{
		this.item = item;
	}
	
	public ItemStack item;
	public int direction = 0;
	public float progress = 0;
	public boolean updated = false;
	
	@Override
	public String toString()
	{
		return item.toString() + " " + ForgeDirection.getOrientation(direction).name() + " U:" + updated + " P:" + progress;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("D", direction | (updated ? 128 : 0));
		tag.setFloat("P", progress);
		item.writeToNBT(tag);
	}
	
	public static TubeItem readFromNBT(NBTTagCompound tag)
	{
		ItemStack item = ItemStack.loadItemStackFromNBT(tag);
		TubeItem tItem = new TubeItem(item);
		
		tItem.direction = tag.getInteger("D") & 0xFF;
		tItem.updated = (tItem.direction & 128) != 0;
		tItem.direction -= (tItem.direction & 128);
		
		tItem.progress = tag.getFloat("P");
		
		return tItem;
	}
}
