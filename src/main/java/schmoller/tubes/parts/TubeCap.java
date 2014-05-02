package schmoller.tubes.parts;

import java.util.ArrayList;

import schmoller.tubes.AdvRender;
import schmoller.tubes.api.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.JCuboidPart;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TFacePart;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TubeCap extends JCuboidPart implements TFacePart, JNormalOcclusion
{
	private int mSlot = 0;
	public static IIcon icon;
	@SideOnly(Side.CLIENT)
	private AdvRender mRender;
	
	private static Cuboid6[] boxes = new Cuboid6[6];
	
	static
	{
		Vector3 center = new Vector3(0.5, 0.5, 0.5);
		
		for(int i = 0; i < 6; ++i)
		{
			Transformation trans = Rotation.sideRotations[i].at(center);
			
			boxes[i] = new Cuboid6(0.25, 0.0625, 0.25, 0.75, 0.25, 0.75).apply(trans);
		}
	}
	
	public void setSlot(int slot)
	{
		mSlot = slot;
	}
	
	@Override
	public int getSlotMask()
	{
		return 1 << mSlot;
	}

	@Override
	public int redstoneConductionMap() { return 0; }

	@Override
	public boolean solid( int side ) { return false; }
	
	@Override
	public boolean occlusionTest( TMultiPart npart )
	{
		return NormalOcclusionTest.apply(this, npart);
	}
	
	@Override
	public float getStrength( MovingObjectPosition hit, EntityPlayer player )
	{
		return player.getCurrentPlayerStrVsBlock(Blocks.netherrack, false) /  Blocks.netherrack.getBlockHardness(player.worldObj, hit.blockX, hit.blockY, hit.blockZ);
	}

	@Override
	public Cuboid6 getBounds()
	{
		return boxes[mSlot];
	}

	@Override
	public String getType()
	{
		return "tubeCap";
	}
	
	@Override
	public boolean doesTick()
	{
		return false;
	}
	
	@Override
	public ItemStack pickItem( MovingObjectPosition hit )
	{
		return new ItemStack(Items.TubeCap.getItem(),1, 0);
	}
	
	@Override
	public Iterable<ItemStack> getDrops()
	{
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Items.TubeCap.getItem(),1, 0));
		
		return items;
	}
	
	
	@Override
	public void save( NBTTagCompound tag )
	{
		tag.setByte("Side", (byte)mSlot);
	}
	
	@Override
	public void load( NBTTagCompound tag )
	{
		mSlot = tag.getByte("Side");
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		packet.writeByte(mSlot);
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		mSlot = packet.readByte();
	}

	@Override
	@SideOnly( Side.CLIENT )
	public boolean renderStatic( final Vector3 pos, int pass )
	{
		if(mRender == null)
			mRender = new AdvRender();
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world(), x(), y(), z());
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.resetColor();
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x(), y(), z());
		mRender.setIcon(icon);
		
		mRender.drawBox(63, (float)boxes[mSlot].min.x, (float)boxes[mSlot].min.y, (float)boxes[mSlot].min.z, (float)boxes[mSlot].max.x, (float)boxes[mSlot].max.y, (float)boxes[mSlot].max.z);
		return true;
	}

	@Override
	public Iterable<Cuboid6> getOcclusionBoxes()
	{
		ArrayList<Cuboid6> boxes = new ArrayList<Cuboid6>();
		boxes.add(getBounds());
		return boxes;
	}
}
