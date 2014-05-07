package schmoller.tubes.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public class TubesContainer extends DummyModContainer
{
	public TubesContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "TubesCore";
		meta.name = "Tubes Core";
		meta.version = "@{mod.version}";
		meta.description = "";
		meta.authorList = Arrays.asList("Schmoller");
		meta.parent = "Tubes";
	}
	
	@Override
	public boolean registerBus( EventBus bus, LoadController controller )
	{
		bus.register(this);
		return true;
	}
	
	@Override
	public VersionRange acceptableMinecraftVersionRange()
	{
		return VersionParser.parseRange("[1.7.2]");
	}
}
