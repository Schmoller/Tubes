package schmoller.tubes.inventory.providers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityRecordPlayer;
import schmoller.tubes.api.interfaces.IInterfaceProvider;

public class JukeboxProvider implements IInterfaceProvider<IInventory>
{
	@Override
	public IInventory provide( Object object )
	{
		return new JukeboxInventory((TileEntityRecordPlayer)object);
	}
	
	private static class JukeboxInventory implements IInventory
	{
		private TileEntityRecordPlayer mTile;
		
		public JukeboxInventory(TileEntityRecordPlayer tile)
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
			return mTile.func_96097_a();
		}

		@Override
		public ItemStack decrStackSize( int i, int j )
		{
			ItemStack existing = mTile.func_96097_a();
			setInventorySlotContents(0, null);
			return existing;
		}

		@Override
		public ItemStack getStackInSlotOnClosing( int i )
		{
			return mTile.func_96097_a();
		}

		@Override
		public void setInventorySlotContents( int i, ItemStack item )
		{
			if(item != null)
			{
				if(!(item.getItem() instanceof ItemRecord))
					return;
				
				mTile.func_96098_a(item.copy());
                mTile.worldObj.setBlockMetadataWithNotify(mTile.xCoord, mTile.yCoord, mTile.zCoord, 1, 2);
                mTile.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1005, mTile.xCoord, mTile.yCoord, mTile.zCoord, item.itemID);
			}
			else
			{
				ItemStack existing = mTile.func_96097_a();

                if (existing != null)
                {
                    mTile.worldObj.playAuxSFX(1005, mTile.xCoord, mTile.yCoord, mTile.zCoord, 0);
                    mTile.worldObj.playRecord((String)null, mTile.xCoord, mTile.yCoord, mTile.zCoord);
                    mTile.func_96098_a((ItemStack)null);
                    mTile.worldObj.setBlockMetadataWithNotify(mTile.xCoord, mTile.yCoord, mTile.zCoord, 0, 2);
                }
			}
		}

		@Override
		public String getInvName()
		{
			return "Jukebox";
		}

		@Override
		public boolean isInvNameLocalized()
		{
			return true;
		}

		@Override
		public int getInventoryStackLimit()
		{
			return 1;
		}

		@Override
		public void onInventoryChanged()
		{
		}

		@Override
		public boolean isUseableByPlayer( EntityPlayer entityplayer )
		{
			return false;
		}

		@Override
		public void openChest() {}

		@Override
		public void closeChest() {}

		@Override
		public boolean isItemValidForSlot( int i, ItemStack item )
		{
			return (item.getItem() instanceof ItemRecord);
		}
		
	}

}
