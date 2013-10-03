package schmoller.tubes.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.InventoryHelper;
import schmoller.tubes.ModTubes;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;
import schmoller.tubes.TubeRegistry;
import schmoller.tubes.definitions.TubeDefinition;
import schmoller.tubes.logic.TubeLogic;
import schmoller.tubes.render.RenderHelper;
import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import codechicken.core.lighting.LazyLightMatrix;
import codechicken.core.vec.Cuboid6;
import codechicken.core.vec.Vector3;
import codechicken.microblock.HollowMicroblock;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.JCuboidPart;
import codechicken.multipart.JIconHitEffects;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TFacePart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TSlottedPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.scalatraits.TSlottedTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BaseTubePart extends JCuboidPart implements ITube, JNormalOcclusion, JIconHitEffects, TSlottedPart
{
	private LinkedList<TubeItem> mItemsInTransit = new LinkedList<TubeItem>();
	private boolean mIsUpdating = false;
	private LinkedList<TubeItem> mWaitingToAdd = new LinkedList<TubeItem>();

	public static int transitTime = 1000;
	public static int blockedWaitTime = 10;
	
	private TubeLogic mLogic;
	private TubeDefinition mDef;
	private String mType;
	
	private static final int NO_ROUTE = -1;
	private static final int ROUTE_TERM = -2;
	
	public BaseTubePart(String type)
	{
		mDef = TubeRegistry.instance().getDefinition(type);
		mLogic = mDef.getTubeLogic(this);
		mType = type;
	}
	
	@Override
	public String getType()
	{
		return "tubes_" + mType;
	}

	@Override
	public Cuboid6 getBounds()
	{
		return mDef.getSize();
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
		return ModTubes.itemTube.createForType(mType);
	}
	
	@Override
	public Iterable<ItemStack> getDrops()
	{
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(mItemsInTransit.size());
		for(TubeItem item : mItemsInTransit)
			stacks.add(item.item);
		
		stacks.add(ModTubes.itemTube.createForType(mType));
		
		return stacks;
	}
	
	public TubeLogic getLogic()
	{
		return mLogic;
	}
	
	@Override
	public boolean addItem(ItemStack item, int fromDir)
	{
		assert(fromDir >= -1 && fromDir < 6);
		
		
		TubeItem tItem = new TubeItem(item);
		if(fromDir == -1)
		{
			tItem.direction = 6;
			tItem.progress = 0.5f;
		}
		else
		{
			tItem.direction = fromDir;
			tItem.progress = 0;
		}
		
		mLogic.onItemEnter(tItem);
		
		if(!world().isRemote)
		{
			if(mIsUpdating)
				mWaitingToAdd.add(tItem);
			else
				mItemsInTransit.add(tItem);
			
			if(tItem.direction != 6)
				addToClient(tItem);
		}
		
		return true;
	}
	
	@Override
	public boolean addItem(TubeItem item)
	{
		return addItem(item, false);
	}
	
	@Override
	public boolean addItem(TubeItem item, boolean syncToClient)
	{
		assert(item.direction >= -1 && item.direction <= 6);
		
		mLogic.onItemEnter(item);
		if(mIsUpdating)
			mWaitingToAdd.add(item);
		else
			mItemsInTransit.add(item);
		
		if(syncToClient)
			addToClient(item);
		
		return true;
	}
	
	@Override
	public int getConnectionClass()
	{
		return mLogic.getConnectionClass();
	}
	
	@Override
	public boolean canAddItem( TubeItem item )
	{
		return mLogic.canItemEnter(item, (item.direction == 6 ? 6 : item.direction^1));
	}
	
	@Override
	public boolean canPathThrough()
	{
		return mLogic.canPathThrough();
	}
	
	@Override
	public int getConnections()
	{
		return TubeHelper.getConnectivity(world(), x(), y(), z());
	}
	
	private int getNumConnections()
	{
		int count = 0;
		int con = getConnections();
		for(int i = 0; i < 6; ++i)
		{
			if((con & (1 << i)) != 0)
				++count;
		}
		
		return count;
	}
	
	@Override
	public int getConnectableMask()
	{
		int con = mLogic.getConnectableMask();
		
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
	
	private boolean mDidRoute = false;
	private int getNextDirection(TubeItem item)
	{
		int count = 0;
		int dir = NO_ROUTE;
		
		mDidRoute = false;
		
		if(mLogic.hasCustomRouting())
		{
			if(world().isRemote)
				return ROUTE_TERM;
			
			mDidRoute = true;
			return mLogic.onDetermineDestination(item);
		}
		
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
				return ROUTE_TERM;
			
			if(count > 1)
			{
				dir = TubeHelper.findNextDirection(world(), x(), y(), z(), item);
				
				mDidRoute = true;
			}
		}
		
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
	
	private void addToClient(TubeItem item)
	{
		if(world().isRemote)
			return;
		
		item.write(tile().getWriteStream(this).writeByte(0));
	}
	
	private int randDirection(int fromDir)
	{
		fromDir = fromDir ^ 1;
		int total = getNumConnections();
		if(total <= 1)
			return fromDir;
		
		
		int num = TubeHelper.rand.nextInt(total - 1);
		
		int index = 0;
		int con = getConnections();
		for(int i = 0; i < 6; ++i)
		{
			if(i != fromDir && (con & (1 << i)) != 0)
			{
				if(num == index)
					return i;
				
				++index;
			}
		}
		
		return fromDir;
	}
	
	private boolean handleJunction(TubeItem item)
	{
		int lastDir = item.direction;
		item.direction = getNextDirection(item);
		item.updated = true;
		if(item.direction == NO_ROUTE)
		{
			if(getNumConnections() == 1)
				item.direction = lastDir ^ 1;
			else
			{
				//item.state = TubeItem.NO_PATH;
//				item.direction = getNextDirection(item);
//				if(item.direction == NO_ROUTE)
					item.direction = randDirection(lastDir);
				
				addToClient(item); // Client will have deleted it
			}
		}
		else if(item.direction == ROUTE_TERM)
			return false;
		// Synch the new direction to client
		else if(!world().isRemote && mDidRoute)
			addToClient(item);
		
		return true;
	}
	
	@Override
	public void update()
	{
		mIsUpdating = true;
		Iterator<TubeItem> it = mItemsInTransit.iterator();
		
		while(it.hasNext())
		{
			TubeItem item = it.next();
			
			if(item.direction == 6) // It needs a path right away
			{
				if(!handleJunction(item))
				{
					it.remove();
					continue;
				}
				else
					addToClient(item);
			}
			
			item.progress += 0.1;
			
			if(!item.updated && item.progress >= 0.5)
			{
				// Find new direction to go
				if(!handleJunction(item))
				{
					it.remove();
					continue;
				}
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
						addToClient(item);
				}
			}
		}
		
		mItemsInTransit.addAll(mWaitingToAdd);
		mWaitingToAdd.clear();
		
		mIsUpdating = false;
	}
	
	
	public List<TubeItem> getItems()
	{
		return mItemsInTransit;
	}
	
	private boolean transferToNext(TubeItem item)
	{
		if(item.direction == 6)
			return false;
		
		ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
		
		TileEntity ent = world().getBlockTileEntity(x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		if(con != null)
		{
			item.progress -= 1;
			item.updated = false;
			
			return con.addItem(item);
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
		return getLogic().getRouteWeight();
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
		
		mLogic.onSave(root);
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
		
		mLogic.onLoad(root);
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		packet.writeShort(mItemsInTransit.size());
		for(TubeItem item : mItemsInTransit)
			item.write(packet);
		
		mLogic.writeDesc(packet);
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		int count = packet.readShort() & 0xFFFF;
		
		mItemsInTransit.clear();
		
		for(int i = 0; i < count; ++i)
			mItemsInTransit.add(TubeItem.read(packet));
		
		mLogic.readDesc(packet);
	}
	
	@Override
	public void read( MCDataInput packet )
	{
		int id = packet.readByte();
		switch(id)
		{
		case 0:
			mItemsInTransit.add(TubeItem.read(packet));
			break;
		}
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void renderDynamic( Vector3 pos, float frame, int pass )
	{
		if(pass == 0)
			RenderHelper.renderDynamic(this, mDef, pos);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void renderStatic(Vector3 pos, LazyLightMatrix olm, int pass) 
	{
		RenderHelper.renderStatic(this, mDef);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void addHitEffects( MovingObjectPosition hit, EffectRenderer effectRenderer )
	{
		IconHitEffects.addHitEffects(this, hit, effectRenderer);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void addDestroyEffects( EffectRenderer effectRenderer )
	{
		IconHitEffects.addDestroyEffects(this, effectRenderer);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public Icon getBreakingIcon( Object subPart, int side )
	{
		return getBrokenIcon(side);
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public Icon getBrokenIcon( int side )
	{
		return mDef.getCenterIcon();
	}

	@Override
	public int getSlotMask()
	{
		return 1 << 6;
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		return mLogic.onActivate(player);
	}
	
	@Override
	public void updateState()
	{
		sendDescUpdate();
	}
	
}
