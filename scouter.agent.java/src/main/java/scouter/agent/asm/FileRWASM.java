/*
 *  Copyright 2015 the original author or authors. 
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package scouter.agent.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;
import scouter.agent.ClassDesc;
import scouter.agent.Configure;
import scouter.agent.Logger;
import scouter.agent.asm.util.AsmUtil;
import scouter.agent.asm.util.HookingSet;
import scouter.agent.trace.TraceMain;


public class FileRWASM implements IASM, Opcodes {
	private HookingSet target = new HookingSet();
	public FileRWASM(){
		this.target.add("java/io/FileInputStream");
		this.target.add("java/io/FileOutputStream");
	}

	public ClassVisitor transform(ClassVisitor cv, String className, ClassDesc classDesc) {
		if (Configure.getInstance()._hook_file_enabled == false) {
			return cv;
		}
		if( target.contains(className)){
			Logger.println("A013", "FILE: " + className);
			return new FileOpenCv(cv,className);
		}

		return cv;
	}

}

// ///////////////////////////////////////////////////////////////////////////
class FileOpenCv extends ClassVisitor implements Opcodes {

	public String className;

	public FileOpenCv(ClassVisitor cv,String className) {
		super(ASM9, cv);
		this.className = className;
	}

	@Override
	public MethodVisitor visitMethod(int access, String methdName, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, methdName, desc, signature, exceptions);
		if(AsmUtil.isSpecial(methdName)){
			return mv;
		}
		if(methdName.equals("open") && desc.startsWith("(Ljava/lang/String;")) {
			return new FileOpenMV(access, desc, mv, this.className);
		}
		return mv;
	}
}

// ///////////////////////////////////////////////////////////////////////////
class FileOpenMV extends LocalVariablesSorter implements Opcodes {
	private static final String CLASS = TraceMain.class.getName().replace('.', '/');
	private static final String WRITE_METHOD_START = "openWriteFileStart";
	private static final String WRITE_METHOD_END = "openWriteFileEnd";
	private static final String READ_METHOD_START = "openReadFileStart";
	private static final String READ_METHOD_END = "openReadFileEnd";
	private static final String START_SIGNATURE = "(Ljava/lang/Object;)Ljava/lang/Object;";
	private static final String END_SIGNATURE = "(Ljava/lang/Object;Ljava/lang/Throwable;)V";

	private final String className;

	private Label startFinally = new Label();
	private int statIdx;

	public FileOpenMV(int access, String desc, MethodVisitor mv,String className) {
		super(ASM9, access, desc, mv);
		this.className = className;

	}

	@Override
	public void visitCode() {
		if (this.className.equals("java/io/FileOutputStream")) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS, WRITE_METHOD_START, START_SIGNATURE, false);
		}

		if (this.className.equals("java/io/FileInputStream")) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS, READ_METHOD_START, START_SIGNATURE, false);
		}
		statIdx = newLocal(Type.getType(Object.class));
		mv.visitVarInsn(Opcodes.ASTORE, statIdx);
		mv.visitLabel(startFinally);
		mv.visitCode();

	}
	@Override
	public void visitInsn(int opcode) {
		if ((opcode >= IRETURN && opcode <= RETURN)) {
			if (this.className.equals("java/io/FileOutputStream")) {
				mv.visitVarInsn(Opcodes.ALOAD, statIdx);
				mv.visitInsn(Opcodes.ACONST_NULL);
				this.mv.visitMethodInsn(INVOKESTATIC, CLASS, WRITE_METHOD_END, END_SIGNATURE, false);
			}
			if (this.className.equals("java/io/FileInputStream")) {
				mv.visitVarInsn(Opcodes.ALOAD, statIdx);
				mv.visitInsn(Opcodes.ACONST_NULL);
				this.mv.visitMethodInsn(INVOKESTATIC, CLASS, READ_METHOD_END, END_SIGNATURE, false);
			}
		}
		mv.visitInsn(opcode);
	}
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		Label endFinally = new Label();
		mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
		mv.visitLabel(endFinally);
		mv.visitInsn(DUP);
		int errIdx = newLocal(Type.getType(Throwable.class));
		mv.visitVarInsn(Opcodes.ASTORE, errIdx);

		mv.visitVarInsn(Opcodes.ALOAD, statIdx);
		mv.visitVarInsn(Opcodes.ALOAD, errIdx);
		if(this.className.equals("java/io/FileOutputStream")){
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS, WRITE_METHOD_END, END_SIGNATURE, false);
		}
		if (this.className.equals("java/io/FileInputStream")) {
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, CLASS, READ_METHOD_END, END_SIGNATURE, false);
		}
		mv.visitInsn(ATHROW);
		mv.visitMaxs(maxStack + 8, maxLocals + 2);


	}




}
