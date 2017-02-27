package schmoller.tubes.asm;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public class TubesContainer extends DummyModContainer
{
	public TubesContainer()
	{
		super(MetadataCollection.from(MetadataCollection.class.getResourceAsStream("/tubesmod.info"), "TubesCore").getMetadataForId("TubesCore", null));
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
		return VersionParser.parseRange("[1.7.10]");
	}
}
