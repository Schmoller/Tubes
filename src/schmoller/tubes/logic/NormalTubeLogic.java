package schmoller.tubes.logic;

import schmoller.tubes.CommonHelper;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class NormalTubeLogic extends TubeLogic
{
	private int mColor = -1;
	

	public NormalTubeLogic( ITube tube )
	{
		super(tube);
	}
	
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
		
		LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(item);
		if(liquid != null && LiquidDictionary.findLiquidName(liquid).equals("Water"))
		{
			mColor = -1;
			mTube.updateState();
			return true;
		}
		
		int index = CommonHelper.getDyeIndex(item);
		if(index != -1)
		{
			mColor = index;
			mTube.updateState();
			return true;
		}
		return false;
	}
}
