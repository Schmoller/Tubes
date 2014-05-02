package schmoller.tubes;

import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.render.CCRenderState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.InventoryHelper;
import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.interfaces.IFilter;

public class ItemFilter implements IFilter
{
	public static ResourceLocation oreDict = new ResourceLocation("tubes", "textures/gui/oreDictHighlight.png"); 
	
	private ItemStack mTemplate;
	private boolean mFuzzy;
	
	@SideOnly(Side.CLIENT)
	private static RenderItem itemRenderer;
	
	public ItemFilter(ItemStack template, boolean fuzzy)
	{
		assert(template != null);
		
		mTemplate = template.copy();
		mFuzzy = fuzzy;
	}
	
	@Override
	public String getType()
	{
		return "item";
	}
	
	@Override
	public Class<? extends Payload> getPayloadType()
	{
		return ItemPayload.class;
	}
	
	public ItemStack getItem()
	{
		return mTemplate;
	}
	
	public void toggleFuzzy()
	{
		mFuzzy = !mFuzzy;
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
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        itemRenderer.zLevel = 200.0F;
        FontRenderer font = mTemplate.getItem().getFontRenderer(mTemplate);
        if (font == null) 
        	font = Minecraft.getMinecraft().fontRenderer;
        
        itemRenderer.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), mTemplate, x, y);
        if(mFuzzy)
        {
        	int frame = (int)((System.currentTimeMillis() / 50) % 32);
        	float offset = frame * 1/32f;
        	GL11.glDepthFunc(GL11.GL_GREATER);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            CCRenderState.changeTexture(oreDict);
            GL11.glTranslatef(0, 0, -50);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            RenderHelper.renderRect(x, y, 16, 16, 0, offset, 1, 1/32f);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            GL11.glTranslatef(0, 0, 50);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }
        
        itemRenderer.renderItemOverlayIntoGUI(font, Minecraft.getMinecraft().getTextureManager(), mTemplate, x, y, null);

        
        
        GL11.glPopMatrix();
        
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
	public IFilter copy()
	{
		return new ItemFilter(mTemplate, mFuzzy);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip( List<String> current )
	{
		List list = mTemplate.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k)
        {
            if (k == 0)
                list.set(k, mTemplate.getRarity().rarityColor + (String)list.get(k));
            else
                list.set(k, EnumChatFormatting.GRAY + (String)list.get(k));
        }
        
        if(mFuzzy)
        	list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("gui.filter.item.oredict"));
        
        return list;
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof ItemFilter))
			return false;
		
		ItemFilter other = (ItemFilter)obj;
		
		return InventoryHelper.areItemsEqual(mTemplate, other.mTemplate) && mFuzzy == other.mFuzzy;
	}
}
