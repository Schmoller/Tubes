package schmoller.tubes.asm;

import codechicken.lib.asm.ObfMapping;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class NameHelper
{
	public static ObfMapping getMapping(String className)
	{
		if(ObfMapping.obfuscated)
			return new ObfMapping(FMLDeobfuscatingRemapper.INSTANCE.unmap(className));
		else
			return new ObfMapping(className);
	}
	
	public static ObfMapping getMapping(String owner, String name, String desc)
	{
		if(ObfMapping.obfuscated)
			return new ObfMapping(FMLDeobfuscatingRemapper.INSTANCE.unmap(owner), FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(owner, name, desc), FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc));
		else
			return new ObfMapping(owner, name, desc);
	}
}
