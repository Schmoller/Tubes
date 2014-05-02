package schmoller.tubes.network;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

public abstract class ModPacket
{
	public abstract void write(MCDataOutput output) throws IOException;
	public abstract void read(MCDataInput input) throws IOException;
}
