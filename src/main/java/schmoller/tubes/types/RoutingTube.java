package schmoller.tubes.types;

import java.util.Arrays;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import schmoller.tubes.ItemFilter;
import schmoller.tubes.ModTubes;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPropertyHolder;
import schmoller.tubes.definitions.TypeRoutingTube;
import schmoller.tubes.routing.GoalRouter;

public class RoutingTube extends BaseTube implements IPropertyHolder
{
	public static final int PROP_COLORSTART = 1;
	public static final int PROP_DIRSTART = 11;
	
	private IFilter[][] mFilters = new IFilter[9][4];
	private int[] mColours = new int[9];
	private RouteDirection[] mDir = new RouteDirection[9]; 
	
	public RoutingTube()
	{
		super("routing");
		Arrays.fill(mDir, RouteDirection.Closed);
		Arrays.fill(mColours, -1);
	}
	
	public void setFilter(int column, int row, IFilter item)
	{
		mFilters[column][row] = item;
		tile().markDirty();
	}
	
	public IFilter getFilter(int column, int row)
	{
		return mFilters[column][row];
	}
	
	@Override
	public <T> T getProperty( int prop )
	{
		if(prop >= PROP_COLORSTART && prop < PROP_COLORSTART + mColours.length)
			return (T)Integer.valueOf(mColours[prop-PROP_COLORSTART]);
		else if(prop >= PROP_DIRSTART && prop < PROP_DIRSTART + mDir.length)
			return (T)mDir[prop-PROP_DIRSTART];

		return null;
	}
	
	@Override
	public <T> void setProperty( int prop, T value )
	{
		if(prop >= PROP_COLORSTART && prop < PROP_COLORSTART + mColours.length)
			mColours[prop-PROP_COLORSTART] = ((Number)value).intValue();
		else if(prop >= PROP_DIRSTART && prop < PROP_DIRSTART + mDir.length)
			mDir[prop-PROP_DIRSTART] = (RouteDirection)value;
		
		tile().markDirty();
	}
	
	private boolean doesItemMatchFilter(int column, TubeItem item)
	{
		boolean empty = true;
		for(int i = 0; i < 4; ++i)
		{
			if(mFilters[column][i] == null)
				continue;

			empty = false;
			if(mFilters[column][i].matches(item, SizeMode.Max))
				return true;
		}
		
		return empty;
	}
	
	@Override
	public boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	public void simulateEffects( TubeItem item )
	{
		int[] matches = new int[9];
		int highest = -1;
		int conns = getConnections();
		
		for(int col = 0; col < 9; ++col)
		{
			if(mDir[col] != RouteDirection.Closed)
			{
				// There must be a connection to consider it
				if (mDir[col] != RouteDirection.Any && ((conns & (1 << mDir[col].ordinal())) == 0))
					continue;
				
				boolean empty = true;
				boolean match = false;
				int level = 0;
				for(int i = 0; i < 4; ++i)
				{
					if(mFilters[col][i] == null)
						continue;
					
					empty = false;
					if(mFilters[col][i].matches(item, SizeMode.Max))
					{
						match = true;
						level = i;
						break;
					}
				}
				
				if(!match && !empty)
					matches[col] = -1;
				else if(empty)
				{
					matches[col] = 0;
					if(mDir[col] != RouteDirection.Any)
						matches[col] += 1;
				}
				else
				{
					matches[col] = (5 - level);
					if(mDir[col] != RouteDirection.Any)
						matches[col] += 1;
				}
				
				if(matches[col] > highest)
					highest = matches[col];
			}
			else
				matches[col] = -1;
		}
	
		int color = -1;
		
		for(int col = 0; col < 9; ++col)
		{
			if(matches[col] == highest && highest != -1)
			{
				if(color == -1)
					color = mColours[col];
				else
				{
					item.colour = -1;
					return;
				}
			}
		}
		
		item.colour = color;
	}
	
	@Override
	public int getRoutableDirections( TubeItem item )
	{
		int allowed = 0;
		int[] matches = new int[9];
		int highest = -1;
		int conns = getConnections();
		
		for(int col = 0; col < 9; ++col)
		{
			if(mDir[col] != RouteDirection.Closed)
			{
				// There must be a connection to consider it
				if (mDir[col] != RouteDirection.Any && ((conns & (1 << mDir[col].ordinal())) == 0))
					continue;
				
				boolean empty = true;
				boolean match = false;
				int level = 0;
				for(int i = 0; i < 4; ++i)
				{
					if(mFilters[col][i] == null)
						continue;
					
					empty = false;
					if(mFilters[col][i].matches(item, SizeMode.Max))
					{
						match = true;
						level = i;
						break;
					}
				}
				
				if(!match && !empty)
					matches[col] = -1;
				else if(empty)
				{
					matches[col] = 0;
					if(mDir[col] != RouteDirection.Any)
						matches[col] += 1;
				}
				else
				{
					matches[col] = (5 - level);
					if(mDir[col] != RouteDirection.Any)
						matches[col] += 1;
				}
				
				if(matches[col] > highest)
					highest = matches[col];
			}
			else
				matches[col] = -1;
		}
	
		for(int col = 0; col < 9; ++col)
		{
			if(matches[col] == highest && highest != -1)
			{
				if(mDir[col] != RouteDirection.Any)
					allowed |= (1 << mDir[col].ordinal());
				else
					allowed = 63;
			}
		}
		
		return allowed;
	}
	
	@Override
	public int onDetermineDestination( TubeItem item )
	{
		int[] matches = new int[9];
		int highest = -1;
		int conns = getConnections();
		
		int fromDir = item.direction;
		
		for(int col = 0; col < 9; ++col)
		{
			if(mDir[col] != RouteDirection.Closed)
			{
				// There must be a connection to consider it
				if (mDir[col] != RouteDirection.Any && ((conns & (1 << mDir[col].ordinal())) == 0))
					continue;
				
				boolean empty = true;
				boolean match = false;
				int level = 0;
				for(int i = 0; i < 4; ++i)
				{
					if(mFilters[col][i] == null)
						continue;
					
					empty = false;
					if(mFilters[col][i].matches(item, SizeMode.Max))
					{
						match = true;
						level = i;
						break;
					}
				}
				
				if(!match && !empty)
					matches[col] = -1;
				else if(empty)
				{
					matches[col] = 0;
					if(mDir[col] != RouteDirection.Any)
						matches[col] += 1;
				}
				else
				{
					matches[col] = (5 - level);
					if(mDir[col] != RouteDirection.Any)
						matches[col] += 1;
				}
				
				if(matches[col] > highest)
					highest = matches[col];
			}
			else
				matches[col] = -1;
		}
	
		int count = 0;
		int[] routes = new int[9];
		int[] routeColours = new int[9];
		int[] routeLength = new int[9];
		
		int smallestDist = Integer.MAX_VALUE;
		int smallCount = 0;
		int[] smallest = new int[9];
		
		
		for(int col = 0; col < 9; ++col)
		{
			if(matches[col] == highest && highest != -1)
			{
				TubeItem copy = item.clone();
				copy.colour = mColours[col];
				
				PathLocation loc;
				
				if(mDir[col] == RouteDirection.Any)
					loc = new GoalRouter(world(), new Position(x(), y(), z()), item, TubesAPI.goalOutput).route();
				else
					loc = new GoalRouter(world(), new Position(x(), y(), z()), item, mDir[col].ordinal(), TubesAPI.goalOutput).route();
				
				if(loc != null)
				{
					routes[count] = loc.initialDir;
					routeLength[count] = loc.dist;
					routeColours[count] = mColours[col];
					
					if(routeLength[count] < smallestDist)
					{
						smallCount = 1;
						smallest[0] = count;
						smallestDist = routeLength[count];
					}
					else if(routeLength[count] == smallestDist)
						smallest[smallCount++] = count;
					
					++count;
				}
				else if(mDir[col] != RouteDirection.Closed)
					fromDir = mDir[col].ordinal();
			}
		}
		
		if(count == 0)
			return fromDir;
		
		int route = smallest[TubeHelper.rand.nextInt(smallCount)];
		
		item.colour = routeColours[route];
		return routes[route];
	}
	
	@Override
	public boolean canPathThrough()
	{
		return true;
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.goal == TubesAPI.goalOverflow)
		{
			int fromDir = item.direction;
			item.lastDirection = item.direction;
			item.direction = TubeHelper.findNextDirection(world(), x(), y(), z(), item);
			if(item.direction == NO_ROUTE)
			{
				fromDir = fromDir ^ 1;
				
				int con = getConnections();
				con &= getRoutableDirections(item);
				
				int total = Integer.bitCount(con);
				if(total == 1)
				{
					item.direction = Integer.numberOfTrailingZeros(con);
					item.updated = true;
					addToClient(item);
					return true;
				}
				else if(total == 0)
				{
					item.direction = 6;
					return true;
				}
				
				int num = TubeHelper.rand.nextInt(total - 1);
				
				int index = 0;
				for(int i = 0; i < 6; ++i)
				{
					if(i != fromDir && (con & (1 << i)) != 0)
					{
						if(num == index)
						{
							item.direction = i;
							item.updated = true;
							addToClient(item);
							return true;
						}
						
						++index;
					}
				}
				
				item.direction = fromDir;
				item.updated = true;
				addToClient(item);
				return true;
			}
			
			addToClient(item);
			
			return true;
		}
		else
			return super.onItemJunction(item);
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		int conns = getConnections();
		
		for(int col = 0; col < 9; ++col)
		{
			if(mDir[col] != RouteDirection.Closed && (mDir[col] == RouteDirection.Any || ((conns & (1 << mDir[col].ordinal())) != 0)) && doesItemMatchFilter(col, item))
				return true;
		}
		
		return false;
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < 9; ++i)
		{
			for(int j = 0; j < 4; ++j)
			{
				int index = j + i*4;
				if(mFilters[i][j] != null)
				{
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("Slot", index);
					FilterRegistry.getInstance().writeFilter(mFilters[i][j], tag);
					list.appendTag(tag);
				}
			}
		}
		
		root.setTag("NewFilter", list);
		
		list = new NBTTagList();
		for(int i = 0; i < 9; ++i)
			list.appendTag(new NBTTagInt(mColours[i]));
		root.setTag("Colours", list);
		
		list = new NBTTagList();
		for(int i = 0; i < 9; ++i)
			list.appendTag(new NBTTagInt(mDir[i].ordinal()));
		root.setTag("Dirs", list);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		NBTTagList colours = root.getTagList("Colours", Constants.NBT.TAG_INT);
		NBTTagList directions = root.getTagList("Dirs", Constants.NBT.TAG_INT);

		if(root.hasKey("Filter"))
		{
			NBTTagList filters = root.getTagList("Filter", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < filters.tagCount(); ++i)
			{
				NBTTagCompound tag = filters.getCompoundTagAt(i);
				
				int row = tag.getInteger("Slot") % 4;
				int column = tag.getInteger("Slot") / 4;
				
				mFilters[column][row] = new ItemFilter(ItemStack.loadItemStackFromNBT(tag), false);
			}
		}
		else
		{
			NBTTagList filters = root.getTagList("NewFilter", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < filters.tagCount(); ++i)
			{
				NBTTagCompound tag = filters.getCompoundTagAt(i);
				
				int row = tag.getInteger("Slot") % 4;
				int column = tag.getInteger("Slot") / 4;
				
				mFilters[column][row] = FilterRegistry.getInstance().readFilter(tag);
			}
		}
		
		for(int i = 0; i < 9; ++i)
			mColours[i] = Integer.valueOf(colours.getStringTagAt(i));
		
		for(int i = 0; i < 9; ++i)
			mDir[i] = RouteDirection.from(Integer.valueOf(directions.getStringTagAt(i)));
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		super.writeDesc(output);
		
		for(int i = 0; i < 9; ++i)
			output.writeShort(mColours[i]);
		
		for(int i = 0; i < 9; ++i)
			output.writeByte(mDir[i].ordinal());
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		super.readDesc(input);
		
		for(int i = 0; i < 9; ++i)
			mColours[i] = input.readShort();
		
		for(int i = 0; i < 9; ++i)
			mDir[i] = RouteDirection.from(input.readByte());
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_ROUTING_TUBE, world(), x(), y(), z());
		return true;
	}
	
	public enum RouteDirection
	{
		Down,
		Up,
		North,
		South,
		West,
		East,
		Any,
		Closed;
		
		public static RouteDirection from(int dir)
		{
			if(dir < 0 || dir > 7)
				return Closed;
			else
				return values()[dir];
		}
		
		@Override
		public String toString()
		{
			if(this == Any || this == Closed)
				return StatCollector.translateToLocal("gui.routingtube.direction." + name());
			
			return TypeRoutingTube.sideColoursText[ordinal()].toString() + StatCollector.translateToLocal("gui.routingtube.direction." + name());
		}
	}
}
