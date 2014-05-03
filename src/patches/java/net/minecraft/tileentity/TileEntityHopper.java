package net.minecraft.tileentity;

import java.util.List;

import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.inventory.BasicInvHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntity implements IHopper, ITubeConnectable
{
    private ItemStack[] field_145900_a = new ItemStack[5];
    private String field_145902_i;
    private int field_145901_j = -1;
    private static final String __OBFID = "CL_00000359";

    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        super.readFromNBT(p_145839_1_);
        NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
        this.field_145900_a = new ItemStack[this.getSizeInventory()];

        if (p_145839_1_.hasKey("CustomName", 8))
        {
            this.field_145902_i = p_145839_1_.getString("CustomName");
        }

        this.field_145901_j = p_145839_1_.getInteger("TransferCooldown");

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.field_145900_a.length)
            {
                this.field_145900_a[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        super.writeToNBT(p_145841_1_);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_145900_a.length; ++i)
        {
            if (this.field_145900_a[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.field_145900_a[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        p_145841_1_.setTag("Items", nbttaglist);
        p_145841_1_.setInteger("TransferCooldown", this.field_145901_j);

        if (this.hasCustomInventoryName())
        {
            p_145841_1_.setString("CustomName", this.field_145902_i);
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        super.markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.field_145900_a.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.field_145900_a[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.field_145900_a[par1] != null)
        {
            ItemStack itemstack;

            if (this.field_145900_a[par1].stackSize <= par2)
            {
                itemstack = this.field_145900_a[par1];
                this.field_145900_a[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.field_145900_a[par1].splitStack(par2);

                if (this.field_145900_a[par1].stackSize == 0)
                {
                    this.field_145900_a[par1] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.field_145900_a[par1] != null)
        {
            ItemStack itemstack = this.field_145900_a[par1];
            this.field_145900_a[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.field_145900_a[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.field_145902_i : "container.hopper";
    }

    /**
     * Returns if the inventory is named
     */
    public boolean hasCustomInventoryName()
    {
        return this.field_145902_i != null && this.field_145902_i.length() > 0;
    }

    public void func_145886_a(String p_145886_1_)
    {
        this.field_145902_i = p_145886_1_;
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openInventory() {}

    public void closeInventory() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }

    public void updateEntity()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            --this.field_145901_j;

            if (!this.func_145888_j())
            {
                this.func_145896_c(0);
                this.func_145887_i();
            }
        }
    }

    public boolean func_145887_i() // updateHopper
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            if (!this.func_145888_j() && BlockHopper.func_149917_c(this.getBlockMetadata()))
            {
                boolean flag = insertItemToTube();
                
                if(!flag)
                    flag = this.func_145883_k(); // Insert item to inventory
                flag = func_145891_a(this) || flag; // SuckItemsIntoHopper

                if (flag)
                {
                    this.func_145896_c(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        }
        else
        {
            return false;
        }
    }
    
    private boolean func_145883_k()
    {
        IInventory iinventory = this.func_145895_l();

        if (iinventory == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < this.getSizeInventory(); ++i)
            {
                if (this.getStackInSlot(i) != null)
                {
                    ItemStack itemstack = this.getStackInSlot(i).copy();
                    ItemStack itemstack1 = func_145889_a(iinventory, this.decrStackSize(i, 1), Facing.oppositeSide[BlockHopper.getDirectionFromMetadata(this.getBlockMetadata())]);

                    if (itemstack1 == null || itemstack1.stackSize == 0)
                    {
                        iinventory.markDirty();
                        return true;
                    }

                    this.setInventorySlotContents(i, itemstack);
                }
            }

            return false;
        }
    }
    
    private boolean insertItemToTube()
    {
    	int side = BlockHopper.getDirectionFromMetadata(this.getBlockMetadata());
    	ITubeConnectable con = TubeHelper.getTubeConnectable(getWorldObj(), xCoord + Facing.offsetsXForSide[side], yCoord + Facing.offsetsYForSide[side], zCoord + Facing.offsetsZForSide[side]);
        
    	if(con == null)
    		return false;
    	
    	for (int i = 0; i < getSizeInventory(); ++i)
        {
            if (getStackInSlot(i) != null)
            {
            	ItemStack inSlot = getStackInSlot(i);
            	ItemPayload item = new ItemPayload(inSlot.copy());
            	item.setSize(1);
            	
            	boolean did = false;
            	
            	if(con.canAddItem(item, side))
            	{
            		inSlot.stackSize--;
            		
            		con.addItem(item, side);
            		did = true;
            	}
                
            	if(inSlot.stackSize == 0)
            		setInventorySlotContents(i, null);
            	
            	if(did)
            		return true;
            }
        }

        return false;
    }

    public static boolean func_145891_a(IHopper p_145891_0_)
    {
        IInventory iinventory = func_145884_b(p_145891_0_);

        if (iinventory != null)
        {
            byte b0 = 0;

            if (iinventory instanceof ISidedInventory && b0 > -1)
            {
                ISidedInventory isidedinventory = (ISidedInventory)iinventory;
                int[] aint = isidedinventory.getAccessibleSlotsFromSide(b0);

                for (int k = 0; k < aint.length; ++k)
                {
                    if (func_145892_a(p_145891_0_, iinventory, aint[k], b0))
                    {
                        return true;
                    }
                }
            }
            else
            {
                int i = iinventory.getSizeInventory();

                for (int j = 0; j < i; ++j)
                {
                    if (func_145892_a(p_145891_0_, iinventory, j, b0))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            EntityItem entityitem = func_145897_a(p_145891_0_.getWorldObj(), p_145891_0_.getXPos(), p_145891_0_.getYPos() + 1.0D, p_145891_0_.getZPos());

            if (entityitem != null)
            {
                return func_145898_a(p_145891_0_, entityitem);
            }
        }

        return false;
    }

    private static boolean func_145892_a(IHopper p_145892_0_, IInventory p_145892_1_, int p_145892_2_, int p_145892_3_)
    {
        ItemStack itemstack = p_145892_1_.getStackInSlot(p_145892_2_);

        if (itemstack != null && func_145890_b(p_145892_1_, itemstack, p_145892_2_, p_145892_3_))
        {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = func_145889_a(p_145892_0_, p_145892_1_.decrStackSize(p_145892_2_, 1), -1);

            if (itemstack2 == null || itemstack2.stackSize == 0)
            {
                p_145892_1_.markDirty();
                return true;
            }

            p_145892_1_.setInventorySlotContents(p_145892_2_, itemstack1);
        }

        return false;
    }

    public static boolean func_145898_a(IInventory p_145898_0_, EntityItem p_145898_1_)
    {
        boolean flag = false;

        if (p_145898_1_ == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = p_145898_1_.getEntityItem().copy();
            ItemStack itemstack1 = func_145889_a(p_145898_0_, itemstack, -1);

            if (itemstack1 != null && itemstack1.stackSize != 0)
            {
                p_145898_1_.setEntityItemStack(itemstack1);
            }
            else
            {
                flag = true;
                p_145898_1_.setDead();
            }

            return flag;
        }
    }

    public static ItemStack func_145889_a(IInventory p_145889_0_, ItemStack p_145889_1_, int p_145889_2_)
    {
        if (p_145889_0_ instanceof ISidedInventory && p_145889_2_ > -1)
        {
            ISidedInventory isidedinventory = (ISidedInventory)p_145889_0_;
            int[] aint = isidedinventory.getAccessibleSlotsFromSide(p_145889_2_);

            for (int l = 0; l < aint.length && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++l)
            {
                p_145889_1_ = func_145899_c(p_145889_0_, p_145889_1_, aint[l], p_145889_2_);
            }
        }
        else
        {
            int j = p_145889_0_.getSizeInventory();

            for (int k = 0; k < j && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++k)
            {
                p_145889_1_ = func_145899_c(p_145889_0_, p_145889_1_, k, p_145889_2_);
            }
        }

        if (p_145889_1_ != null && p_145889_1_.stackSize == 0)
        {
            p_145889_1_ = null;
        }

        return p_145889_1_;
    }

    private static boolean func_145885_a(IInventory p_145885_0_, ItemStack p_145885_1_, int p_145885_2_, int p_145885_3_)
    {
        return !p_145885_0_.isItemValidForSlot(p_145885_2_, p_145885_1_) ? false : !(p_145885_0_ instanceof ISidedInventory) || ((ISidedInventory)p_145885_0_).canInsertItem(p_145885_2_, p_145885_1_, p_145885_3_);
    }

    private static boolean func_145890_b(IInventory p_145890_0_, ItemStack p_145890_1_, int p_145890_2_, int p_145890_3_)
    {
        return !(p_145890_0_ instanceof ISidedInventory) || ((ISidedInventory)p_145890_0_).canExtractItem(p_145890_2_, p_145890_1_, p_145890_3_);
    }

    private static ItemStack func_145899_c(IInventory p_145899_0_, ItemStack p_145899_1_, int p_145899_2_, int p_145899_3_)
    {
        ItemStack itemstack1 = p_145899_0_.getStackInSlot(p_145899_2_);

        if (func_145885_a(p_145899_0_, p_145899_1_, p_145899_2_, p_145899_3_))
        {
            boolean flag = false;

            if (itemstack1 == null)
            {
                //Forge: BUGFIX: Again, make things respect max stack sizes.
                int max = Math.min(p_145899_1_.getMaxStackSize(), p_145899_0_.getInventoryStackLimit());
                if (max >= p_145899_1_.stackSize)
                {
                    p_145899_0_.setInventorySlotContents(p_145899_2_, p_145899_1_);
                    p_145899_1_ = null;
                }
                else
                {
                    p_145899_0_.setInventorySlotContents(p_145899_2_, p_145899_1_.splitStack(max));
                }
                flag = true;
            }
            else if (func_145894_a(itemstack1, p_145899_1_))
            {
                //Forge: BUGFIX: Again, make things respect max stack sizes.
                int max = Math.min(p_145899_1_.getMaxStackSize(), p_145899_0_.getInventoryStackLimit());
                if (max > itemstack1.stackSize)
                {
                    int l = Math.min(p_145899_1_.stackSize, max - itemstack1.stackSize);
                    p_145899_1_.stackSize -= l;
                    itemstack1.stackSize += l;
                    flag = l > 0;
                }
            }

            if (flag)
            {
                if (p_145899_0_ instanceof TileEntityHopper)
                {
                    ((TileEntityHopper)p_145899_0_).func_145896_c(8);
                    p_145899_0_.markDirty();
                }

                p_145899_0_.markDirty();
            }
        }

        return p_145899_1_;
    }

    private IInventory func_145895_l()
    {
        int i = BlockHopper.getDirectionFromMetadata(this.getBlockMetadata());
        return func_145893_b(this.getWorldObj(), (double)(this.xCoord + Facing.offsetsXForSide[i]), (double)(this.yCoord + Facing.offsetsYForSide[i]), (double)(this.zCoord + Facing.offsetsZForSide[i]));
    }

    public static IInventory func_145884_b(IHopper p_145884_0_)
    {
        return func_145893_b(p_145884_0_.getWorldObj(), p_145884_0_.getXPos(), p_145884_0_.getYPos() + 1.0D, p_145884_0_.getZPos());
    }

    public static EntityItem func_145897_a(World p_145897_0_, double p_145897_1_, double p_145897_3_, double p_145897_5_)
    {
        List list = p_145897_0_.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(p_145897_1_, p_145897_3_, p_145897_5_, p_145897_1_ + 1.0D, p_145897_3_ + 1.0D, p_145897_5_ + 1.0D), IEntitySelector.selectAnything);
        return list.size() > 0 ? (EntityItem)list.get(0) : null;
    }

    public static IInventory func_145893_b(World p_145893_0_, double p_145893_1_, double p_145893_3_, double p_145893_5_)
    {
        IInventory iinventory = null;
        int i = MathHelper.floor_double(p_145893_1_);
        int j = MathHelper.floor_double(p_145893_3_);
        int k = MathHelper.floor_double(p_145893_5_);
        TileEntity tileentity = p_145893_0_.getTileEntity(i, j, k);

        if (tileentity != null && tileentity instanceof IInventory)
        {
            iinventory = (IInventory)tileentity;

            if (iinventory instanceof TileEntityChest)
            {
                Block block = p_145893_0_.getBlock(i, j, k);

                if (block instanceof BlockChest)
                {
                    iinventory = ((BlockChest)block).func_149951_m(p_145893_0_, i, j, k);
                }
            }
        }

        if (iinventory == null)
        {
            List list = p_145893_0_.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB(p_145893_1_, p_145893_3_, p_145893_5_, p_145893_1_ + 1.0D, p_145893_3_ + 1.0D, p_145893_5_ + 1.0D), IEntitySelector.selectInventories);

            if (list != null && list.size() > 0)
            {
                iinventory = (IInventory)list.get(p_145893_0_.rand.nextInt(list.size()));
            }
        }

        return iinventory;
    }

    private static boolean func_145894_a(ItemStack p_145894_0_, ItemStack p_145894_1_)
    {
        return p_145894_0_.getItem() != p_145894_1_.getItem() ? false : (p_145894_0_.getItemDamage() != p_145894_1_.getItemDamage() ? false : (p_145894_0_.stackSize > p_145894_0_.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(p_145894_0_, p_145894_1_)));
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    public double getXPos()
    {
        return (double)this.xCoord;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos()
    {
        return (double)this.yCoord;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos()
    {
        return (double)this.zCoord;
    }

    public void func_145896_c(int p_145896_1_)
    {
        this.field_145901_j = p_145896_1_;
    }

    public boolean func_145888_j()
    {
        return this.field_145901_j > 0;
    }
    
    @Override
	public int getConnectableMask()
	{
		return 63;
	}

	@Override
	public boolean showInventoryConnection( int side )
	{
		return true;
	}

	@Override
	public boolean canItemEnter( TubeItem item )
	{
		return canAddItem(item.item, item.direction);
	}

	@Override
	public boolean canAddItem( Payload item, int direction )
	{
		if(!(item instanceof ItemPayload))
			return false;
		
		int facing = BlockHopper.getDirectionFromMetadata(this.getBlockMetadata());
		
		if(direction == (facing ^ 1))
			return false;
		
		BasicInvHandler handler = new BasicInvHandler(this);
		ItemPayload left = handler.insert((ItemPayload)item, direction ^ 1, false);
		
		if(left != null)
		{
			if(left.size() == item.size())
				return false;
		}
		
		return true;
	}

	@Override
	public boolean addItem( Payload item, int side )
	{
		if(!canAddItem(item, side))
			return false;
		
		BasicInvHandler handler = new BasicInvHandler(this);
		ItemPayload left = handler.insert((ItemPayload)item, side ^ 1, true);
		
		if(left != null)
			item.setSize(left.size());
		
		return true;
	}

	@Override
	public boolean addItem( TubeItem item )
	{
		return addItem(item.item, item.direction);
	}

	@Override
	public boolean addItem( TubeItem item, boolean syncToClient )
	{
		return addItem(item.item, item.direction);
	}

	@Override
	public void simulateEffects( TubeItem item ) {}

	@Override
	public int getRoutableDirections( TubeItem item ) { return 0; }

	@Override
	public boolean canPathThrough() { return false; }

	@Override
	public int getRouteWeight() { return 1; }

	@Override
	public int getColor() { return -1; }
}