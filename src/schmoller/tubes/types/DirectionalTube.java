package schmoller.tubes.types;

import net.minecraft.nbt.NBTTagCompound;
import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import schmoller.tubes.IDirectionalTube;

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

}