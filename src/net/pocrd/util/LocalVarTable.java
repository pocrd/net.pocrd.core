package net.pocrd.util;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalVarTable implements Opcodes {
	private class VarInfo {
		public VarInfo(int slotPos, int loadOpcode) {
			this.slotPos = slotPos;
			this.loadOpcode = loadOpcode;
		}

		/**
		 * 加载变量的指令
		 */
		public int loadOpcode;
		/**
		 * 变量在局部变量表中的存储位置
		 */
		public int slotPos;
	}

	private int nextFreeSlotPos = 0;
	private int nextLocalVarIndex = 0;
	private HashMap<Integer, VarInfo> localVarInfo = new HashMap<Integer, VarInfo>();

	public LocalVarTable(Method method) {
		init(method);
	}

	/**
	 * 设置this
	 */
	private void init(Method method) {
		nextLocalVarIndex = 0;
		nextFreeSlotPos = 0;
		localVarInfo.put(0, new VarInfo(0, ALOAD));//this
		nextFreeSlotPos++;
		nextLocalVarIndex++;
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes != null) {
			for (int i=0;i<paramTypes.length;i++) {
				if (paramTypes[i].isPrimitive()) {
					String pName = paramTypes[i].getName();
					switch (pName) {
					case "int":
					case "boolean":
					case "short":
					case "byte":
					case "char":
						localVarInfo.put(nextLocalVarIndex, new VarInfo(nextFreeSlotPos, ILOAD));
						break;
					case "float":
						localVarInfo.put(nextLocalVarIndex, new VarInfo(nextFreeSlotPos, FLOAD));
						break;
					case "double":
						localVarInfo.put(nextLocalVarIndex, new VarInfo(nextFreeSlotPos,DLOAD));
						nextFreeSlotPos++;
						break;
					case "long":
						localVarInfo.put(nextLocalVarIndex, new VarInfo(nextFreeSlotPos,LLOAD));
						nextFreeSlotPos++;
						break;
					default:
						throw new RuntimeException("不支持的类型" + pName);
					}
				} else {
					localVarInfo.put(nextLocalVarIndex, new VarInfo(nextFreeSlotPos,ALOAD));
				}
				nextFreeSlotPos++;
				nextLocalVarIndex++;
			}
		}
	}
	

	/**
	 * 下一个局部变量的索引
	 */
	public int nextLocalVarIndex(){
		return nextLocalVarIndex;
	}
	/**
	 * 存放局部变量，修改SlotTop
	 * 
	 * @param mv
	 * @param clazz
	 */
	public void storeLocalVar(MethodVisitor mv, Class<?> clazz) {
		if (clazz.isPrimitive()) {
			String pName = clazz.getName();
			switch (pName) {
			case "int":
			case "boolean":
			case "short":
			case "byte":
			case "char":
				mv.visitVarInsn(ISTORE, nextFreeSlotPos);
				localVarInfo.put(nextLocalVarIndex, new VarInfo(
						nextFreeSlotPos, ILOAD));
				break;
			case "float":
				mv.visitVarInsn(FSTORE, nextFreeSlotPos);
				localVarInfo.put(nextLocalVarIndex, new VarInfo(
						nextFreeSlotPos, FLOAD));
				break;
			case "double":
				mv.visitVarInsn(DSTORE, nextFreeSlotPos);// 32位机，double/long占两个slot
				localVarInfo.put(nextLocalVarIndex, new VarInfo(
						nextFreeSlotPos, DLOAD));
				nextFreeSlotPos++;
				break;
			case "long":
				mv.visitVarInsn(LSTORE, nextFreeSlotPos);
				localVarInfo.put(nextLocalVarIndex, new VarInfo(
						nextFreeSlotPos, LLOAD));
				nextFreeSlotPos++;
				break;
			default:
				throw new RuntimeException("不支持的类型" + clazz.getName());
			}
		} else {
			mv.visitVarInsn(ASTORE, nextFreeSlotPos);
			localVarInfo.put(nextLocalVarIndex, new VarInfo(nextFreeSlotPos,
					ALOAD));
		}
		nextFreeSlotPos++;
		nextLocalVarIndex++;
	}

	/**
	 * 加载临时变量
	 * 
	 * @param mv
	 * @param indexOfVar
	 */
	public void loadLocalVar(MethodVisitor mv, int indexOfVar) {
		if (localVarInfo.containsKey(indexOfVar)) {
			VarInfo vinfo = localVarInfo.get(indexOfVar);
			mv.visitVarInsn(vinfo.loadOpcode, vinfo.slotPos);
		} else {
			throw new RuntimeException("无法获取尚未设置的局部变量,变量编号:" + indexOfVar);
		}
	}
}
