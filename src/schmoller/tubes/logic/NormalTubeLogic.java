package schmoller.tubes.logic;

import schmoller.tubes.CommonHelper;
import schmoller.tubes.ITubeConnectable;
import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NormalTubeLogic extends TubeLogic
{
	private int mColor = -1;
	
	@Override
	public int getColor()
	{
		return mColor;
	}
	
	@Override
	public void onLoad( NBTTagCompound root )
	{
		if(root.hasKey("Color"))
			mColor = root.getShort("Color");
	}
	
	@Override
	public void onSave( NBTTagCompound root )
	{
		root.setShort("Color", (short)mColor);
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		mColor = input.readShort();
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		output.writeShort(mColor);
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return (mColor == -1 || con.getColor() == mColor || con.getColor() == -1);
	}
	
	
	@Override
	public boolean onActivate( EntityPlayer player )
	{
		ItemStack item = player.inventory.getCurrentItem();
		if(item == null || item.itemID == 0)
			return false;
		
		int index = CommonHelper.getDyeIndex(item);
		if(index != -1)
		{
			mColor = index;
			return true;
		}
		return false;
	}
}
