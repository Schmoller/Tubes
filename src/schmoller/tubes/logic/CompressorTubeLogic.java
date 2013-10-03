package schmoller.tubes.logic;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;

public class CompressorTubeLogic extends TubeLogic
{
	private ITube mTube;
	private TubeItem mCurrent;
	private ItemStack mTarget;
	
	public CompressorTubeLogic(ITube tube)
	{
		mTube = tube;
		mTarget = new ItemStack(0, 64, 0);
		mCurrent = null;
	}
	
	@Override
	public boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item, int side )
	{
		if(mCurrent != null)
			return (item.item.isItemEqual(mCurrent.item) && ItemStack.areItemStackTagsEqual(item.item, mCurrent.item));
		
		return true;
	}
	
	@Override
	public void onLoad( NBTTagCompound root )
	{
		NBTTagCompound target = (NBTTagCompound)root.getTag("Target");
		mTarget = new ItemStack(0,0,0);
		mTarget.readFromNBT(target);
		
		
		if(root.hasKey("Current"))
		{
			NBTTagCompound current = (NBTTagCompound)root.getTag("Current");
			mCurrent = TubeItem.readFromNBT(current);
		}
	}
	
	@Override
	public void onSave( NBTTagCompound root )
	{
		NBTTagCompound target = new NBTTagCompound();
		mTarget.writeToNBT(target);
		root.setTag("Target", target);
		
		if(mCurrent != null)
		{
			NBTTagCompound current = new NBTTagCompound();
			mCurrent.writeToNBT(current);
			root.setTag("Current", current);
		}
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		if(input.readBoolean())
			mCurrent = TubeItem.read(input);
		else
			mCurrent = null;
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		if(mCurrent != null)
		{
			output.writeBoolean(true);
			mCurrent.write(output);
		}
		else
			output.writeBoolean(false);
	}
	
	@Override
	public int onDetermineDestination( TubeItem item )
	{
		if(mCurrent != null)
		{
			if(!item.item.isItemEqual(mCurrent.item) || !ItemStack.areItemStackTagsEqual(item.item, mCurrent.item))
				return item.direction ^ 1;
			
			int amt = Math.min(item.item.stackSize, mTarget.stackSize - mCurrent.item.stackSize);
			mCurrent.item.stackSize += amt;
			item.item.stackSize -= amt;
			
			if(mCurrent.item.stackSize == mTarget.stackSize)
			{
				mCurrent.updated = true;
				mTube.addItem(mCurrent, true);
				mCurrent = null;
				
				if(item.item.stackSize > 0)
					mCurrent = item.clone();
			}
			
			return -2;
		}
		else if(mTarget.itemID == 0 || (item.item.isItemEqual(mTarget) && ItemStack.areItemStackTagsEqual(item.item, mTarget)))
		{
			if(item.item.stackSize < mTarget.stackSize)
			{
				mCurrent = item.clone();
				return -2;
			}
		}
		
		int conns = mTube.getConnections();
		int count = 0;
		int dir = 0;
		
		conns -= (conns & (1 << (item.direction ^ 1)));
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				++count;
				dir = i;
			}
		}
		
		if(count > 1)
			dir = TubeHelper.findNextDirection(mTube.world(), mTube.x(), mTube.y(), mTube.z(), item);
		
		return dir;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		if(con instanceof ITube)
			return !(((ITube)con).getLogic() instanceof CompressorTubeLogic);

		return true;
	}
}
