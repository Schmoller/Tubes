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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import schmoller.tubes.definitions.TubeDefinition;
import schmoller.tubes.parts.BaseTubePart;
import schmoller.tubes.render.ITubeRender;

public class TubeRegistry implements IPartFactory
{
	private static TubeRegistry mInstance;
	
	private boolean mCanAdd = true;
	private HashMap<String, TubeDefinition> mRegisteredTubes = new HashMap<String, TubeDefinition>();
	private HashMap<TubeDefinition, ITubeRender> mRenderers = new HashMap<TubeDefinition, ITubeRender>();
	
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
	
	@SideOnly(Side.CLIENT)
	public static void registerRenderer(String typeName, ITubeRender render)
	{
		assert(instance().mCanAdd);
		assert(instance().mRegisteredTubes.containsKey(typeName));
		assert(render != null);
		
		instance().mRenderers.put(instance().mRegisteredTubes.get(typeName), render);
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
		TubeDefinition def = mRegisteredTubes.get(name);
		if(def != null)
			return def;
		
		return mRegisteredTubes.get("basic");
	}
	
	public ITubeRender getRender(TubeDefinition def)
	{
		ITubeRender render = mRenderers.get(def);
		if(render != null)
			return render;
		
		return mRenderers.values().iterator().next();
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
