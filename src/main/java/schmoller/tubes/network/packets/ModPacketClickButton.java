package schmoller.tubes.network.packets;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import schmoller.tubes.network.ModPacket;

public class ModPacketClickButton extends ModPacket
{
	public int windowId;
	public int buttonId;
	public int mouseButton;
	public int modifiers;
	
	public ModPacketClickButton()
	{
	}
	
	public ModPacketClickButton(int windowId, int buttonId, int mouseButton, int modifiers)
	{
		this.windowId = windowId;
		this.buttonId = buttonId;
		this.mouseButton = mouseButton;
		this.modifiers = modifiers;
	}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		output.writeInt(windowId);
		output.writeByte(buttonId);
		output.writeByte(mouseButton);
		output.writeByte(modifiers);
	}
	
	@Override
	public void read( MCDataInput input ) throws IOException
	{
		windowId = input.readInt();
		buttonId = input.readByte();
		mouseButton = input.readByte();
		modifiers = input.readByte();
	}
}
