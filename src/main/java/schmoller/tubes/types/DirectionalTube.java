package schmoller.tubes.types;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.api.interfaces.IDirectionalTube;

public abstract class DirectionalTube extends BaseTube implements IDirectionalTube
{
	private int mDirection;

	public DirectionalTube( String type )
	{
		super(type);
		mDirection = 0;
	}

	@Override
	protected int getConnectableSides()
	{
		return 63 - (63 & (1 << mDirection));
	}

	@Override
	public int getFacing()
	{
		return mDirection;
	}

	@Override
	public boolean canFaceDirection( int face )
	{
		return true;
	}

	@Override
	public void setFacing( int face )
	{
		mDirection = face;
	}

	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		root.setByte("Dir", (byte)mDirection);
	}

	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		mDirection = root.getByte("Dir");
	}

	@Override
	public void readDesc( MCDataInput packet )
	{
		super.readDesc(packet);
		mDirection = packet.readByte();
	}

	@Override
	public void writeDesc( MCDataOutput packet )
	{
		super.writeDesc(packet);
		packet.writeByte(mDirection);
	}

	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(item != null && item.getItem() instanceof IToolWrench)
		{
			IToolWrench wrench = (IToolWrench)item.getItem();
			
			if(wrench.canWrench(player, x(), y(), z()))
			{
				++mDirection;
				
				if(mDirection >= 6)
					mDirection = 0;
				
				markForRender();
				
				wrench.wrenchUsed(player, x(), y(), z());
				return true;
			}
		}
		
		return false;
	}
}