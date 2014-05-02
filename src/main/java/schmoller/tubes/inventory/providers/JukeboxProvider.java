package schmoller.tubes.inventory.providers;

import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import schmoller.tubes.api.interfaces.IInterfaceProvider;

public class JukeboxProvider implements IInterfaceProvider<IInventory>
{
	@Override
	public IInventory provide( Object object )
	{
		return new JukeboxInventory((TileEntityJukebox)object);
	}
	
	private static class JukeboxInventory implements IInventory
	{
		private TileEntityJukebox mTile;
		
		public JukeboxInventory(TileEntityJukebox tile)
		{
			mTile = tile;
		}
		
		@Override
		public int getSizeInventory()
		{
			return 1;
		}

		@Override
		public ItemStack getStackInSlot( int i )
		{
			return mTile.func_145856_a();
		}

		@Override
		public ItemStack decrStackSize( int i, int j )
		{
			ItemStack existing = mTile.func_145856_a();
			setInventorySlotContents(0, null);
			return existing;
		}

		@Override
		public ItemStack getStackInSlotOnClosing( int i )
		{
			return mTile.func_145856_a();
		}

		@Override
		public void setInventorySlotContents( int i, ItemStack item )
		{
			if(item != null)
			{
				if(!(item.getItem() instanceof ItemRecord))
					return;
				
				mTile.func_145857_a(item.copy());
                mTile.getWorldObj().setBlockMetadataWithNotify(mTile.xCoord, mTile.yCoord, mTile.zCoord, 1, 2);
                mTile.getWorldObj().playAuxSFXAtEntity((EntityPlayer)null, 1005, mTile.xCoord, mTile.yCoord, mTile.zCoord, Item.getIdFromItem(item.getItem()));
			}
			else
			{
				ItemStack existing = mTile.func_145856_a();

                if (existing != null)
                {
                    mTile.getWorldObj().playAuxSFX(1005, mTile.xCoord, mTile.yCoord, mTile.zCoord, 0);
                    mTile.getWorldObj().playRecord((String)null, mTile.xCoord, mTile.yCoord, mTile.zCoord);
                    mTile.func_145857_a((ItemStack)null);
                    mTile.getWorldObj().setBlockMetadataWithNotify(mTile.xCoord, mTile.yCoord, mTile.zCoord, 0, 2);
                }
			}
		}

		@Override
		public String getInventoryName()
		{
			return "Jukebox";
		}

		@Override
		public boolean hasCustomInventoryName()
		{
			return true;
		}

		@Override
		public int getInventoryStackLimit()
		{
			return 1;
		}

		@Override
		public void markDirty()
		{
		}

		@Override
		public boolean isUseableByPlayer( EntityPlayer entityplayer )
		{
			return false;
		}

		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}

		@Override
		public boolean isItemValidForSlot( int i, ItemStack item )
		{
			return (item.getItem() instanceof ItemRecord);
		}
		
	}

}
