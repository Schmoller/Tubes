package schmoller.tubes;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.renderer.texture.IconRegister;
import schmoller.tubes.definitions.TubeDefinition;
import schmoller.tubes.parts.BaseTubePart;

public class TubeRegistry implements IPartFactory
{
	private static TubeRegistry mInstance;
	
	private boolean mCanAdd = true;
	private HashMap<String, TubeDefinition> mRegisteredTubes = new HashMap<String, TubeDefinition>();
	
	public static TubeRegistry instance()
	{
		if(mInstance == null)
			mInstance = new TubeRegistry();
		
		return mInstance;
	}
	
	public static void registerTube(TubeDefinition tube, String name)
	{
		assert(instance().mCanAdd);
		
		instance().mRegisteredTubes.put(name, tube);
	}
	
	public void registerIcons(IconRegister register)
	{
		for(TubeDefinition def : mRegisteredTubes.values())
			def.registerIcons(register);
	}

	public void finalizeTubes()
	{
		mCanAdd = false;
		
		String[] names = new String[mRegisteredTubes.size()];
		
		int index = 0;
		for(String key : mRegisteredTubes.keySet())
			names[index++] = "tubes_" + key;

		MultiPartRegistry.registerParts(this, names);
	}
	
	public Set<String> getTypeNames()
	{
		return Collections.unmodifiableSet(mRegisteredTubes.keySet());
	}
	
	public TubeDefinition getDefinition(String name)
	{
		return mRegisteredTubes.get(name);
	}
	
	private HashMap<String, Constructor<? extends BaseTubePart>> mCachedConstructors = new HashMap<String, Constructor<? extends BaseTubePart>>();
	
	@Override
	public TMultiPart createPart( String name, boolean client )
	{
		String actualName = name.replaceFirst("tubes_", "");
		
		try
		{
			Constructor<? extends BaseTubePart> constructor = mCachedConstructors.get(actualName);
			if(constructor == null)
			{
				TubeDefinition def = getDefinition(actualName);
				constructor = def.getPartClass().getConstructor(String.class);
				mCachedConstructors.put(actualName, constructor);
			}
			
			return constructor.newInstance(actualName);
		}
		catch(NoSuchMethodException e)
		{
			FMLLog.severe("Cannot find the constructor that takes a String, for the specified TubePart used by %s", actualName);
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			FMLLog.severe("Cannot find the constructor that takes a String, for the specified TubePart used by %s", actualName);
			throw new RuntimeException(e);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
}
