package schmoller.tubes.nei;

import java.util.List;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.network.packets.ModPacketNEIDragDrop;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;

public class DragDropHandler implements INEIGuiHandler
{

	@Override
	public VisiblityData modifyVisiblity( GuiContainer paramGuiContainer, VisiblityData paramVisiblityData )
	{
		return null;
	}

	@Override
	public int getItemSpawnSlot( GuiContainer paramGuiContainer, ItemStack paramItemStack )
	{
		return 0;
	}

	@Override
	public List<TaggedInventoryArea> getInventoryAreas( GuiContainer paramGuiContainer )
	{
		return null;
	}

	@Override
	public boolean handleDragNDrop( GuiContainer gui, int x, int y, ItemStack item, int button )
	{
		if(!(gui instanceof GuiExtContainer))
			return false;
		
		Slot slot = null;
		
		x = x - ((GuiExtContainer)gui).getLeft();
		y = y - ((GuiExtContainer)gui).getTop();
		
		int index = 0;
		for(Slot s : (List<Slot>)gui.inventorySlots.inventorySlots)
		{
			if(x >= s.xDisplayPosition - 1 && x <= s.xDisplayPosition + 16 && y >= s.yDisplayPosition - 1 && y <= s.yDisplayPosition + 16)
			{
				slot = s;
				break;
			}
			++index;
		}
		
		if(slot instanceof FakeSlot)
		{
			int modifiers = 0;
			if(GuiScreen.isShiftKeyDown())
				modifiers = 1;
			if(GuiScreen.isCtrlKeyDown())
				modifiers |= 2;
			
			ModTubes.packetManager.sendPacketToServer(new ModPacketNEIDragDrop(gui.inventorySlots.windowId, index, button, modifiers, item.copy()));
			return true;
		}

		return false;
	}
	@Override
	public boolean hideItemPanelSlot( GuiContainer gui, int x, int y, int w, int h )
	{
		return false;
	}
}
