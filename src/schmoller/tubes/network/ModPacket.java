package schmoller.tubes.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class ModPacket
{
	public abstract void write(DataOutput output) throws IOException;
	public abstract void read(DataInput input) throws IOException;
}
