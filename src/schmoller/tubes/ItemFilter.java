package schmoller.tubes;

import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.InventoryHelper;
import schmoller.tubes.api.interfaces.IFilter;

public class ItemFilter implements IFilter
{
	private ItemStack mTemplate;
	private boolean mFuzzy;
	
	@SideOnly(Side.CLIENT)
	private static RenderItem itemRenderer;
	
	public ItemFilter(ItemStack template, boolean fuzzy)
	{
		assert(template != null);
		
		mTemplate = template;
		mFuzzy = fuzzy;
	}
	
	@Override
	public String getType()
	{
		return "item";
	}
	
	public ItemStack getItem()
	{
		return mTemplate;
	}
	
	@Override
	public boolean matches( Payload payload, SizeMode mode )
	{
		if(!(payload instanceof ItemPayload))
			return false;
		
		if(!mFuzzy && !InventoryHelper.areItemsEqual((ItemStack)payload.get(), mTemplate))
			return false;
		else if(mFuzzy)
		{
			int id = OreDictionary.getOreID(mTemplate);
			if(id != OreDictionary.getOreID((ItemStack)payload.get()))
				return false;
		}
		
		ItemStack other = (ItemStack)payload.get();
		
		switch(mode)
		{
		case Exact:
			return other.stackSize == mTemplate.stackSize;
		case GreaterEqual:
			return other.stackSize >= mTemplate.stackSize;
		case LessEqual:
			return other.stackSize <= mTemplate.stackSize;
		case Max:
			return true;
		}
		
		return false;
	}

	@Override
	public boolean matches( TubeItem item, SizeMode mode )
	{
		return matches(item.item, mode);
	}

	@Override
	public void increase( boolean useMax, boolean shift )
	{
		mTemplate.stackSize += (shift ? 10 : 1);
		if (useMax && mTemplate.stackSize > getMax())
			mTemplate.stackSize = getMax();
	}

	@Override
	public void decrease( boolean shift )
	{
		mTemplate.stackSize -= (shift ? 10 : 1);
		if (mTemplate.stackSize < 0)
			mTemplate.stackSize = 0;
	}

	@Override
	public int getMax()
	{
		return mTemplate.getMaxStackSize();
	}

	@Override
	public int size()
	{
		return mTemplate.stackSize;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFilter( int x, int y )
	{
		if(itemRenderer == null)
			itemRenderer = new RenderItem();
		
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        itemRenderer.zLevel = 200.0F;
        FontRenderer font = mTemplate.getItem().getFontRenderer(mTemplate);
        if (font == null) 
        	font = Minecraft.getMinecraft().fontRenderer;
        
        itemRenderer.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), mTemplate, x, y);
        itemRenderer.renderItemOverlayIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), mTemplate, x, y, "");

        itemRenderer.zLevel = 0.0F;
	}

	@Override
	public void write( NBTTagCompound tag )
	{
		mTemplate.writeToNBT(tag);
		tag.setBoolean("fuzzy", mFuzzy);
	}
	
	@Override
	public void write( MCDataOutput output )
	{
		output.writeItemStack(mTemplate);
		output.writeBoolean(mFuzzy);
	}
	
	public static ItemFilter from(NBTTagCompound tag)
	{
		return new ItemFilter(ItemStack.loadItemStackFromNBT(tag), tag.getBoolean("fuzzy"));
	}
	
	public static ItemFilter from(MCDataInput input)
	{
		return new ItemFilter(input.readItemStack(), input.readBoolean());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip( List<String> current )
	{
		List list = mTemplate.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k)
        {
            if (k == 0)
                list.set(k, "\u00a7" + Integer.toHexString(mTemplate.getRarity().rarityColor) + (String)list.get(k));
            else
                list.set(k, EnumChatFormatting.GRAY + (String)list.get(k));
        }
        
        return list;
	}
}
