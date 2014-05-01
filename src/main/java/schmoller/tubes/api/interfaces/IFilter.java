package schmoller.tubes.api.interfaces;

import java.util.List;

import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;

public interface IFilter
{
	public String getType();
	
	/**
	 * Gets the corresponding payload class if there is one
	 */
	public Class<? extends Payload> getPayloadType();
	
	public boolean matches(Payload payload, SizeMode mode);
	public boolean matches(TubeItem item, SizeMode mode);
	
	public void increase(boolean useMax, boolean shift);
	public void decrease(boolean shift);
	
	public int getMax();
	public int size();
	
	public void write(NBTTagCompound tag);
	public void write(MCDataOutput output);
	
	public IFilter copy();
	
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip(List<String> current);
	@SideOnly(Side.CLIENT)
	public void renderFilter(int x, int y);
}
