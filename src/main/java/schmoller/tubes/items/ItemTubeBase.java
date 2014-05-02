package schmoller.tubes.items;

import java.util.List;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.interfaces.IDirectionalTube;
import schmoller.tubes.api.interfaces.ISpecialItemCompare;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTubeBase extends JItemMultiPart implements ISpecialItemCompare
{

	public ItemTubeBase()
	{
		setHasSubtypes(true);
		setCreativeTab(ModTubes.creativeTab);
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void registerIcons( IIconRegister register )
	{
		itemIcon = register.registerIcon("missing");
	}
	
	@Override
	public boolean onItemUse( ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		if(super.onItemUse(item, player, world, x, y, z, side, hitX, hitY, hitZ))
		{
			world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, Blocks.glass.stepSound.func_150496_b(), (Blocks.glass.stepSound.getVolume() * 5.0F), Blocks.glass.stepSound.getPitch() * .9F);
			return true;
		}
		
		return false;
	}
	
	@Override
	public TMultiPart newPart( ItemStack item, EntityPlayer player, World world, BlockCoord pos, int side, Vector3 hit )
	{
		TMultiPart part = MultiPartRegistry.createPart("tubes_" + getTubeType(item), false);
		
		if(part instanceof IDirectionalTube)
		{
			//int face = (player.isSneaking() ? side : side ^ 1);
			int face = side ^ 1;
			
			if(((IDirectionalTube)part).canFaceDirection(face))
				((IDirectionalTube)part).setFacing(face);
		}
		
		return part;
	}
	
	@Override
	public String getUnlocalizedName( ItemStack stack )
	{
		return "tubes." + getTubeType(stack);
	}
	
	public ItemStack createForType(String tubeType, int amount)
	{
		ItemStack item = new ItemStack(this, amount);
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("tube", tubeType);
		item.setTagCompound(tag);
		
		return item;
	}
	
	public ItemStack createForType(String tubeType)
	{
		return createForType(tubeType, 1);
	}
	
	public String getTubeType(ItemStack item)
	{
		if(item.hasTagCompound())
		{
			String type = item.getTagCompound().getString("tube");
			if(!type.isEmpty())
				return type;
		}
		
		return "basic";
	}
	
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	@SideOnly( Side.CLIENT )
	public void getSubItems( Item item, CreativeTabs tab, List items )
	{
		for(String type : TubeRegistry.instance().getTypeNames())
			items.add(createForType(type));
	}

	@Override
	public boolean areItemsEqual( ItemStack a, ItemStack b )
	{
		String typeA = getTubeType(a);
		if(typeA == null)
			return false;
		
		return typeA.equals(getTubeType(b));
	}
	
}
