package schmoller.tubes.asm;

import codechicken.core.asm.ClassOverrider;
import codechicken.lib.asm.ObfMapping;
import net.minecraft.launchwrapper.IClassTransformer;

public class MinecraftTransformer implements IClassTransformer
{
	@Override
	public byte[] transform( String className, String arg1, byte[] bytes )
	{
		return ClassOverrider.overrideBytes(className, bytes, new ObfMapping("net/minecraft/tileentity/TileEntityHopper"), TubesPlugin.location);
	}
}
