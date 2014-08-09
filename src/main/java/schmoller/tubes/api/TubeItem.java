package schmoller.tubes.api;

import schmoller.tubes.api.interfaces.IRoutingGoal;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class TubeItem implements Cloneable
{
	public TubeItem(Payload item)
	{
		this.item = item;
	}
	
	public Payload item;
	public int direction = 0;
	public int lastDirection = 0;
	public float progress = 0;
	public float lastProgress = 0;
	public boolean updated = false;
	public IRoutingGoal goal = TubesAPI.goalOutput;
	public int colour = -1;
	
	public int speed = 1;
	
	// Used to ensure items only get ticked once per tick when passing from one tube to another
	public int tickNo = 0;
	
	public void setProgress(float progress)
	{
		this.progress = lastProgress = progress;
	}
	
	@Override
	public String toString()
	{
		return item.toString() + " " + ForgeDirection.getOrientation(direction).name() + " U:" + updated + " P:" + progress + " G:" + goal;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("D", direction | (updated ? 128 : 0));
		tag.setFloat("P", progress);
		tag.setString("G", GoalRegistry.getInstance().getName(goal));
		tag.setShort("C", (short)colour);
		item.write(tag);
	}
	
	public void write(MCDataOutput output)
	{
		output.writeByte(direction | (updated ? 128 : 0));
		output.writeFloat(progress);
		item.write(output);
		output.writeByte(GoalRegistry.getInstance().getId(goal));
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
		item.goal = GoalRegistry.getInstance().get(input.readByte());
		item.colour = input.readShort();
		item.lastProgress = progress;
		item.lastDirection = direction;
		
		return item;
	}
	public static TubeItem readFromNBT(NBTTagCompound tag)
	{
		Payload payload = null;
		
		if(tag.hasKey("Type"))
			payload = Payload.load(tag);
		else
			payload = new ItemPayload(ItemStack.loadItemStackFromNBT(tag));
		
		TubeItem tItem = new TubeItem(payload);
		
		tItem.direction = tag.getInteger("D") & 0xFF;
		tItem.updated = (tItem.direction & 128) != 0;
		tItem.direction -= (tItem.direction & 128);
		
		tItem.lastProgress = tItem.progress = tag.getFloat("P");
		
		if(tag.hasKey("S", Constants.NBT.TAG_INT))
		{
			switch(tag.getInteger("S"))
			{
			default:
			case 0:
				tItem.goal = TubesAPI.goalOutput;
				break;
			case 1:
				tItem.goal = TubesAPI.goalInput;
				break;
			case 2:
				tItem.goal = TubesAPI.goalOverflow;
				break;
			}
		}
		else
			tItem.goal = GoalRegistry.getInstance().get(tag.getString("G"));
		
		tItem.colour = tag.getShort("C");
		
		return tItem;
	}
	
	public TubeItem clone()
	{
		TubeItem item = new TubeItem(this.item.copy());
		item.direction = direction;
		item.lastDirection = lastDirection;
		item.goal = goal;
		item.progress = progress;
		item.lastProgress = lastProgress;
		item.updated = updated;
		item.colour = colour;
		
		return item;
	}
}
