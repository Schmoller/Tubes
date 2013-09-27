package schmoller.tubes.network.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.TubeItem;
import schmoller.tubes.network.ModBlockPacket;

public class ModPacketAddItem extends ModBlockPacket
{
	public TubeItem item;
	
	public ModPacketAddItem()
	{
		
	}
	
	public ModPacketAddItem(int x, int y, int z, TubeItem item)
	{
		super(x, y, z);
		
		this.item = item;
	}
	
	@Override
	public void read( DataInput input ) throws IOException
	{
		super.read(input);
		
		int direction = input.readByte() & 0xFF;
		boolean updated = (direction & 128) != 0;
		direction -= (direction & 128);
		
		float progress = input.readFloat();
		
		short len = input.readShort();
		byte[] bytes = new byte[len];
        input.readFully(bytes);
        NBTTagCompound tag = CompressedStreamTools.decompress(bytes);
        
        item = new TubeItem(ItemStack.loadItemStackFromNBT(tag));
        item.direction = direction;
        item.progress = progress;
        item.updated = updated;
	}
	
	@Override
	public void write( DataOutput output ) throws IOException
	{
		super.write(output);
		
		output.writeByte(item.direction | (item.updated ? 128 : 0));
		output.writeFloat(item.progress);
		NBTTagCompound tag = new NBTTagCompound();
		item.item.writeToNBT(tag);
		
		byte[] bytes = CompressedStreamTools.compress(tag);
        output.writeShort((short)bytes.length);
        output.write(bytes);
	}
	
	@Override
	public String toString()
	{
		return String.format("AddItem: %d, %d, %d {%s}", xCoord, yCoord, zCoord, item);
	}
}
