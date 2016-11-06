package schmoller.tubes.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;

public class MCDOutputBridge implements MCDataOutput
{
	private ByteBuf mBuffer;
	
	public MCDOutputBridge(ByteBuf buf)
	{
		mBuffer = buf;
	}

	@Override
	public MCDataOutput writeBoolean( boolean bool )
	{
		mBuffer.writeBoolean(bool);
		return this;
	}

	@Override
	public MCDataOutput writeByte( int value )
	{
		mBuffer.writeByte((byte)value);
		return this;
	}

	@Override
	public MCDataOutput writeByteArray( byte[] array )
	{
		mBuffer.writeBytes(array);
		return this;
	}

	@Override
	public MCDataOutput writeChar( char ch )
	{
		mBuffer.writeChar(ch);
		return this;
	}

	@Override
	public MCDataOutput writeCoord( BlockCoord coord )
	{
		writeInt(coord.x);
		writeInt(coord.y);
		writeInt(coord.z);
		
		return this;
	}

	@Override
	public MCDataOutput writeCoord( int x, int y, int z )
	{
		writeInt(x);
		writeInt(y);
		writeInt(z);
		
		return this;
	}

	@Override
	public MCDataOutput writeDouble( double value )
	{
		mBuffer.writeDouble(value);
		return this;
	}

	@Override
	public MCDataOutput writeFloat( float value )
	{
		mBuffer.writeFloat(value);
		return this;
	}

	@Override
	public MCDataOutput writeFluidStack( FluidStack fluid )
	{
		if (fluid == null)
			writeShort(-1);
		else
		{
			writeShort(fluid.getFluidID());
			writeInt(fluid.amount);
			writeNBTTagCompound(fluid.tag);
		}
		return this;
	}

	@Override
	public MCDataOutput writeInt( int value )
	{
		mBuffer.writeInt(value);
		return this;
	}

	@Override
	public MCDataOutput writeItemStack( ItemStack stack )
	{
		if (stack == null) 
			writeShort(-1);
		else 
		{
			writeShort(Item.getIdFromItem(stack.getItem()));
			writeByte(stack.stackSize);
			writeShort(stack.getItemDamage());
			writeNBTTagCompound(stack.stackTagCompound);
		}
		return this;
	}

	@Override
	public MCDataOutput writeLong( long value )
	{
		mBuffer.writeLong(value);
		return this;
	}

	@Override
	public MCDataOutput writeNBTTagCompound( NBTTagCompound tag )
	{
		try
		{
			if (tag == null)
				writeShort(-1);
			else
			{
				byte[] bytes = CompressedStreamTools.compress(tag);
				writeShort((short)bytes.length);
				writeByteArray(bytes);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		return this;
	}

	@Override
	public MCDataOutput writeShort( int value )
	{
		mBuffer.writeShort((short)value);
		return this;
	}

	@Override
	public MCDataOutput writeString( String string )
	{
		mBuffer.writeShort((short)string.length());
		for(char c : string.toCharArray())
			mBuffer.writeChar(c);
		
		return this;
	}

	@Override
	public MCDataOutput writeVarInt( int value )
	{
		while ((value & 0x80) != 0)
		{
			writeByte(value & 0x7F | 0x80);
			value >>>= 7;
		}
		
		writeByte(value);
		return this;
	}

	@Override
	public MCDataOutput writeVarShort( int value )
	{
		int low = value & 0x7FFF;
		int high = (value & 0x7F8000) >> 15;
		if (high != 0)
			low |= 0x8000;
		writeShort(low);
		if (high != 0)
			writeByte(high);
		return this;
	}
}
