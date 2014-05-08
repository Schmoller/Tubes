package schmoller.tubes.gui;

import schmoller.tubes.api.gui.GuiExtContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

public class GUIHook
{
	public static boolean drawSlotInventory(GuiContainer gui, Slot slot)
	{
		if(gui instanceof GuiExtContainer)
			return ((GuiExtContainer)gui).drawSlotInventory(slot);
		return false;
	}
}
