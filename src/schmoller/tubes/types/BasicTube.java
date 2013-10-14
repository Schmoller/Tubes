package schmoller.tubes.types;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import schmoller.tubes.CommonHelper;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.TubeItem;

public class BasicTube extends BaseTube implements ITubeConnectable
{
	private int mColor;
	
	public static final int CHANNEL_COLOUR = 1;
	
	public BasicTube( String type )
	{
		super(type);
		mColor = NO_COLOUR;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		return (mColor == NO_COLOUR || item.colour == mColor || item.colour == NO_COLOUR);
	}
	
	@Override
	public int getColor()
	{
		return mColor;
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		mColor = root.getShort("Color");
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		root.setShort("Color", (short)mColor);
	}

	@Override
	public void readDesc( MCDataInput packet )
	{
		super.readDesc(packet);
		mColor = packet.readShort();
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		super.writeDesc(packet);
		packet.writeShort(mColor);
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return (mColor == NO_COLOUR || con.getColor() == mColor || con.getColor() == NO_COLOUR);
	}
	
	private void updateColour()
	{
		if(world().isRemote)
			return;
		
		openChannel(CHANNEL_COLOUR).writeShort(mColor);
	}
	
	@Override
	protected void onRecieveDataClient( int channel, MCDataInput input )
	{
		if(channel == CHANNEL_COLOUR)
		{
			mColor = input.readShort();
			tile().markRender();
		}
		
		super.onRecieveDataClient(channel, input);
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(item == null || item.itemID == 0)
			return false;
		
		LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(item);
		if(liquid != null && LiquidDictionary.findLiquidName(liquid).equals("Water"))
		{
			mColor = NO_COLOUR;
			updateColour();
			return true;
		}
		
		int index = CommonHelper.getDyeIndex(item);
		if(index != -1)
		{
			mColor = index;
			updateColour();
			return true;
		}
		return false;
	}
}
