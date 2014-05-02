package schmoller.tubes.network.packets;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

import net.minecraft.item.ItemStack;

import schmoller.tubes.network.ModPacket;

public class ModPacketNEIDragDrop extends ModPacket
{
	public int windowId;
	public ItemStack item;
	public int slot;
	public int button;
	public int modifiers;
	
	public ModPacketNEIDragDrop()
	{
		
	}
	
	public ModPacketNEIDragDrop(int windowId, int slot, int button, int modifiers, ItemStack item)
	{
		this.windowId = windowId;
		this.slot = slot;
		this.button = button;
		this.modifiers = modifiers;
		this.item = item;
	}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		output.writeInt(windowId);
		output.writeShort(slot);
		output.writeShort(button);
		output.writeShort(modifiers);
		output.writeItemStack(item);
	}

	@Override
	public void read( MCDataInput input ) throws IOException
	{
		windowId = input.readInt();
		slot = input.readShort();
		button = input.readShort();
		modifiers = input.readShort();
		item = input.readItemStack();
	}

}
