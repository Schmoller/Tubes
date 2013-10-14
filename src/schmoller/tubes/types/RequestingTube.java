package schmoller.tubes.types;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import schmoller.tubes.CommonHelper;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ITubeImportDest;
import schmoller.tubes.ModTubes;
import schmoller.tubes.PullMode;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;
import schmoller.tubes.inventory.InventoryHelper;
import schmoller.tubes.routing.ImportSourceFinder;
import schmoller.tubes.routing.BaseRouter.PathLocation;

public class RequestingTube extends DirectionalTube implements ITubeImportDest, IRedstonePart
{
	private ItemStack[] mFilter = new ItemStack[16];
	private int mNext = 0;
	private PullMode mMode = PullMode.RedstoneConstant;
	
	private int mPulses = 0;
	private boolean mIsPowered;
	
	public RequestingTube()
	{
		super("requesting");
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return !(con instanceof RequestingTube);
	}
	
	@Override
	protected int getConnectableSides()
	{
		int dir = getFacing();

		return (1 << dir) | (1 << (dir ^ 1));
	}
	
	@Override
	public int getTickRate()
	{
		return 20;
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
			
			PathLocation source = new ImportSourceFinder(world(), new ChunkPosition(x(), y(), z()), getFacing(), filterItem).route();
			
			if(source != null)
			{
				ItemStack extracted = InventoryHelper.extractItem(world(), source.position.x, source.position.y, source.position.z, source.dir, filterItem);
				if(extracted != null)
				{
					TubeItem item = new TubeItem(extracted);
					item.state = TubeItem.IMPORT;
					item.direction = source.dir ^ 1;
					
					PathLocation tubeLoc = new PathLocation(source, source.dir ^ 1);
					TileEntity tile = CommonHelper.getTileEntity(world(), tubeLoc.position);
					ITubeConnectable con = TubeHelper.getTubeConnectable(tile);
					if(con != null)
						con.addItem(item, true);
					
					--mPulses;
				}
			}
		}
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
		
		return getFacing() ^ 1;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		return item.state == TubeItem.IMPORT;
	}
	
	private int getPower()
	{
		int current = 0;
		for(int side = 0; side < 6; ++side)
			current = Math.max(current, RedstoneInteractions.getPowerTo(world(), x(), y(), z(), side, 0x1f));
		
		return current;
	}
	
	@Override
	public void onWorldJoin()
	{
		mIsPowered = getPower() > 0;
	}
	
	@Override
	public void update()
	{
		boolean state = getPower() > 0;
		
		if(!mIsPowered && state && mMode == PullMode.RedstoneSingle)
			++mPulses;

		mIsPowered = state;
		
		super.update();
	}
	
	@Override
	public boolean canConnectRedstone( int side ) { return true; }

	@Override
	public int strongPowerLevel( int side ) { return 0; }

	@Override
	public int weakPowerLevel( int side ) { return 0; }

	
	@Override
	public boolean canImportFromSide( int side )
	{
		return side == getFacing();
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
		super.readDesc(input);
		mMode = PullMode.values()[input.readByte()];
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		super.writeDesc(output);
		output.writeByte(mMode.ordinal());
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
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
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
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
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_REQUESTING_TUBE, world(), x(), y(), z());
		return true;
	}
}
