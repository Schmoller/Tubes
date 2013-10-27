package schmoller.tubes.network.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;

import schmoller.tubes.network.ModPacket;

public class ModPacketNEIDragDrop extends ModPacket
{
	public ItemStack item;
	public int slot;
	
	public ModPacketNEIDragDrop()
	{
		
	}
	
	public ModPacketNEIDragDrop(int slot, ItemStack item)
	{
		this.item = item;
		this.slot = slot;
	}
	
	@Override
	public void write( DataOutput output ) throws IOException
	{
		output.writeShort(slot);
		Packet.writeItemStack(item, output);
	}

	@Override
	public void read( DataInput input ) throws IOException
	{
		slot = input.readShort();
		item = Packet.readItemStack(input);
	}

}
