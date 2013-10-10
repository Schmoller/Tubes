package schmoller.tubes.logic;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import schmoller.tubes.CommonHelper;
import schmoller.tubes.IDirectionalTube;
import schmoller.tubes.IRedstoneTube;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ITubeImportDest;
import schmoller.tubes.ModTubes;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;
import schmoller.tubes.inventory.InventoryHelper;
import schmoller.tubes.routing.BaseRouter.PathLocation;
import schmoller.tubes.routing.ImportSourceFinder;

public class RequestingTubeLogic extends TubeLogic implements ITubeImportDest, IRedstoneTube
{
	private ItemStack[] mFilter = new ItemStack[16];
	private int mNext = 0;
	private PullMode mMode = PullMode.RedstoneConstant;
	
	private int mPulses = 0;
	private boolean mIsPowered;
	
	public RequestingTubeLogic(ITube tube)
	{
		super(tube);
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return true;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		if(con instanceof ITube)
			return !(((ITube)con).getLogic() instanceof RequestingTubeLogic);

		return true;
	}
	
	@Override
	public int getConnectableMask()
	{
		int dir = ((IDirectionalTube)mTube).getFacing();

		return (1 << dir) | (1 << (dir ^ 1));
	}
	
	@Override
	public int getTickRate()
	{
		return 20;
	}
	
	@Override
	public boolean canPathThrough()
	{
		return false;
	}
	
	@Override
	public boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	public int onDetermineDestination( TubeItem item )
	{
		if(item.state != TubeItem.IMPORT)
			return item.direction ^ 1;
		
		item.state = TubeItem.NORMAL;
		
		return ((IDirectionalTube)mTube).getFacing() ^ 1;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item, int side )
	{
		return item.state == TubeItem.IMPORT;
	}
	
	public ItemStack getFilter(int slot)
	{
		return mFilter[slot];
	}
	
	public void setFilter(int slot, ItemStack item)
	{
		mFilter[slot] = item;
	}
	
	public PullMode getMode()
	{
		return mMode;
	}
	
	public void setMode(PullMode mode)
	{
		mMode = mode;
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		mMode = PullMode.values()[input.readByte()];
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		output.writeByte(mMode.ordinal());
	}
	
	@Override
	public boolean canImportFromSide( int side )
	{
		return side == ((IDirectionalTube)mTube).getFacing();
	}
	
	
	
	@Override
	public void onTick()
	{
		if(mMode == PullMode.Constant || (mMode == PullMode.RedstoneConstant && mIsPowered) || (mMode == PullMode.RedstoneSingle && mPulses > 0))
		{
			ItemStack filterItem = null;
			int start = mNext;
			do
			{
				filterItem = mFilter[mNext++];
				if(mNext >= 16)
					mNext = 0;
			}
			while(filterItem == null && mNext != start);
			
			PathLocation source = new ImportSourceFinder(mTube.world(), new ChunkPosition(mTube.x(), mTube.y(), mTube.z()), ((IDirectionalTube)mTube).getFacing(), filterItem).route();
			
			if(source != null)
			{
				ItemStack extracted = InventoryHelper.extractItem(mTube.world(), source.position.x, source.position.y, source.position.z, source.dir, filterItem);
				if(extracted != null)
				{
					TubeItem item = new TubeItem(extracted);
					item.state = TubeItem.IMPORT;
					item.direction = source.dir ^ 1;
					
					PathLocation tubeLoc = new PathLocation(source, source.dir ^ 1);
					TileEntity tile = CommonHelper.getTileEntity(mTube.world(), tubeLoc.position);
					ITubeConnectable con = TubeHelper.getTubeConnectable(tile);
					if(con != null)
						con.addItem(item, true);
					
					--mPulses;
				}
			}
		}
	}
	
	@Override
	public boolean onActivate( EntityPlayer player )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_REQUESTING_TUBE, mTube.world(), mTube.x(), mTube.y(), mTube.z());
		return true;
	}
	
	@Override
	public void onSave( NBTTagCompound root )
	{
		NBTTagList filter = new NBTTagList();
		for(int i = 0; i < 16; ++i)
		{
			if(mFilter[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				mFilter[i].writeToNBT(tag);
				filter.appendTag(tag);
			}
		}

		root.setTag("Filter", filter);
		
		root.setString("PullMode", mMode.name());
		root.setInteger("Pulses", mPulses);
	}
	
	@Override
	public void onLoad( NBTTagCompound root )
	{
		NBTTagList filter = root.getTagList("Filter");
		
		if(filter == null)
			return;
		
		for(int i = 0; i < filter.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)filter.tagAt(i);
			
			int slot = tag.getInteger("Slot");
			mFilter[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
		
		mMode = PullMode.valueOf(root.getString("PullMode"));
		
		mPulses = root.getInteger("Pulses");
	}
	
	public ITube getTube()
	{
		return mTube;
	}

	@Override
	public void onPowerChange( int level )
	{
		if(!mIsPowered && level > 0 && mMode == PullMode.RedstoneSingle)
			++mPulses;

		mIsPowered = level > 0;
	}
	
	@Override
	public void onLoadPower( int level )
	{
		mIsPowered = (level > 0);
	}

	@Override
	public int weakPower()
	{
		return 0;
	}

	@Override
	public int strongPower()
	{
		return 0;
	}
}
