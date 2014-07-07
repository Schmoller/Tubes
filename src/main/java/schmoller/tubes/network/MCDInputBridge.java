package schmoller.tubes.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.vec.BlockCoord;

public class MCDInputBridge implements MCDataInput
{
	private ByteBuf mBuffer;
	
	public MCDInputBridge(ByteBuf buf)
	{
		mBuffer = buf;
	}

	@Override
	public boolean readBoolean()
	{
		return mBuffer.readBoolean();
	}
	
	@Override
	public byte readByte()
	{
		return mBuffer.readByte();
	}
	
	@Override
	public short readUByte()
	{
		return mBuffer.readUnsignedByte();
	}
	
	@Override
	public byte[] readByteArray( int size )
	{
		byte[] bytes = new byte[size];
		mBuffer.readBytes(bytes);
		return bytes;
	}
	
	@Override
	public char readChar()
	{
		return mBuffer.readChar();
	}
	
	@Override
	public BlockCoord readCoord()
	{
		return new BlockCoord(readInt(), readInt(), readInt());
	}

	@Override
	public double readDouble()
	{
		return mBuffer.readDouble();
	}
	
	@Override
	public float readFloat()
	{
		return mBuffer.readFloat();
	}
	
	@Override
	public FluidStack readFluidStack()
	{
		short id = readShort();
		if(id == -1)
			return null;
		
		int amount = readInt();
		NBTTagCompound tag = readNBTTagCompound();
		
		return new FluidStack(id, amount, tag);
	}
	
	@Override
	public int readInt()
	{
		return mBuffer.readInt();
	}

	@Override
	public ItemStack readItemStack()
	{
		short id = readShort();
		if(id == -1)
			return null;
		
		byte size = readByte();
		short damage = readShort();
		NBTTagCompound tag = readNBTTagCompound();
		
		ItemStack item = new ItemStack(Item.getItemById(id), size, damage);
		item.setTagCompound(tag);
		
		return item;
	}
	
	@Override
	public long readLong()
	{
		return mBuffer.readLong();
	}
	
	@Override
	public NBTTagCompound readNBTTagCompound()
	{
		try
		{
			PacketBuffer buffer = new PacketBuffer(mBuffer);
			return buffer.readNBTTagCompoundFromBuffer();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public short readShort()
	{
		return mBuffer.readShort();
	}
	
	@Override
	public int readUShort()
	{
		return mBuffer.readUnsignedShort();
	}

	@Override
	public String readString()
	{
		int size = readShort();
		char[] chars = new char[size];
		for(int i = 0; i < chars.length; ++i)
			chars[i] = readChar();
		return new String(chars);
	}
	
	@Override
	public int readVarInt()
	{
		int value = 0;
		byte temp;
		int spot = 0;
		do
		{
			temp = readByte();
			value |= (temp & 0x7F) << spot++ * 7;
		} while ((temp & 0x80) == 128);

		return value;
	}
	
	@Override
	public int readVarShort()
	{
		int low = readUShort();
		int high = 0;
		if ((low & 0x8000) != 0)
		{
			low &= 0x7FFF;
			high = readUShort();
		}
		return (high & 0xFF) << 15 | low;
	}
}
