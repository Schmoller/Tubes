package schmoller.tubes.items;

import java.util.List;

import schmoller.tubes.parts.TubeCap;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTubeCap extends JItemMultiPart
{
	public ItemTubeCap(int id)
	{
		super(id);
	}
	
	@Override
	public TMultiPart newPart( ItemStack item, EntityPlayer player, World world, BlockCoord pos, int side, Vector3 hitPos )
	{
		return null;
	}
	
	private int getHitSlot(Vector3 hit, int side)
	{
		int s1 = (side+2)%6;
        int s2 = (side+4)%6;
        double u = hit.copy().add(-0.5, -0.5, -0.5).scalarProject(Rotation.axes[s1]);
        double v = hit.copy().add(-0.5, -0.5, -0.5).scalarProject(Rotation.axes[s2]);
        
        double size = 1/4D;
        
        if(Math.abs(u) < size && Math.abs(v) < size)
            return side^1;
        if(Math.abs(u) > Math.abs(v))
            return (u > 0) ? s1 : s1^1;
        else
            return (v > 0) ? s2 : s2^1;
	}
	
	private TMultiPart create(int slot)
	{
		TubeCap part = (TubeCap)MultiPartRegistry.createPart("tubeCap", false);
		part.setSlot(slot);
		
		return part;
	}
	
	private boolean placeInternal(TileMultipart tile, int slot)
	{
		TMultiPart part = create(slot);
		if(tile.canAddPart(part))
		{
			if(!tile.worldObj.isRemote)
				TileMultipart.addPart(tile.worldObj, new BlockCoord(tile.xCoord, tile.yCoord, tile.zCoord), part);
			
			return true;
		}
		else
			return false;
	}
	
	private boolean placeExternal(World world, BlockCoord pos, int slot)
	{
		TMultiPart part = create(slot);
		if(TileMultipart.canPlacePart(world, pos, part))
		{
			if(!world.isRemote)
				TileMultipart.addPart(world, pos, part);
			
			return true;
		}
		return false;
	}
	
	
	@Override
	public boolean onItemUse( ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
        if(hit != null && hit.typeOfHit == EnumMovingObjectType.TILE)
        {
        	Vector3 vhit = new Vector3(hitX, hitY, hitZ);
        	int slot = getHitSlot(vhit, side);
        	
        	if(slot < 0)
        		return false;
        	
        	BlockCoord pos = new BlockCoord(x, y, z); 
        	
        	TileMultipart tile = TileMultipart.getOrConvertTile(world, pos);
        	double depth = getHitDepth(vhit, side);
        	boolean internal = depth < 1 && tile != null;
        	int oslot = slot ^ 1;
        	
        	
        	boolean useOp = slot == (side ^ 1);
        	
        	if(internal)
        	{
        		if(!useOp)
        		{
        			if(!placeInternal(tile, slot))
        				return false;
        		}
        		else
        		{
	        		if(depth < 0.5)
	        		{
	        			if(!placeInternal(tile, slot))
	        			{
	        				if(!placeInternal(tile, oslot))
	        					return false;
	        			}
	        		}
	        		else
	        		{
	        			if(!placeInternal(tile, oslot))
	    				{
	        				if(!placeExternal(world, pos.copy().offset(side), slot))
	        					return false;
	    				}
	        		}
        		}
        		
        	}
        	else
        	{
        		if(!placeExternal(world, pos.copy().offset(side), slot))
        		{
        			if(!useOp)
        				return false;
        			if(!placeExternal(world, pos.copy().offset(side), oslot))
        				return false;
        		}
        	}
        	
        	
        	if(!player.capabilities.isCreativeMode)
        		item.stackSize -= 1;

        	world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, Block.soundGlassFootstep.getPlaceSound(), (Block.soundGlassFootstep.getVolume() * 5.0F), Block.soundGlassFootstep.getPitch() * .9F);
        	
        	return true;
        }
        
        return false;
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void getSubItems( int itemId, CreativeTabs tab, List items )
	{
		items.add(new ItemStack(itemId, 1, 0));
	}

}
