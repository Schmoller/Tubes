package schmoller.tubes.nei;

import java.util.List;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiExtContainer;
import schmoller.tubes.network.packets.ModPacketNEIDragDrop;

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
			if(x >= s.xDisplayPosition && x < s.xDisplayPosition + 16 && y >= s.yDisplayPosition && y < s.yDisplayPosition + 16)
			{
				slot = s;
				break;
			}
			++index;
		}
		
		if(slot instanceof FakeSlot)
		{
			((FakeSlot)slot).putStack(item);
			ModTubes.packetManager.sendPacketToServer(new ModPacketNEIDragDrop(index, item.copy()));
			return true;
		}

		return false;
	}

}
