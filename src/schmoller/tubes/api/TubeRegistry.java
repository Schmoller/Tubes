package schmoller.tubes.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import schmoller.tubes.api.client.ITubeRender;

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
	
	@SideOnly(Side.CLIENT)
	public ITubeRender getRender(TubeDefinition def)
	{
		ITubeRender render = mRenderers.get(def);
		if(render != null)
			return render;
		
		mRenderers.put(def, mRenderers.get(getDefinition("basic")));
		return mRenderers.get(def);
	}
	
	@Override
	public TMultiPart createPart( String name, boolean client )
	{
		String actualName = name.replaceFirst("tubes_", "");
		
		TubeDefinition def = getDefinition(actualName);
		return def.createTube();
	}
	
	
}
