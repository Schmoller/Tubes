package schmoller.tubes.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.InventoryHelper;
import schmoller.tubes.ModTubes;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;
import schmoller.tubes.network.packets.ModPacketAddItem;
import schmoller.tubes.render.RenderTubePart;
import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import codechicken.core.lighting.LazyLightMatrix;
import codechicken.core.vec.Cuboid6;
import codechicken.core.vec.Vector3;
import codechicken.microblock.HollowMicroblock;
import codechicken.microblock.Microblock;
import codechicken.multipart.JCuboidPart;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TFacePart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.scalatraits.TSlottedTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BaseTubePart extends JCuboidPart implements ITube, JNormalOcclusion
{
	private LinkedList<TubeItem> mItemsInTransit = new LinkedList<TubeItem>();

	public static int transitTime = 1000;
	
	@Override
	public String getType()
	{
		return "schmoller_tube";
	}

	@Override
	public Cuboid6 getBounds()
	{
		return new Cuboid6(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
	}

	@Override
	public boolean occlusionTest( TMultiPart npart )
	{
		return NormalOcclusionTest.apply(this, npart);
	}
	
	@Override
	public Iterable<Cuboid6> getOcclusionBoxes()
	{
		ArrayList<Cuboid6> boxes = new ArrayList<Cuboid6>();
		boxes.add(getBounds());
		return boxes;
	}
	
	@Override
	public ItemStack pickItem( MovingObjectPosition hit )
	{
		// TODO Auto-generated method stub
		return super.pickItem(hit);
	}
	
	@Override
	public Iterable<ItemStack> getDrops()
	{
		ArrayList<ItemStack> stacks = new ArrayList<>(mItemsInTransit.size());
		for(TubeItem item : mItemsInTransit)
			stacks.add(item.item);
		
		return stacks;
	}
	
	@Override
	public boolean addItem(ItemStack item, int fromDir)
	{
		TubeItem tItem = new TubeItem(item);
		tItem.direction = fromDir;
		tItem.progress = 0;
		
		if(!world().isRemote)
			mItemsInTransit.add(tItem);
		else
			ModTubes.packetManager.sendPacketForBlock(new ModPacketAddItem(x(), y(), z(), tItem), world());
		
		return true;
	}
	
	@Override
	public boolean addItem(TubeItem item)
	{
		mItemsInTransit.add(item);
		return true;
	}
	
	@Override
	public boolean canAddItem( TubeItem item )
	{
		return true;
	}
	
	@Override
	public boolean canPathThrough()
	{
		return true;
	}
	
	public int getConnections()
	{
		return TubeHelper.getConnectivity(world(), x(), y(), z());
	}
	
	@Override
	public int getConnectableMask()
	{
		int con = 63;
		
		if (tile() instanceof TSlottedTile)
		{
			for(int i = 0; i < 6; ++i)
			{
				TMultiPart part = tile().partMap(i);
				if(part instanceof TFacePart && !(part instanceof HollowMicroblock))
					con -= (con & (1 << i));
			}
		}
		return con;
	}
	
	private int getNextDirection(TubeItem item)
	{
		int count = 0;
		int dir = -1;
		
		int conns = getConnections();
		
		conns -= (conns & (1 << (item.direction ^ 1)));
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				++count;
				dir = i;
			}
		}
		
		if(count > 1)
		{
			if(world().isRemote)
				return -1;
			
			if(count > 1)
				dir = TubeHelper.findNextDirection(world(), x(), y(), z(), item);
			
			if(dir == -1)
				dir = item.direction ^ 1;
			
			int l = item.direction;
			item.direction = dir;
			item.updated = true;
			ModTubes.packetManager.sendPacketForBlock(new ModPacketAddItem(x(), y(), z(), item), world());
			item.updated = false;
			item.direction = l;
		}
		else if(dir == -1)
			dir = item.direction ^ 1;
			
		
		return dir;
	}
	
	@Override
	public void onPartChanged()
	{
//		world().loadedTileEntityList.remove(tile());
//		world().addTileEntity(tile());
	}
	
	@Override
	public void bind( TileMultipart t )
	{
		if(tile() != null && tile() != t)
		{
			world().loadedTileEntityList.remove(tile());
			world().addTileEntity(t);
		}
		
		super.bind(t);
	}
	
	@Override
	public boolean doesTick()
	{
		return true;
	}
	@Override
	public void update()
	{
		Iterator<TubeItem> it = mItemsInTransit.iterator();
		
		while(it.hasNext())
		{
			TubeItem item = it.next();
			item.progress += 0.1;
			
			if(!item.updated && item.progress >= 0.5)
			{
				// Find new direction to go
				item.direction = getNextDirection(item);
				if(item.direction == -1)
				{
					it.remove();
					continue;
				}
				
				item.updated = true;
			}
			
			if(item.progress >= 1)
			{
				if(transferToNext(item))
					it.remove();
				else
				{
					item.progress -= 1;
					item.updated = false;
					item.direction ^= 1;
					
					if(!world().isRemote)
						ModTubes.packetManager.sendPacketForBlock(new ModPacketAddItem(x(), y(), z(), item), world());
				}
			}
		}
	}
	
	public List<TubeItem> getItems()
	{
		return mItemsInTransit;
	}
	
	private boolean transferToNext(TubeItem item)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
		
		TileEntity ent = world().getBlockTileEntity(x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		if(con != null)
		{
			item.progress -= 1;
			item.updated = false;
			
			if(con.addItem(item))
				return true;
		}
		
		if(world().isRemote)
			return true;
		
		if(ent != null && InventoryHelper.canAcceptItem(item.item, world(), ent.xCoord, ent.yCoord, ent.zCoord, item.direction))
		{
			InventoryHelper.insertItem(item.item, world(), ent.xCoord, ent.yCoord, ent.zCoord, item.direction);
			
			if(item.item.stackSize == 0)
				return true;
		}
		
		return false;
	}

	
	@Override
	public int getRouteWeight()
	{
		return 1;
	}

	@Override
	public boolean isBlocked()
	{
		return false;
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		NBTTagList list = new NBTTagList();
		for(TubeItem item : mItemsInTransit)
		{
			NBTTagCompound tag = new NBTTagCompound();
			item.writeToNBT(tag);
			list.appendTag(tag);
		}
		
		root.setTag("items", list);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		mItemsInTransit.clear();
		
		NBTTagList list = root.getTagList("items");
		
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)list.tagAt(i);
			
			mItemsInTransit.add(TubeItem.readFromNBT(tag));
		}
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		packet.writeShort(mItemsInTransit.size());
		for(TubeItem item : mItemsInTransit)
		{
			packet.writeByte(item.direction | (item.updated ? 128 : 0));
			packet.writeFloat(item.progress);
			packet.writeItemStack(item.item);
		}
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		int count = packet.readShort() & 0xFFFF;
		
		mItemsInTransit.clear();
		
		for(int i = 0; i < count; ++i)
		{
			int direction = packet.readByte() & 0xFF;
			boolean updated = (direction & 128) != 0;
			direction -= (direction & 128);
			
			float progress = packet.readFloat();
			TubeItem item = new TubeItem(packet.readItemStack());
			item.direction = direction;
			item.updated = updated;
			item.progress = progress;
			mItemsInTransit.add(item);
		}
	}
	
	
	@Override
	@SideOnly( Side.CLIENT )
	public void renderDynamic( Vector3 pos, float frame, int pass )
	{
		RenderTubePart.instance().renderDynamic(this, pos);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void renderStatic(Vector3 pos, LazyLightMatrix olm, int pass) 
	{
		RenderTubePart.instance().renderStatic(this);
	}
}
