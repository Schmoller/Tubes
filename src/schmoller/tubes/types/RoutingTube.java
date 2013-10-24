package schmoller.tubes.types;

import java.util.Arrays;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import schmoller.tubes.ModTubes;
import schmoller.tubes.Position;
import schmoller.tubes.TubeHelper;
import schmoller.tubes.TubeItem;
import schmoller.tubes.definitions.TypeRoutingTube;
import schmoller.tubes.inventory.InventoryHelper;
import schmoller.tubes.routing.OutputRouter;
import schmoller.tubes.routing.BaseRouter.PathLocation;

public class RoutingTube extends BaseTube
{
	private ItemStack[][] mFilters = new ItemStack[9][4];
	private int[] mColours = new int[9];
	private RouteDirection[] mDir = new RouteDirection[9]; 
	
	public RoutingTube()
	{
		super("routing");
		Arrays.fill(mDir, RouteDirection.Closed);
		Arrays.fill(mColours, -1);
	}
	
	public void setFilter(int column, int row, ItemStack item)
	{
		mFilters[column][row] = item;
	}
	
	public ItemStack getFilter(int column, int row)
	{
		return mFilters[column][row];
	}
	
	public void setColour(int column, int colour)
	{
		mColours[column] = colour;
	}
	
	public int getColour(int column)
	{
		return mColours[column];
	}
	
	public void setDirection(int column, RouteDirection direction)
	{
		mDir[column] = direction;
	}
	
	public RouteDirection getDirection(int column)
	{
		return mDir[column];
	}
	
	private boolean doesItemMatchFilter(int column, ItemStack item)
	{
		boolean empty = true;
		for(int i = 0; i < 4; ++i)
		{
			if(mFilters[column][i] == null)
				continue;

			empty = false;
			if(InventoryHelper.areItemsEqual(mFilters[column][i], item))
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
	public boolean simulateEffects( TubeItem item )
	{
		item.direction = onDetermineDestination(item);
		
		return true;
	}
	
	@Override
	public int onDetermineDestination( TubeItem item )
	{
		int[] matches = new int[9];
		int highest = -1;
		
		for(int col = 0; col < 9; ++col)
		{
			if(mDir[col] != RouteDirection.Closed)
			{
				boolean empty = true;
				boolean match = false;
				int level = 0;
				for(int i = 0; i < 4; ++i)
				{
					if(mFilters[col][i] == null)
						continue;
					
					empty = false;
					if(InventoryHelper.areItemsEqual(mFilters[col][i], item.item))
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
					loc = new OutputRouter(world(), new Position(x(), y(), z()), item).route();
				else
					loc = new OutputRouter(world(), new Position(x(), y(), z()), item, mDir[col].ordinal()).route();
				
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
			}
		}
		
		if(count == 0)
			return -1;
		
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
	public boolean canItemEnter( TubeItem item )
	{
		for(int col = 0; col < 9; ++col)
		{
			if(mDir[col] != RouteDirection.Closed && doesItemMatchFilter(col, item.item))
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
					mFilters[i][j].writeToNBT(tag);
					list.appendTag(tag);
				}
			}
		}
		
		root.setTag("Filter", list);
		
		list = new NBTTagList();
		for(int i = 0; i < 9; ++i)
			list.appendTag(new NBTTagInt("", mColours[i]));
		root.setTag("Colours", list);
		
		list = new NBTTagList();
		for(int i = 0; i < 9; ++i)
			list.appendTag(new NBTTagInt("", mDir[i].ordinal()));
		root.setTag("Dirs", list);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		NBTTagList filters = root.getTagList("Filter");
		NBTTagList colours = root.getTagList("Colours");
		NBTTagList directions = root.getTagList("Dirs");
		
		for(int i = 0; i < filters.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)filters.tagAt(i);
			
			int row = tag.getInteger("Slot") % 4;
			int column = tag.getInteger("Slot") / 4;
			
			mFilters[column][row] = ItemStack.loadItemStackFromNBT(tag);
		}
		
		for(int i = 0; i < 9; ++i)
			mColours[i] = ((NBTTagInt)colours.tagAt(i)).data;
		
		for(int i = 0; i < 9; ++i)
			mDir[i] = RouteDirection.from(((NBTTagInt)directions.tagAt(i)).data);
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
				return name();
			
			return TypeRoutingTube.sideColoursText[ordinal()].toString() + name();
		}
	}
}
