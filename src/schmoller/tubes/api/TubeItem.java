package schmoller.tubes.api;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class TubeItem implements Cloneable
{
	public static final int NORMAL = 0;
	public static final int IMPORT = 1;
	public static final int BLOCKED = 2;
	
	public TubeItem(Payload item)
	{
		this.item = item;
	}
	
	public Payload item;
	public int direction = 0;
	public float progress = 0;
	public float lastProgress = 0;
	public boolean updated = false;
	public int state = NORMAL;
	public int colour = -1;
	
	
	public void setProgress(float progress)
	{
		this.progress = lastProgress = progress;
	}
	
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
		tag.setShort("C", (short)colour);
		item.write(tag);
	}
	
	public void write(MCDataOutput output)
	{
		output.writeByte(direction | (updated ? 128 : 0));
		output.writeFloat(progress);
		item.write(output);
		output.writeByte(state);
		output.writeShort(colour);
	}
	
	public static TubeItem read(MCDataInput input)
	{
		int direction = input.readByte() & 0xFF;
		boolean updated = (direction & 128) != 0;
		direction -= (direction & 128);
		
		float progress = input.readFloat();
		TubeItem item = new TubeItem(Payload.load(input));
		item.direction = direction;
		item.updated = updated;
		item.progress = progress;
		item.state = input.readByte();
		item.colour = input.readShort();
		item.lastProgress = progress;
		
		return item;
	}
	public static TubeItem readFromNBT(NBTTagCompound tag)
	{
		TubeItem tItem = new TubeItem(Payload.load(tag));
		
		tItem.direction = tag.getInteger("D") & 0xFF;
		tItem.updated = (tItem.direction & 128) != 0;
		tItem.direction -= (tItem.direction & 128);
		
		tItem.lastProgress = tItem.progress = tag.getFloat("P");
		tItem.state = tag.getInteger("S");
		
		tItem.colour = tag.getShort("C");
		
		return tItem;
	}
	
	public TubeItem clone()
	{
		TubeItem item = new TubeItem(this.item.copy());
		item.direction = direction;
		item.state = state;
		item.progress = progress;
		item.lastProgress = lastProgress;
		item.updated = updated;
		item.colour = colour;
		
		return item;
	}
}
