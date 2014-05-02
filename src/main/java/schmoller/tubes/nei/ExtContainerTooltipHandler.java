package schmoller.tubes.nei;

import java.util.Collections;
import java.util.List;

import codechicken.nei.guihook.IContainerTooltipHandler;

import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiExtContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ExtContainerTooltipHandler implements IContainerTooltipHandler
{
	@Override
	public List<String> handleTooltip( GuiContainer gui, int x, int y, List<String> current )
	{
		if(!(gui instanceof GuiExtContainer))
			return current;
		
		Slot slot = null;
		
		x = x - ((GuiExtContainer)gui).getLeft();
		y = y - ((GuiExtContainer)gui).getTop();
		
		for(Slot s : (List<Slot>)gui.inventorySlots.inventorySlots)
		{
			if(x >= s.xDisplayPosition - 1 && x <= s.xDisplayPosition + 16 && y >= s.yDisplayPosition - 1 && y <= s.yDisplayPosition + 16)
			{
				slot = s;
				break;
			}
		}
		
		if(slot instanceof FakeSlot)
		{
			List<String> tooltip = null;
			
			if(((FakeSlot)slot).getFilter() != null)
				tooltip = ((FakeSlot)slot).getFilter().getTooltip(null);
			
			tooltip = ((FakeSlot)slot).getTooltip(tooltip);
			
			if(tooltip != null)
				return tooltip;
			else
				return Collections.EMPTY_LIST;
		}
		else
			return current;
	}

	@Override
	public List<String> handleItemTooltip( GuiContainer gui, ItemStack itemstack, int x, int y, List<String> currenttip )
	{
		return currenttip;
	}
	
	@Override
	public List<String> handleItemDisplayName( GuiContainer gui, ItemStack item, List<String> currentName )
	{
		return currentName;
	}
}
