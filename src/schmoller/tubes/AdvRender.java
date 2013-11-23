package schmoller.tubes;

import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import cpw.mods.fml.client.FMLClientHandler;

public class AdvRender 
{
	private Vector3f[] vertices = new Vector3f[8];
	
	private boolean mUseColor = false;
	private Vector3f[] mCornerColors = new Vector3f[8];
	
	
	private Matrix4f transform = new Matrix4f();
	
	
	private int[][] corners = new int[6][];
	private Tessellator tes;
	
	private Vector2f[][] textureCoords = new Vector2f[6][4]; 
	private int[] textureRotation = new int[6];
	private int[] textureFlip = new int[6];
	
	private int [] texIndex = new int[6];
	private Icon[] texIcons = new Icon[6];
	
	// Width in 16x16 icons
	private int textureWidth = 16;
	// Height in 16x16 icons
	private int textureHeight = 16;
	
	private boolean iconMode = false;
	
	
	private boolean mAbsoluteTexCoords = true;
	
	private Stack<Matrix4f> mStackTransforms = new Stack<Matrix4f>();
	
	
	// Lighting information
	private boolean mEnableAO = false;
	
	private float[][] mCornerAO = new float[6][4];
	private float mInternalAO = 0;
	
	private int[][] mFaceBrightness = new int[6][4];
	private int mInternalBrightness = 0;
	
	private float[] mLocalLighting = new float[6];
	
	private float opacity = 1;

	public boolean enableNormals = false;
	
	public AdvRender()
	{
		transform.setIdentity();
		
		for(int i = 0; i < 8; ++i)
			mCornerColors[i] = new Vector3f(1,1,1);
	}
	
	public void setIcon(Icon icon)
	{
		iconMode = true;
		for(int i = 0; i < 6; ++i)
			texIcons[i] = icon;
	}
	
	public void setIcon(int side, Icon icon)
	{
		iconMode = true;
		texIcons[side] = icon;
	}
	
	public void setIcon(Icon down, Icon up, Icon north, Icon south, Icon west, Icon east)
	{
		iconMode = true;
		texIcons[0] = down;
		texIcons[1] = up;
		texIcons[2] = north;
		texIcons[3] = south;
		texIcons[4] = west;
		texIcons[5] = east;
	}
	
	public void setTextureSize(int width, int height)
	{
		textureWidth = width;
		textureHeight = height;
	}
	public void setTextureIndex(int index)
	{
		iconMode = false;
		for(int i = 0; i < 6; i++)
			texIndex[i] = index;
	}
	
	public void setTextureIndex(int side, int index)
	{
		iconMode = false;
		texIndex[side] = index;
	}
	
	public void setTextureIndex(int down, int up, int north, int south, int west, int east)
	{
		iconMode = false;
		texIndex[0] = down;
		texIndex[1] = up;
		texIndex[2] = north;
		texIndex[3] = south;
		texIndex[4] = west;
		texIndex[5] = east;
	}
	
	public void resetTextureFlip()
	{
		setTextureFlip(0);
	}
	
	public void setTextureFlip(int flip)
	{
		for(int i = 0; i < 6; ++i)
			textureFlip[i] = flip;
	}
	
	public void setTextureFlip(int side, int flip)
	{
		textureFlip[side] = flip;
	}
	public void setTextureFlip(int down, int up, int north, int south, int west, int east)
	{
		textureFlip[0] = down;
		textureFlip[1] = up;
		textureFlip[2] = north;
		textureFlip[3] = south;
		textureFlip[4] = west;
		textureFlip[5] = east;
	}
	
	public void resetTextureRotation()
	{
		setTextureRotation(0);
	}
	public void setTextureRotation(int rotation)
	{
		for(int i = 0; i < 6; i++)
			textureRotation[i] = rotation;
	}
	public void setTextureRotation(int down, int up, int north, int south, int west, int east)
	{
		textureRotation[0] = down;
		textureRotation[1] = up;
		textureRotation[2] = north;
		textureRotation[3] = south;
		textureRotation[4] = west;
		textureRotation[5] = east;
	}
	
	public void setLocalLights(float down, float up, float north, float south, float west, float east)
	{
		mLocalLighting[0] = down;
		mLocalLighting[1] = up;
		mLocalLighting[2] = north;
		mLocalLighting[3] = south;
		mLocalLighting[4] = west;
		mLocalLighting[5] = east;
	}
	
	public void resetAO()
	{
		mEnableAO = false;
	}
	
	public void resetLighting(int brightness)
	{
		for(int side = 0; side < 6; side++)
		{
			mFaceBrightness[side][0] = brightness;
			mFaceBrightness[side][1] = brightness;
			mFaceBrightness[side][2] = brightness;
			mFaceBrightness[side][3] = brightness;
		}
		
		mInternalBrightness = brightness;
		mInternalAO = 1.0f;
		
		opacity = 1;
	}
	
	public void setOpacity(float value)
	{
		opacity = value;
	}

	private int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z)
	{
		int id = world.getBlockId(x, y, z);
		if(Block.blocksList[id] != null)
			return Block.blocksList[id].getMixedBrightnessForBlock(world, x, y, z);
		else
			return world.getLightBrightnessForSkyBlocks(x, y, z, 0);
	}
	
	private float getAmbientOcclusionLightValue(IBlockAccess world, int x, int y, int z)
	{
		int id = world.getBlockId(x, y, z);

		if(Block.blocksList[id] != null)
			return Block.blocksList[id].getAmbientOcclusionLightValue(world, x, y, z);
		else
			return 1.0f;
	}
	
	private int mixBrightness(int source1, int source2, int source3, int source4)
	{
		if (source1 == 0)
            source1 = source4;

        if (source2 == 0)
            source2 = source4;

        if (source3 == 0)
            source3 = source4;

        return source1 + source2 + source3 + source4 >> 2 & 16711935;
	}
	
	public void setLightingFromBlock(IBlockAccess world, int x, int y, int z)
	{
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			setFaceLightingFromWorld(dir.ordinal(), world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		
		mInternalBrightness = getMixedBrightnessForBlock(world, x, y, z);
		mInternalAO = getAmbientOcclusionLightValue(world, x, y, z);
	}
	
	public void setFaceLightingFromWorld(int side, IBlockAccess world, int x, int y, int z)
	{
		if(!FMLClientHandler.instance().getClient().isAmbientOcclusionEnabled())
		{
			int lightVal = getMixedBrightnessForBlock(world, x, y, z);
			
			mFaceBrightness[side][0] = lightVal;
			mFaceBrightness[side][1] = lightVal;
			mFaceBrightness[side][2] = lightVal;
			mFaceBrightness[side][3] = lightVal;
		}
		else
		{
			int[] brightness = new int[8]; // Starts on the left side relative to the face. goes clockwise
			float[] ao = new float[8]; // Same as above
			int myBrightness = getMixedBrightnessForBlock(world, x, y, z);
			float myAO = getAmbientOcclusionLightValue(world, x, y, z);
			
			
			switch(side)
			{
			case 0: // Down
				brightness[0] = getMixedBrightnessForBlock(world, x - 1, y, z);
				brightness[1] = getMixedBrightnessForBlock(world, x - 1, y, z - 1);
				brightness[2] = getMixedBrightnessForBlock(world, x, y, z - 1);
				brightness[3] = getMixedBrightnessForBlock(world, x + 1, y, z - 1);
				brightness[4] = getMixedBrightnessForBlock(world, x + 1, y, z);
				brightness[5] = getMixedBrightnessForBlock(world, x + 1, y, z + 1);
				brightness[6] = getMixedBrightnessForBlock(world, x, y, z + 1);
				brightness[7] = getMixedBrightnessForBlock(world, x - 1, y, z + 1);
				
				ao[0] = getAmbientOcclusionLightValue(world, x - 1, y, z);
				ao[1] = getAmbientOcclusionLightValue(world, x - 1, y, z - 1);
				ao[2] = getAmbientOcclusionLightValue(world, x, y, z - 1);
				ao[3] = getAmbientOcclusionLightValue(world, x + 1, y, z - 1);
				ao[4] = getAmbientOcclusionLightValue(world, x + 1, y, z);
				ao[5] = getAmbientOcclusionLightValue(world, x + 1, y, z + 1);
				ao[6] = getAmbientOcclusionLightValue(world, x, y, z + 1);
				ao[7] = getAmbientOcclusionLightValue(world, x - 1, y, z + 1);
				break;
			case 1: // Up
				brightness[0] = getMixedBrightnessForBlock(world, x - 1, y, z);
				brightness[1] = getMixedBrightnessForBlock(world, x - 1, y, z + 1);
				brightness[2] = getMixedBrightnessForBlock(world, x, y, z + 1);
				brightness[3] = getMixedBrightnessForBlock(world, x + 1, y, z + 1);
				brightness[4] = getMixedBrightnessForBlock(world, x + 1, y, z);
				brightness[5] = getMixedBrightnessForBlock(world, x + 1, y, z - 1);
				brightness[6] = getMixedBrightnessForBlock(world, x, y, z - 1);
				brightness[7] = getMixedBrightnessForBlock(world, x - 1, y, z - 1);
				
				ao[0] = getAmbientOcclusionLightValue(world, x - 1, y, z);
				ao[1] = getAmbientOcclusionLightValue(world, x - 1, y, z + 1);
				ao[2] = getAmbientOcclusionLightValue(world, x, y, z + 1);
				ao[3] = getAmbientOcclusionLightValue(world, x + 1, y, z + 1);
				ao[4] = getAmbientOcclusionLightValue(world, x + 1, y, z);
				ao[5] = getAmbientOcclusionLightValue(world, x + 1, y, z - 1);
				ao[6] = getAmbientOcclusionLightValue(world, x, y, z - 1);
				ao[7] = getAmbientOcclusionLightValue(world, x - 1, y, z - 1);
				break;
			case 2: // North
				brightness[0] = getMixedBrightnessForBlock(world, x - 1, y, z);
				brightness[1] = getMixedBrightnessForBlock(world, x - 1, y + 1, z);
				brightness[2] = getMixedBrightnessForBlock(world, x, y + 1, z);
				brightness[3] = getMixedBrightnessForBlock(world, x + 1, y + 1, z);
				brightness[4] = getMixedBrightnessForBlock(world, x + 1, y, z);
				brightness[5] = getMixedBrightnessForBlock(world, x + 1, y - 1, z);
				brightness[6] = getMixedBrightnessForBlock(world, x, y - 1, z);
				brightness[7] = getMixedBrightnessForBlock(world, x - 1, y - 1, z);
				
				ao[0] = getAmbientOcclusionLightValue(world, x - 1, y, z);
				ao[1] = getAmbientOcclusionLightValue(world, x - 1, y + 1, z);
				ao[2] = getAmbientOcclusionLightValue(world, x, y + 1, z);
				ao[3] = getAmbientOcclusionLightValue(world, x + 1, y + 1, z);
				ao[4] = getAmbientOcclusionLightValue(world, x + 1, y, z);
				ao[5] = getAmbientOcclusionLightValue(world, x + 1, y - 1, z);
				ao[6] = getAmbientOcclusionLightValue(world, x, y - 1, z);
				ao[7] = getAmbientOcclusionLightValue(world, x - 1, y - 1, z);
			break;
			case 3: // South
				brightness[0] = getMixedBrightnessForBlock(world, x + 1, y, z);
				brightness[1] = getMixedBrightnessForBlock(world, x + 1, y + 1, z);
				brightness[2] = getMixedBrightnessForBlock(world, x, y + 1, z);
				brightness[3] = getMixedBrightnessForBlock(world, x - 1, y + 1, z);
				brightness[4] = getMixedBrightnessForBlock(world, x - 1, y, z);
				brightness[5] = getMixedBrightnessForBlock(world, x - 1, y - 1, z);
				brightness[6] = getMixedBrightnessForBlock(world, x, y - 1, z);
				brightness[7] = getMixedBrightnessForBlock(world, x + 1, y - 1, z);
				
				ao[0] = getAmbientOcclusionLightValue(world, x + 1, y, z);
				ao[1] = getAmbientOcclusionLightValue(world, x + 1, y + 1, z);
				ao[2] = getAmbientOcclusionLightValue(world, x, y + 1, z);
				ao[3] = getAmbientOcclusionLightValue(world, x - 1, y + 1, z);
				ao[4] = getAmbientOcclusionLightValue(world, x - 1, y, z);
				ao[5] = getAmbientOcclusionLightValue(world, x - 1, y - 1, z);
				ao[6] = getAmbientOcclusionLightValue(world, x, y - 1, z);
				ao[7] = getAmbientOcclusionLightValue(world, x + 1, y - 1, z);
				break;
			case 4: // West
				brightness[0] = getMixedBrightnessForBlock(world, x, y, z + 1);
				brightness[1] = getMixedBrightnessForBlock(world, x, y + 1, z + 1);
				brightness[2] = getMixedBrightnessForBlock(world, x, y + 1, z);
				brightness[3] = getMixedBrightnessForBlock(world, x, y + 1, z - 1);
				brightness[4] = getMixedBrightnessForBlock(world, x, y, z - 1);
				brightness[5] = getMixedBrightnessForBlock(world, x, y - 1, z - 1);
				brightness[6] = getMixedBrightnessForBlock(world, x, y - 1, z);
				brightness[7] = getMixedBrightnessForBlock(world, x, y - 1, z + 1);
				
				ao[0] = getAmbientOcclusionLightValue(world, x, y, z + 1);
				ao[1] = getAmbientOcclusionLightValue(world, x, y + 1, z + 1);
				ao[2] = getAmbientOcclusionLightValue(world, x, y + 1, z);
				ao[3] = getAmbientOcclusionLightValue(world, x, y + 1, z - 1);
				ao[4] = getAmbientOcclusionLightValue(world, x, y, z - 1);
				ao[5] = getAmbientOcclusionLightValue(world, x, y - 1, z - 1);
				ao[6] = getAmbientOcclusionLightValue(world, x, y - 1, z);
				ao[7] = getAmbientOcclusionLightValue(world, x, y - 1, z + 1);
				break;
			case 5: // East
				brightness[0] = getMixedBrightnessForBlock(world, x, y, z - 1);
				brightness[1] = getMixedBrightnessForBlock(world, x, y + 1, z - 1);
				brightness[2] = getMixedBrightnessForBlock(world, x, y + 1, z);
				brightness[3] = getMixedBrightnessForBlock(world, x, y + 1, z + 1);
				brightness[4] = getMixedBrightnessForBlock(world, x, y, z + 1);
				brightness[5] = getMixedBrightnessForBlock(world, x, y - 1, z + 1);
				brightness[6] = getMixedBrightnessForBlock(world, x, y - 1, z);
				brightness[7] = getMixedBrightnessForBlock(world, x, y - 1, z - 1);
				
				ao[0] = getAmbientOcclusionLightValue(world, x, y, z - 1);
				ao[1] = getAmbientOcclusionLightValue(world, x, y + 1, z - 1);
				ao[2] = getAmbientOcclusionLightValue(world, x, y + 1, z);
				ao[3] = getAmbientOcclusionLightValue(world, x, y + 1, z + 1);
				ao[4] = getAmbientOcclusionLightValue(world, x, y, z + 1);
				ao[5] = getAmbientOcclusionLightValue(world, x, y - 1, z + 1);
				ao[6] = getAmbientOcclusionLightValue(world, x, y - 1, z);
				ao[7] = getAmbientOcclusionLightValue(world, x, y - 1, z - 1);
				break;
			}
			
			mFaceBrightness[side][0] = mixBrightness(brightness[0], brightness[1], brightness[2], myBrightness);
			mFaceBrightness[side][1] = mixBrightness(brightness[2], brightness[3], brightness[4], myBrightness);
			mFaceBrightness[side][2] = mixBrightness(brightness[4], brightness[5], brightness[6], myBrightness);
			mFaceBrightness[side][3] = mixBrightness(brightness[6], brightness[7], brightness[0], myBrightness);
			
			mCornerAO[side][0] = (ao[0] + ao[1] + ao[2] + myAO) / 4.0f;
			mCornerAO[side][1] = (ao[2] + ao[3] + ao[4] + myAO) / 4.0f;
			mCornerAO[side][2] = (ao[4] + ao[5] + ao[6] + myAO) / 4.0f;
			mCornerAO[side][3] = (ao[6] + ao[7] + ao[0] + myAO) / 4.0f;
			
			mEnableAO = true;
		}
	}
	
	public void resetColor()
	{
		mUseColor = false;
	}
	
	public void setColorRGB(int color)
	{
		mUseColor = true;
		float r,g,b;
		
		r = ((color >> 16) & 255) / 255f;
		g = ((color >> 8) & 255) / 255f;
		b = (color & 255) / 255f;
		
		for(int i = 0; i < mCornerColors.length; ++i)
		{
			mCornerColors[i].x = r;
			mCornerColors[i].y = g;
			mCornerColors[i].z = b;
		}
	}
	
	public void setColorI(int r, int g, int b)
	{
		mUseColor = true;
		float rF,gF,bF;
		
		rF = r / 255f;
		gF = g / 255f;
		bF = b / 255f;
		
		for(int i = 0; i < mCornerColors.length; ++i)
		{
			mCornerColors[i].x = rF;
			mCornerColors[i].y = gF;
			mCornerColors[i].z = bF;
		}
	}
	
	public void setColorF(float r, float b, float g)
	{
		mUseColor = true;

		for(int i = 0; i < mCornerColors.length; ++i)
		{
			mCornerColors[i].x = r;
			mCornerColors[i].y = g;
			mCornerColors[i].z = b;
		}
	}
	
	public void setAbsoluteTextureCoords(boolean value)
	{
		mAbsoluteTexCoords = value;
	}
	
	public boolean getAbsoluteTextureCoords()
	{
		return mAbsoluteTexCoords;
	}
	
	
	private Vector3f getLocalCoords(Vector3f vec, int side)
	{
		Vector3f coords;
		
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		
		switch(dir)
		{
		case DOWN:
			coords = new Vector3f(vec.x, vec.z, 1 - vec.y);
			break;
		case EAST:
			coords = new Vector3f(1 - vec.z, vec.y, vec.x);
			break;
		case NORTH:
			coords = new Vector3f(1 - vec.x, vec.y, 1 - vec.z);
			break;
		case SOUTH:
			coords = new Vector3f(vec.x, vec.y, vec.z);
			break;
		case UP:
			coords = new Vector3f(vec.x, 1 - vec.z, vec.y);
			break;
		case WEST:
			coords = new Vector3f(vec.z,vec.y, 1 - vec.x);
			break;
		default:
			coords = null;
			break;
		
		}
		
		return coords;
		
	}
	
	// Rotates a vector the number of times specified
	private void rotateVector(Vector3f vec, int amount)
	{
		double x, y;
		double theta = amount * (Math.PI / 2);
		
		x = vec.x * Math.cos(theta) - vec.y * Math.sin(theta);
		y = vec.x * Math.sin(theta) + vec.y * Math.cos(theta);
		
		vec.x = (float)x;
		vec.y = (float)y;
	}
	
	private void rotateVector(Vector2f vec, int amount)
	{
		double x, y;
		double theta = amount * (Math.PI / 2);
		
		x = vec.x * Math.cos(theta) - vec.y * Math.sin(theta);
		y = vec.x * Math.sin(theta) + vec.y * Math.cos(theta);
		
		vec.x = (float)x;
		vec.y = (float)y;
	}
	
	private void normalizeCoords(Vector2f[] coords)
	{
		int xNeg = 0;
		int yNeg = 0;
		
		int xPos = 0;
		int yPos = 0;
		
		for(int i = 0; i < coords.length; i++)
		{
			if (coords[i].x < -0.00001)
				xNeg = (int)Math.max(xNeg, -Math.floor(coords[i].x));
			if (coords[i].y < -0.00001)
				yNeg = (int)Math.max(yNeg, -Math.floor(coords[i].y));
			
			if (coords[i].x > 1.00001)
				xPos = (int)Math.max(xPos, Math.floor(coords[i].x));
			if (coords[i].y > 1.00001)
				yPos = (int)Math.max(yPos, Math.floor(coords[i].y));
		}
		
		if (xNeg != 0 || yNeg != 0 || xPos != 0 || yPos != 0)
		{
			for(int i = 0; i < coords.length; i++)
			{
				coords[i].x += xNeg;
				coords[i].y += yNeg;
				
				coords[i].x -= xPos;
				coords[i].y -= yPos;
			}
		}
	}
	
	private void normalizeCoords(Vector3f[] coords)
	{
		int xNeg = 0;
		int yNeg = 0;
		
		int xPos = 0;
		int yPos = 0;
		
		for(int i = 0; i < coords.length; i++)
		{
			if (coords[i].x < -0.00001)
				xNeg = (int)Math.max(xNeg, -Math.floor(coords[i].x));
			if (coords[i].y < -0.00001)
				yNeg = (int)Math.max(yNeg, -Math.floor(coords[i].y));
			
			if (coords[i].x > 1.00001)
				xPos = (int)Math.max(xPos, Math.floor(coords[i].x));
			if (coords[i].y > 1.00001)
				yPos = (int)Math.max(yPos, Math.floor(coords[i].y));
		}
		
		if (xNeg != 0 || yNeg != 0 || xPos != 0 || yPos != 0)
		{
			for(int i = 0; i < coords.length; i++)
			{
				coords[i].x += xNeg;
				coords[i].y += yNeg;
				
				coords[i].x -= xPos;
				coords[i].y -= yPos;
			}
		}
	}
	
	private void flipCoords(Vector3f[] coords, int flip)
	{
		if((flip & 1) != 0)
		{
			for(int i = 0; i < coords.length; ++i)
				coords[i].x = 1 - coords[i].x;
		}
		
		if((flip & 2) != 0)
		{
			for(int i = 0; i < coords.length; ++i)
				coords[i].y = 1 - coords[i].y;
		}
	}
	
	private void updateTextureCoords()
	{
		for(int side = 0; side < 6; side++)
		{
			float left, top;
			
			float width, height;
			
			if(!iconMode)
			{
				left = (texIndex[side] % textureWidth) / (float)textureWidth;
				top = (texIndex[side] / textureWidth) / (float)textureHeight;
				
				width = 1/(float)textureWidth;
				height = 1/(float)textureHeight;
			}
			else
			{
				left = texIcons[side].getMinU();
				top = texIcons[side].getMinV();
				
				width = texIcons[side].getMaxU() - left;
				height = texIcons[side].getMaxV() - top;
			}
			
			
			if (mAbsoluteTexCoords)
			{
				Vector3f[] coords = new Vector3f[4];
				
				for(int i = 0; i < 4; ++i)
				{
					coords[i] = getLocalCoords(vertices[corners[side][i]], side);
					rotateVector(coords[i], textureRotation[side]);
				}
				
				normalizeCoords(coords);
				flipCoords(coords, textureFlip[side]);
				
				for(int i = 0; i < 4; i++)
					textureCoords[side][i] = new Vector2f(left + (coords[i].x * width),top + ((1 - coords[i].y) * height));
			}
			else
			{
				textureCoords[side][0] = new Vector2f(left,top+height);
				textureCoords[side][1] = new Vector2f(left+width,top+height);
				textureCoords[side][2] = new Vector2f(left+width,top);
				textureCoords[side][3] = new Vector2f(left,top);
				
				for(int i = 0; i < 4; ++i)
					rotateVector(textureCoords[side][i], textureRotation[side]);
				
				normalizeCoords(textureCoords[side]);
			}
		}
		
	}
	
	public void setupBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
	{
		vertices[0] = new Vector3f(minX, minY, minZ);
		vertices[1] = new Vector3f(maxX, minY, minZ);
		vertices[2] = new Vector3f(minX, maxY, minZ);
		vertices[3] = new Vector3f(minX, minY, maxZ);
		vertices[4] = new Vector3f(maxX, maxY, minZ);
		vertices[5] = new Vector3f(minX, maxY, maxZ);
		vertices[6] = new Vector3f(maxX, minY, maxZ);
		vertices[7] = new Vector3f(maxX, maxY, maxZ);
		
		corners[0] = new int[] {0,1,6,3}; // Down
		corners[1] = new int[] {5,7,4,2}; // Up
		corners[2] = new int[] {2,4,1,0}; // South
		corners[3] = new int[] {7,5,3,6}; // North
		corners[4] = new int[] {5,2,0,3}; // West
		corners[5] = new int[] {4,7,6,1}; // East
	}
	public void resetTransform()
	{
		transform.setIdentity();
	}
	public void translate(float x, float y, float z)
	{
		Matrix4f mat = new Matrix4f();
		mat = mat.translate(new Vector3f(x,y,z));
		Matrix4f.mul(mat, transform, transform);
	}
	public void rotate(float amount, Vector3f axis)
	{
		Matrix4f mat = new Matrix4f();
		mat = mat.rotate(amount, axis);
		Matrix4f.mul(mat, transform, transform);
	}
	public void scale(float x, float y, float z)
	{
		Matrix4f mat = new Matrix4f();
		mat = mat.scale(new Vector3f(x,y,z));
		Matrix4f.mul(mat, transform, transform);
	}
	public void drawBox(int faces, float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
	{
		setupBox(minX, minY, minZ, maxX, maxY, maxZ);
		drawFaces(faces);
	}
	
//	private Vector3f getColorAt(Vector3f vec)
//	{
//		Vector3f color = null;
//		
//		
//	}
	
	private float interpolateAO(int side, float u, float v)
	{
		float top, bottom;
		
		top = mCornerAO[side][0] * u + (1 - u) * mCornerAO[side][1];
		bottom = mCornerAO[side][3] * u + (1 - u) * mCornerAO[side][2];
		
		return top * v + (1 - v) * bottom;
	}
	
	private int interpolateBrightness(int side, float u, float v)
	{
		int[] sky = new int[4];
		int[] block = new int[4];
		
		for(int i = 0; i < 4; ++i)
		{
			sky[i] = (mFaceBrightness[side][i] >> 20) & 15;
			block[i] = (mFaceBrightness[side][i] >> 4) & 15;
		}
		
		float skyValTop = sky[0] * u + (1 - u) * sky[1];
		float skyValBottom = sky[3] * u + (1 - u) * sky[2];
		
		float blockValTop = block[0] * u + (1 - u) * block[1];
		float blockValBottom = block[3] * u + (1 - u) * block[2];
		
		float skyVal = skyValTop * v + (1 - v) * skyValBottom;
		float blockVal = blockValTop * v + (1 - v) * blockValBottom;
		
		return (int)Math.round(skyVal) << 20 | (int)Math.round(blockVal) << 4;
	}
	
	public void drawFaces(int faces)
	{
		tes = Tessellator.instance;
		updateTextureCoords();
		
		
		for(int i = 0; i < 6; i++)
		{
			if((faces & 1 << i) != 0)
			{
				// Draw the face
				
				Vector3f[] coords = new Vector3f[4];
				
				for(int v = 0; v < 4; ++v)
				{
					coords[v] = getLocalCoords(vertices[corners[i][v]], i);
				}
				
				if(i == 0 || i == 1)
				{
					Vector3f temp = coords[0];
					coords[0] = coords[2];
					coords[2] = temp;
					temp = coords[3];
					coords[3] = coords[1];
					coords[1] = temp;
				}
				
				normalizeCoords(coords);
				
				for(int v = 0; v < 4; v++)
				{
					Vector3f color = mCornerColors[i];
					
					if (!mUseColor)
						color.x = color.y = color.z = 1;
					
					if (mEnableAO)
					{
						if(mAbsoluteTexCoords)
						{
							float ao = (coords[v].z < 1 ? mInternalAO : interpolateAO(i, coords[v].x, coords[v].y));
							
							color.x *= ao;
							color.y *= ao;
							color.z *= ao;
						}
						else
						{
							color.x *= mCornerAO[i][v];
							color.y *= mCornerAO[i][v];
							color.z *= mCornerAO[i][v];
						}
					}
					
					color.x *= mLocalLighting[i];
					color.y *= mLocalLighting[i];
					color.z *= mLocalLighting[i];

					tes.setColorRGBA_F(color.x, color.y, color.z, opacity);
					tes.setBrightness((coords[v].z < 1 ? mInternalBrightness : interpolateBrightness(i, coords[v].x, coords[v].y)));
					
					if (enableNormals)
						tes.setNormal((float)ForgeDirection.getOrientation(i).offsetX, (float)ForgeDirection.getOrientation(i).offsetY, (float)ForgeDirection.getOrientation(i).offsetZ);
					
					Vector4f vert = new Vector4f();
					Matrix4f.transform(transform, new Vector4f(vertices[corners[i][v]].x, vertices[corners[i][v]].y, vertices[corners[i][v]].z,1), vert);
					tes.addVertexWithUV(vert.x,vert.y,vert.z, textureCoords[i][v].x, textureCoords[i][v].y);
				}
			}
		}
	}
	
	public void drawPolygon(Vector3f[] vertices, int[] indices)
	{
		for(int i = 0; i < indices.length; i++)
		{
			Vector4f vert = new Vector4f();
			Matrix4f.transform(transform, new Vector4f(vertices[indices[i]].x, vertices[indices[i]].y, vertices[indices[i]].z,1), vert);
			tes.addVertex(vert.x,vert.y,vert.z);
		}
	}
	public void drawPolygon(Vector3f[] vertices,Vector2f[] texCoords, int[] indices)
	{
		tes = Tessellator.instance;
		for(int i = 0; i < indices.length; i++)
		{
			Vector4f vert = new Vector4f();
			Matrix4f.transform(transform, new Vector4f(vertices[indices[i]].x, vertices[indices[i]].y, vertices[indices[i]].z,1), vert);
			tes.addVertexWithUV(vert.x,vert.y,vert.z,texCoords[indices[i]].x,texCoords[indices[i]].y);
		}
	}
			
	
	public float toRadians(float deg)
	{
		return (float)(deg * Math.PI / 180.F);
	}
	public float toDegrees(float rad)
	{
		return (float)(rad / Math.PI * 180.F);
	}
	
	public void pushTransform()
	{
		mStackTransforms.push(new Matrix4f().load(transform));
	}
	public void popTransform()
	{
		transform = mStackTransforms.pop();
	}

	public void resetLighting()
	{
		resetLighting(15728880);
	}
}
