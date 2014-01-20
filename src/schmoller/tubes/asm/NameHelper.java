package schmoller.tubes.asm;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;
import codechicken.lib.asm.ObfMapping;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class NameHelper
{
	public static ObfMapping getMapping(String className)
	{
		if(ObfMapping.obfuscated)
		{
			String newName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className);
			if(newName.equals(className))
				return new ObfMapping("");

			return new ObfMapping(newName);
		}
		else
			return new ObfMapping(className);
	}
	
	private static Pattern mDescPattern;
	private static String mapDescription(String desc)
	{
		if(mDescPattern == null)
			mDescPattern = Pattern.compile("L([\\w\\$\\<\\>_\\/]+);");
		
		Matcher match = mDescPattern.matcher(desc);
		
		StringBuffer result = new StringBuffer();
		
		while(match.find())
		{
			String mapped = FMLDeobfuscatingRemapper.INSTANCE.unmap(match.group(1));
			match.appendReplacement(result, "L" + mapped + ";");
		}
		
		match.appendTail(result);
		
		return result.toString();
	}
	
	private static Method mGetFieldMap;
    private static Method mGetMethodMap;
    
	private static String unmapName(String name, String owner, String desc)
	{
		if(mGetFieldMap == null)
		{
			try
			{
				mGetFieldMap = FMLDeobfuscatingRemapper.class.getDeclaredMethod("getFieldMap", String.class);
				mGetFieldMap.setAccessible(true);
				
				mGetMethodMap = FMLDeobfuscatingRemapper.class.getDeclaredMethod("getMethodMap", String.class);
				mGetMethodMap.setAccessible(true);
			}
			catch(Exception e)
			{
				Throwables.propagateIfPossible(e);
				throw new RuntimeException(e);
			}
		}

		boolean method = false;
		Map<String,String> map;
		try
		{
			if(desc.contains("("))
			{
				map = (Map<String,String>)mGetMethodMap.invoke(FMLDeobfuscatingRemapper.INSTANCE, owner);
				method = true;
			}
			else
				map = (Map<String,String>)mGetFieldMap.invoke(FMLDeobfuscatingRemapper.INSTANCE, owner);
		}
		catch(Exception e)
		{
			Throwables.propagateIfPossible(e);
			throw new RuntimeException(e);
		}
		
		if(map == null)
		{
			System.out.println("No mapping for " + name);
			return name;
		}
		
		for(Entry<String, String> ent : map.entrySet())
		{
			if(!ent.getValue().equals(name))
				continue;
			
			if(method)
				return ent.getKey().substring(0, ent.getKey().indexOf('('));
			else
				return ent.getKey().split(":")[0];
		}
					
		return name;
	}
	
	public static ObfMapping getMapping(String owner, String name, String desc)
	{
		if(ObfMapping.obfuscated)
		{
			owner = FMLDeobfuscatingRemapper.INSTANCE.unmap(owner);
			desc = mapDescription(desc);
			return new ObfMapping(owner, unmapName(name, owner, desc), desc);
		}
		else
			return new ObfMapping(owner, name, desc);
	}
}
