package schmoller.tubes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
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
	
	@Override
	public TMultiPart createPart( String name, boolean client )
	{
		String actualName = name.replaceFirst("tubes_", "");
		
		return new BaseTubePart(actualName);
	}
	
	
}
