package schmoller.tubes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockTube extends BlockContainer
{
	public static Icon center;
	public static Icon straight;
	
	public BlockTube(int id)
	{
		super(id, Material.glass);
		
		setUnlocalizedName("tubes:tube");
		setCreativeTab(CreativeTabs.tabTransport);
		setLightOpacity(0);
		setStepSound(Block.soundGlassFootstep);
		
		setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void registerIcons( IconRegister register )
	{
		blockIcon = center = register.registerIcon("Tubes:tube-center");
		straight = register.registerIcon("Tubes:tube");
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public Icon getIcon( int side, int meta )
	{
		return blockIcon;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int x, int y, int z )
	{
		return AxisAlignedBB.getAABBPool().getAABB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).offset(x, y, z);
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube( World world, int x, int y, int z )
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return RenderTube.renderId;
	}
	
	@Override
	public boolean onBlockActivated( World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ )
	{
		ItemStack item = player.inventory.getCurrentItem();
		
		if(item == null || item.itemID == 0)
			return false;
		
		TileTube tile = (TileTube)world.getBlockTileEntity(x, y, z);
		tile.addItem(item, 4);
		
		return true;
	}
	
	@Override
	public void onNeighborBlockChange( World world, int x, int y, int z, int id )
	{
		TileTube tile = (TileTube)world.getBlockTileEntity(x, y, z);
		tile.onNeighbourUpdate();
	}
	
	@Override
	public TileEntity createNewTileEntity( World world )
	{
		return new TileTube();
	}
}
