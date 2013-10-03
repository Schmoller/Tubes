package schmoller.tubes;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class TubeItem implements Cloneable
{
	public static final int NORMAL = 0;
	public static final int NO_PATH = 1;
	
	public TubeItem(ItemStack item)
	{
		this.item = item;
	}
	
	public ItemStack item;
	public int direction = 0;
	public float progress = 0;
	public boolean updated = false;
	public int state = NORMAL;
	
	@Override
	public String toString()
	{
		return item.toString() + " " + ForgeDirection.getOrientation(direction).name() + " U:" + updated + " P:" + progress + " S:" + state;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("D", direction | (updated ? 128 : 0));
		tag.setFloat("P", progress);
		tag.setInteger("S", state);
		item.writeToNBT(tag);
	}
	
	public void write(MCDataOutput output)
	{
		output.writeByte(direction | (updated ? 128 : 0));
		output.writeFloat(progress);
		output.writeItemStack(item);
		output.writeByte(state);
	}
	
	public static TubeItem read(MCDataInput input)
	{
		int direction = input.readByte() & 0xFF;
		boolean updated = (direction & 128) != 0;
		direction -= (direction & 128);
		
		float progress = input.readFloat();
		TubeItem item = new TubeItem(input.readItemStack());
		item.direction = direction;
		item.updated = updated;
		item.progress = progress;
		item.state = input.readByte();
		
		return item;
	}
	public static TubeItem readFromNBT(NBTTagCompound tag)
	{
		ItemStack item = ItemStack.loadItemStackFromNBT(tag);
		TubeItem tItem = new TubeItem(item);
		
		tItem.direction = tag.getInteger("D") & 0xFF;
		tItem.updated = (tItem.direction & 128) != 0;
		tItem.direction -= (tItem.direction & 128);
		
		tItem.progress = tag.getFloat("P");
		tItem.state = tag.getInteger("S");
		
		return tItem;
	}
	
	public TubeItem clone()
	{
		TubeItem item = new TubeItem(this.item.copy());
		item.direction = direction;
		item.state = state;
		item.progress = progress;
		item.updated = updated;
		
		return item;
	}
}
