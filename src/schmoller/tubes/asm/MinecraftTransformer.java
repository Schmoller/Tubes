package schmoller.tubes.asm;

import codechicken.core.asm.ClassOverrider;
import codechicken.lib.asm.ObfMapping;
import net.minecraft.launchwrapper.IClassTransformer;

public class MinecraftTransformer implements IClassTransformer
{
	@Override
	public byte[] transform( String className, String arg1, byte[] bytes )
	{
		if(className.equals(TubesPlugin.getHopperClass().replace('/', '.')))
			System.err.println("************************ Found it ****************");
		
		return ClassOverrider.overrideBytes(className, bytes, new ObfMapping(TubesPlugin.getHopperClass()), TubesPlugin.location);
	}
}
