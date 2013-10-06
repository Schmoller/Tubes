package schmoller.tubes.parts;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;
import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.IDirectionalTube;
import schmoller.tubes.IRedstoneTube;

public class DirectionalRedstoneTubePart extends BaseTubePart implements IDirectionalTube, IRedstonePart
{
	private int mDirection;
	private int mRedstoneLevel;
	
	public DirectionalRedstoneTubePart(String type)
	{
		super(type);
		mDirection = 0;
		mRedstoneLevel = 0;
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
	public boolean canConnectRedstone( int side )
	{
		return true;
	}

	@Override
	public int strongPowerLevel( int side )
	{
		return 0;
	}

	@Override
	public int weakPowerLevel( int side )
	{
		return 0;
	}
	
	private int getPower()
	{
		int current = 0;
		for(int side = 0; side < 6; ++side)
			current = Math.max(current, RedstoneInteractions.getPowerTo(world(), x(), y(), z(), side, 0x1f));
		
		return current;
	}
	
	@Override
	public void onWorldJoin()
	{
		mRedstoneLevel = getPower();
		((IRedstoneTube)getLogic()).onLoadPower(mRedstoneLevel);
	}
	
	@Override
	public void update()
	{
		int level = getPower();
		if(level != mRedstoneLevel)
		{
			((IRedstoneTube)getLogic()).onPowerChange(level);
			mRedstoneLevel = level;
		}

		super.update();
	}
}
