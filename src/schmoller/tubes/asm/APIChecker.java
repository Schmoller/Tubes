package schmoller.tubes.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class APIChecker implements IClassTransformer
{
	@Override
	public byte[] transform( String name, String arg1, byte[] bytes )
	{
		// TODO: Handle checking API correctness
		return bytes;
	}

}
