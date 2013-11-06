package schmoller.tubes.parts;

import java.util.List;

import schmoller.tubes.SpecialShapedRecipe.ISpecialItemCompare;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.interfaces.IDirectionalTube;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
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

	public ItemTubeBase( int id )
	{
		super(id);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabTransport);
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void registerIcons( IconRegister register )
	{
		itemIcon = register.registerIcon("missing");
	}
	
	@Override
	public boolean onItemUse( ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		if(super.onItemUse(item, player, world, x, y, z, side, hitX, hitY, hitZ))
		{
			world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, Block.soundGlassFootstep.getPlaceSound(), (Block.soundGlassFootstep.getVolume() * 5.0F), Block.soundGlassFootstep.getPitch() * .9F);
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
		
		NBTTagCompound tag = new NBTTagCompound("tag");
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
	public void getSubItems( int id, CreativeTabs tab, List items )
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
