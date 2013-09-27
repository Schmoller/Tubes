package schmoller.tubes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class TubeItem
{
	public TubeItem(ItemStack item)
	{
		this.item = item;
	}
	
	public ItemStack item;
	public int direction = 0;
	public float progress = 0;
	public boolean updated = false;
	
	@Override
	public String toString()
	{
		return item.toString() + " " + ForgeDirection.getOrientation(direction).name() + " U:" + updated + " P:" + progress;
	}
}
