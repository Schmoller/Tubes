package schmoller.tubes.asm;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class MinecraftTransformer implements IClassTransformer, Opcodes
{
	@Override
	public byte[] transform( String className, String arg1, byte[] bytes )
	{
		if(className.equals("net.minecraft.tileentity.TileEntityHopper"))
		{
			ClassNode classNode = new ClassNode();
	        ClassReader classReader = new ClassReader(bytes);
	        classReader.accept(classNode, 0);
        
	        MethodNode mv;
	        
	        classNode.interfaces.add("schmoller/tubes/api/interfaces/ITubeConnectable");
	        
	        // updateHopper
	        {
        	mv = new MethodNode(ACC_PUBLIC, "updateHopper", "()Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(252, l0);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/tileentity/TileEntityHopper", "worldObj", "Lnet/minecraft/world/World;");
        	Label l1 = new Label();
        	mv.visitJumpInsn(IFNULL, l1);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/tileentity/TileEntityHopper", "worldObj", "Lnet/minecraft/world/World;");
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/world/World", "isRemote", "Z");
        	mv.visitJumpInsn(IFNE, l1);
        	Label l2 = new Label();
        	mv.visitLabel(l2);
        	mv.visitLineNumber(254, l2);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "isCoolingDown", "()Z");
        	Label l3 = new Label();
        	mv.visitJumpInsn(IFNE, l3);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "getBlockMetadata", "()I");
        	mv.visitMethodInsn(INVOKESTATIC, "net/minecraft/block/BlockHopper", "getIsBlockNotPoweredFromMetadata", "(I)Z");
        	mv.visitJumpInsn(IFEQ, l3);
        	Label l4 = new Label();
        	mv.visitLabel(l4);
        	mv.visitLineNumber(256, l4);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKESPECIAL, "net/minecraft/tileentity/TileEntityHopper", "insertItemToTube", "()Z");
        	mv.visitVarInsn(ISTORE, 1);
        	Label l5 = new Label();
        	mv.visitLabel(l5);
        	mv.visitLineNumber(258, l5);
        	mv.visitVarInsn(ILOAD, 1);
        	Label l6 = new Label();
        	mv.visitJumpInsn(IFNE, l6);
        	Label l7 = new Label();
        	mv.visitLabel(l7);
        	mv.visitLineNumber(259, l7);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKESPECIAL, "net/minecraft/tileentity/TileEntityHopper", "insertItemToInventory", "()Z");
        	mv.visitVarInsn(ISTORE, 1);
        	mv.visitLabel(l6);
        	mv.visitLineNumber(260, l6);
        	mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKESTATIC, "net/minecraft/tileentity/TileEntityHopper", "suckItemsIntoHopper", "(Lnet/minecraft/tileentity/Hopper;)Z");
        	Label l8 = new Label();
        	mv.visitJumpInsn(IFNE, l8);
        	mv.visitVarInsn(ILOAD, 1);
        	mv.visitJumpInsn(IFNE, l8);
        	mv.visitInsn(ICONST_0);
        	Label l9 = new Label();
        	mv.visitJumpInsn(GOTO, l9);
        	mv.visitLabel(l8);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitInsn(ICONST_1);
        	mv.visitLabel(l9);
        	mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
        	mv.visitVarInsn(ISTORE, 1);
        	Label l10 = new Label();
        	mv.visitLabel(l10);
        	mv.visitLineNumber(262, l10);
        	mv.visitVarInsn(ILOAD, 1);
        	mv.visitJumpInsn(IFEQ, l3);
        	Label l11 = new Label();
        	mv.visitLabel(l11);
        	mv.visitLineNumber(264, l11);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitIntInsn(BIPUSH, 8);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "setTransferCooldown", "(I)V");
        	Label l12 = new Label();
        	mv.visitLabel(l12);
        	mv.visitLineNumber(265, l12);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "onInventoryChanged", "()V");
        	Label l13 = new Label();
        	mv.visitLabel(l13);
        	mv.visitLineNumber(266, l13);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l3);
        	mv.visitLineNumber(270, l3);
        	mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l1);
        	mv.visitLineNumber(274, l1);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	Label l14 = new Label();
        	mv.visitLabel(l14);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l14, 0);
        	mv.visitLocalVariable("flag", "Z", null, l5, l3, 1);
        	mv.visitMaxs(2, 2);
        	mv.visitEnd();
        	
        	// Remove the existing one
        	Iterator<MethodNode> it = classNode.methods.iterator();
        	while(it.hasNext())
        	{
        		MethodNode node = it.next();
        		if(node.name.equals("updateHopper"))
        		{
        			it.remove();
        			break;
        		}
        	}
        	
        	classNode.methods.add(mv);
        	}
	        
	        // insertItemToTube method
	        {
        	mv = new MethodNode(ACC_PRIVATE, "insertItemToTube", "()Z", null, null);
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(314, l0);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "getBlockMetadata", "()I"); //***
        	mv.visitMethodInsn(INVOKESTATIC, "net/minecraft/block/BlockHopper", "getDirectionFromMetadata", "(I)I"); //****
        	mv.visitVarInsn(ISTORE, 1);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLineNumber(315, l1);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "getWorldObj", "()Lnet/minecraft/world/World;"); //****
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/tileentity/TileEntityHopper", "xCoord", "I");
        	mv.visitFieldInsn(GETSTATIC, "net/minecraft/util/Facing", "offsetsXForSide", "[I");
        	mv.visitVarInsn(ILOAD, 1);
        	mv.visitInsn(IALOAD);
        	mv.visitInsn(IADD);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/tileentity/TileEntityHopper", "yCoord", "I");
        	mv.visitFieldInsn(GETSTATIC, "net/minecraft/util/Facing", "offsetsYForSide", "[I");
        	mv.visitVarInsn(ILOAD, 1);
        	mv.visitInsn(IALOAD);
        	mv.visitInsn(IADD);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/tileentity/TileEntityHopper", "zCoord", "I");
        	mv.visitFieldInsn(GETSTATIC, "net/minecraft/util/Facing", "offsetsZForSide", "[I");
        	mv.visitVarInsn(ILOAD, 1);
        	mv.visitInsn(IALOAD);
        	mv.visitInsn(IADD);
        	mv.visitMethodInsn(INVOKESTATIC, "schmoller/tubes/api/helpers/TubeHelper", "getTubeConnectable", "(Lnet/minecraft/world/IBlockAccess;III)Lschmoller/tubes/api/interfaces/ITubeConnectable;");
        	mv.visitVarInsn(ASTORE, 2);
        	Label l2 = new Label();
        	mv.visitLabel(l2);
        	mv.visitLineNumber(317, l2);
        	mv.visitVarInsn(ALOAD, 2);
        	Label l3 = new Label();
        	mv.visitJumpInsn(IFNONNULL, l3);
        	Label l4 = new Label();
        	mv.visitLabel(l4);
        	mv.visitLineNumber(318, l4);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l3);
        	mv.visitLineNumber(320, l3);
        	mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, "schmoller/tubes/api/interfaces/ITubeConnectable"}, 0, null);
        	mv.visitInsn(ICONST_0);
        	mv.visitVarInsn(ISTORE, 3);
        	Label l5 = new Label();
        	mv.visitLabel(l5);
        	Label l6 = new Label();
        	mv.visitJumpInsn(GOTO, l6);
        	Label l7 = new Label();
        	mv.visitLabel(l7);
        	mv.visitLineNumber(322, l7);
        	mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitVarInsn(ILOAD, 3);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "getStackInSlot", "(I)Lnet/minecraft/item/ItemStack;"); //****
        	Label l8 = new Label();
        	mv.visitJumpInsn(IFNULL, l8);
        	Label l9 = new Label();
        	mv.visitLabel(l9);
        	mv.visitLineNumber(324, l9);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitVarInsn(ILOAD, 3);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "getStackInSlot", "(I)Lnet/minecraft/item/ItemStack;"); // ****
        	mv.visitVarInsn(ASTORE, 4);
        	Label l10 = new Label();
        	mv.visitLabel(l10);
        	mv.visitLineNumber(325, l10);
        	mv.visitTypeInsn(NEW, "schmoller/tubes/api/ItemPayload");
        	mv.visitInsn(DUP);
        	mv.visitVarInsn(ALOAD, 4);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "copy", "()Lnet/minecraft/item/ItemStack;"); // ****
        	mv.visitMethodInsn(INVOKESPECIAL, "schmoller/tubes/api/ItemPayload", "<init>", "(Lnet/minecraft/item/ItemStack;)V"); //****
        	mv.visitVarInsn(ASTORE, 5);
        	Label l11 = new Label();
        	mv.visitLabel(l11);
        	mv.visitLineNumber(326, l11);
        	mv.visitVarInsn(ALOAD, 5);
        	mv.visitInsn(ICONST_1);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "schmoller/tubes/api/ItemPayload", "setSize", "(I)V");
        	Label l12 = new Label();
        	mv.visitLabel(l12);
        	mv.visitLineNumber(328, l12);
        	mv.visitInsn(ICONST_0);
        	mv.visitVarInsn(ISTORE, 6);
        	Label l13 = new Label();
        	mv.visitLabel(l13);
        	mv.visitLineNumber(330, l13);
        	mv.visitVarInsn(ALOAD, 2);
        	mv.visitVarInsn(ALOAD, 5);
        	mv.visitVarInsn(ILOAD, 1);
        	mv.visitMethodInsn(INVOKEINTERFACE, "schmoller/tubes/api/interfaces/ITubeConnectable", "canAddItem", "(Lschmoller/tubes/api/Payload;I)Z");
        	Label l14 = new Label();
        	mv.visitJumpInsn(IFEQ, l14);
        	Label l15 = new Label();
        	mv.visitLabel(l15);
        	mv.visitLineNumber(332, l15);
        	mv.visitVarInsn(ALOAD, 4);
        	mv.visitInsn(DUP);
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/item/ItemStack", "stackSize", "I"); //****
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(ISUB);
        	mv.visitFieldInsn(PUTFIELD, "net/minecraft/item/ItemStack", "stackSize", "I"); //****
        	Label l16 = new Label();
        	mv.visitLabel(l16);
        	mv.visitLineNumber(334, l16);
        	mv.visitVarInsn(ALOAD, 2);
        	mv.visitVarInsn(ALOAD, 5);
        	mv.visitVarInsn(ILOAD, 1);
        	mv.visitMethodInsn(INVOKEINTERFACE, "schmoller/tubes/api/interfaces/ITubeConnectable", "addItem", "(Lschmoller/tubes/api/Payload;I)Z");
        	mv.visitInsn(POP);
        	Label l17 = new Label();
        	mv.visitLabel(l17);
        	mv.visitLineNumber(335, l17);
        	mv.visitInsn(ICONST_1);
        	mv.visitVarInsn(ISTORE, 6);
        	mv.visitLabel(l14);
        	mv.visitLineNumber(338, l14);
        	mv.visitFrame(Opcodes.F_APPEND,3, new Object[] {"net/minecraft/item/ItemStack", "schmoller/tubes/api/ItemPayload", Opcodes.INTEGER}, 0, null); //***
        	mv.visitVarInsn(ALOAD, 4);
        	mv.visitFieldInsn(GETFIELD, "net/minecraft/item/ItemStack", "stackSize", "I"); //***
        	Label l18 = new Label();
        	mv.visitJumpInsn(IFNE, l18);
        	Label l19 = new Label();
        	mv.visitLabel(l19);
        	mv.visitLineNumber(339, l19);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitVarInsn(ILOAD, 3);
        	mv.visitInsn(ACONST_NULL);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "setInventorySlotContents", "(ILnet/minecraft/item/ItemStack;)V"); //***
        	mv.visitLabel(l18);
        	mv.visitLineNumber(341, l18);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitVarInsn(ILOAD, 6);
        	mv.visitJumpInsn(IFEQ, l8);
        	Label l20 = new Label();
        	mv.visitLabel(l20);
        	mv.visitLineNumber(342, l20);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l8);
        	mv.visitLineNumber(320, l8);
        	mv.visitFrame(Opcodes.F_CHOP,3, null, 0, null);
        	mv.visitIincInsn(3, 1);
        	mv.visitLabel(l6);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitVarInsn(ILOAD, 3);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "getSizeInventory", "()I"); //***
        	mv.visitJumpInsn(IF_ICMPLT, l7);
        	Label l21 = new Label();
        	mv.visitLabel(l21);
        	mv.visitLineNumber(346, l21);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	Label l22 = new Label();
        	mv.visitLabel(l22);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l22, 0);
        	mv.visitLocalVariable("side", "I", null, l1, l22, 1);
        	mv.visitLocalVariable("con", "Lschmoller/tubes/api/interfaces/ITubeConnectable;", null, l2, l22, 2);
        	mv.visitLocalVariable("i", "I", null, l5, l21, 3);
        	mv.visitLocalVariable("inSlot", "Lnet/minecraft/item/ItemStack;", null, l10, l8, 4);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/ItemPayload;", null, l11, l8, 5);
        	mv.visitLocalVariable("did", "Z", null, l13, l8, 6);
        	mv.visitMaxs(6, 7);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
	        
	        // Add ITubeConnectable methods
	        {
        	mv = new MethodNode(ACC_PUBLIC, "getConnectableMask", "()I", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(648, l0);
        	mv.visitIntInsn(BIPUSH, 63);
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitMaxs(1, 1);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "showInventoryConnection", "(I)Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(654, l0);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitLocalVariable("side", "I", null, l0, l1, 1);
        	mv.visitMaxs(1, 2);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "canItemEnter", "(Lschmoller/tubes/api/TubeItem;)Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(660, l0);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitFieldInsn(GETFIELD, "schmoller/tubes/api/TubeItem", "item", "Lschmoller/tubes/api/Payload;");
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitFieldInsn(GETFIELD, "schmoller/tubes/api/TubeItem", "direction", "I");
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "canAddItem", "(Lschmoller/tubes/api/Payload;I)Z");
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/TubeItem;", null, l0, l1, 1);
        	mv.visitMaxs(3, 2);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "canAddItem", "(Lschmoller/tubes/api/Payload;I)Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(666, l0);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitTypeInsn(INSTANCEOF, "schmoller/tubes/api/ItemPayload");
        	Label l1 = new Label();
        	mv.visitJumpInsn(IFNE, l1);
        	Label l2 = new Label();
        	mv.visitLabel(l2);
        	mv.visitLineNumber(667, l2);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l1);
        	mv.visitLineNumber(669, l1);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "getBlockMetadata", "()I");
        	mv.visitMethodInsn(INVOKESTATIC, "net/minecraft/block/BlockHopper", "getDirectionFromMetadata", "(I)I"); //*******
        	mv.visitVarInsn(ISTORE, 3);
        	Label l3 = new Label();
        	mv.visitLabel(l3);
        	mv.visitLineNumber(671, l3);
        	mv.visitVarInsn(ILOAD, 2);
        	mv.visitVarInsn(ILOAD, 3);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IXOR);
        	Label l4 = new Label();
        	mv.visitJumpInsn(IF_ICMPNE, l4);
        	Label l5 = new Label();
        	mv.visitLabel(l5);
        	mv.visitLineNumber(672, l5);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l4);
        	mv.visitLineNumber(674, l4);
        	mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
        	mv.visitTypeInsn(NEW, "schmoller/tubes/inventory/BasicInvHandler");
        	mv.visitInsn(DUP);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKESPECIAL, "schmoller/tubes/inventory/BasicInvHandler", "<init>", "(Lnet/minecraft/inventory/IInventory;)V");
        	mv.visitVarInsn(ASTORE, 4);
        	Label l6 = new Label();
        	mv.visitLabel(l6);
        	mv.visitLineNumber(675, l6);
        	mv.visitVarInsn(ALOAD, 4);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitTypeInsn(CHECKCAST, "schmoller/tubes/api/ItemPayload");
        	mv.visitVarInsn(ILOAD, 2);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IXOR);
        	mv.visitInsn(ICONST_0);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "schmoller/tubes/inventory/BasicInvHandler", "insert", "(Lschmoller/tubes/api/ItemPayload;IZ)Lschmoller/tubes/api/ItemPayload;");
        	mv.visitVarInsn(ASTORE, 5);
        	Label l7 = new Label();
        	mv.visitLabel(l7);
        	mv.visitLineNumber(677, l7);
        	mv.visitVarInsn(ALOAD, 5);
        	Label l8 = new Label();
        	mv.visitJumpInsn(IFNULL, l8);
        	Label l9 = new Label();
        	mv.visitLabel(l9);
        	mv.visitLineNumber(679, l9);
        	mv.visitVarInsn(ALOAD, 5);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "schmoller/tubes/api/ItemPayload", "size", "()I");
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "schmoller/tubes/api/Payload", "size", "()I");
        	mv.visitJumpInsn(IF_ICMPNE, l8);
        	Label l10 = new Label();
        	mv.visitLabel(l10);
        	mv.visitLineNumber(680, l10);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l8);
        	mv.visitLineNumber(683, l8);
        	mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {"schmoller/tubes/inventory/BasicInvHandler", "schmoller/tubes/api/ItemPayload"}, 0, null);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IRETURN);
        	Label l11 = new Label();
        	mv.visitLabel(l11);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l11, 0);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/Payload;", null, l0, l11, 1);
        	mv.visitLocalVariable("direction", "I", null, l0, l11, 2);
        	mv.visitLocalVariable("facing", "I", null, l3, l11, 3);
        	mv.visitLocalVariable("handler", "Lschmoller/tubes/inventory/BasicInvHandler;", null, l6, l11, 4);
        	mv.visitLocalVariable("left", "Lschmoller/tubes/api/ItemPayload;", null, l7, l11, 5);
        	mv.visitMaxs(4, 6);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "addItem", "(Lschmoller/tubes/api/Payload;I)Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(689, l0);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitVarInsn(ILOAD, 2);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "canAddItem", "(Lschmoller/tubes/api/Payload;I)Z");
        	Label l1 = new Label();
        	mv.visitJumpInsn(IFNE, l1);
        	Label l2 = new Label();
        	mv.visitLabel(l2);
        	mv.visitLineNumber(690, l2);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	mv.visitLabel(l1);
        	mv.visitLineNumber(692, l1);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitTypeInsn(NEW, "schmoller/tubes/inventory/BasicInvHandler");
        	mv.visitInsn(DUP);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitMethodInsn(INVOKESPECIAL, "schmoller/tubes/inventory/BasicInvHandler", "<init>", "(Lnet/minecraft/inventory/IInventory;)V");
        	mv.visitVarInsn(ASTORE, 3);
        	Label l3 = new Label();
        	mv.visitLabel(l3);
        	mv.visitLineNumber(693, l3);
        	mv.visitVarInsn(ALOAD, 3);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitTypeInsn(CHECKCAST, "schmoller/tubes/api/ItemPayload");
        	mv.visitVarInsn(ILOAD, 2);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IXOR);
        	mv.visitInsn(ICONST_1);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "schmoller/tubes/inventory/BasicInvHandler", "insert", "(Lschmoller/tubes/api/ItemPayload;IZ)Lschmoller/tubes/api/ItemPayload;");
        	mv.visitVarInsn(ASTORE, 4);
        	Label l4 = new Label();
        	mv.visitLabel(l4);
        	mv.visitLineNumber(695, l4);
        	mv.visitVarInsn(ALOAD, 4);
        	Label l5 = new Label();
        	mv.visitJumpInsn(IFNULL, l5);
        	Label l6 = new Label();
        	mv.visitLabel(l6);
        	mv.visitLineNumber(696, l6);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitVarInsn(ALOAD, 4);
        	mv.visitMethodInsn(INVOKEVIRTUAL, "schmoller/tubes/api/ItemPayload", "size", "()I");
        	mv.visitMethodInsn(INVOKEVIRTUAL, "schmoller/tubes/api/Payload", "setSize", "(I)V");
        	mv.visitLabel(l5);
        	mv.visitLineNumber(698, l5);
        	mv.visitFrame(Opcodes.F_APPEND,2, new Object[] {"schmoller/tubes/inventory/BasicInvHandler", "schmoller/tubes/api/ItemPayload"}, 0, null);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IRETURN);
        	Label l7 = new Label();
        	mv.visitLabel(l7);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l7, 0);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/Payload;", null, l0, l7, 1);
        	mv.visitLocalVariable("side", "I", null, l0, l7, 2);
        	mv.visitLocalVariable("handler", "Lschmoller/tubes/inventory/BasicInvHandler;", null, l3, l7, 3);
        	mv.visitLocalVariable("left", "Lschmoller/tubes/api/ItemPayload;", null, l4, l7, 4);
        	mv.visitMaxs(4, 5);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "addItem", "(Lschmoller/tubes/api/TubeItem;)Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(704, l0);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitFieldInsn(GETFIELD, "schmoller/tubes/api/TubeItem", "item", "Lschmoller/tubes/api/Payload;");
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitFieldInsn(GETFIELD, "schmoller/tubes/api/TubeItem", "direction", "I");
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "addItem", "(Lschmoller/tubes/api/Payload;I)Z");
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/TubeItem;", null, l0, l1, 1);
        	mv.visitMaxs(3, 2);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "addItem", "(Lschmoller/tubes/api/TubeItem;Z)Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(710, l0);
        	mv.visitVarInsn(ALOAD, 0);
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitFieldInsn(GETFIELD, "schmoller/tubes/api/TubeItem", "item", "Lschmoller/tubes/api/Payload;");
        	mv.visitVarInsn(ALOAD, 1);
        	mv.visitFieldInsn(GETFIELD, "schmoller/tubes/api/TubeItem", "direction", "I");
        	mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntityHopper", "addItem", "(Lschmoller/tubes/api/Payload;I)Z");
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/TubeItem;", null, l0, l1, 1);
        	mv.visitLocalVariable("syncToClient", "Z", null, l0, l1, 2);
        	mv.visitMaxs(3, 3);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "simulateEffects", "(Lschmoller/tubes/api/TubeItem;)V", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(714, l0);
        	mv.visitInsn(RETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/TubeItem;", null, l0, l1, 1);
        	mv.visitMaxs(0, 2);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "getRoutableDirections", "(Lschmoller/tubes/api/TubeItem;)I", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(717, l0);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitLocalVariable("item", "Lschmoller/tubes/api/TubeItem;", null, l0, l1, 1);
        	mv.visitMaxs(1, 2);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "canPathThrough", "()Z", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(720, l0);
        	mv.visitInsn(ICONST_0);
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitMaxs(1, 1);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "getRouteWeight", "()I", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(723, l0);
        	mv.visitInsn(ICONST_1);
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitMaxs(1, 1);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	{
        	mv = new MethodNode(ACC_PUBLIC, "getColor", "()I", null, null);
        	mv.visitCode();
        	Label l0 = new Label();
        	mv.visitLabel(l0);
        	mv.visitLineNumber(726, l0);
        	mv.visitInsn(ICONST_M1);
        	mv.visitInsn(IRETURN);
        	Label l1 = new Label();
        	mv.visitLabel(l1);
        	mv.visitLocalVariable("this", "Lnet/minecraft/tileentity/TileEntityHopper;", null, l0, l1, 0);
        	mv.visitMaxs(1, 1);
        	mv.visitEnd();
        	classNode.methods.add(mv);
        	}
        	
        	ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
	        classNode.accept(cw);
	        
	        System.out.println("TileEntityHopper was modified by Tubes");
	        return cw.toByteArray();
		}
		return bytes;
	}
}
