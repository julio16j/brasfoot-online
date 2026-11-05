package brasfoot;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.*;

/** Host-only: replace the Swing navigator with inert methods, leaving game rules intact. */
public final class HostUiAgent {
    public static void premain(String options,Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader,String name,Class<?> type,ProtectionDomain domain,byte[] bytes) {
                if(!"best/S".equals(name))return null;
                ClassReader reader=new ClassReader(bytes);
                final ClassWriter writer=new ClassWriter(0);
                reader.accept(new ClassVisitor(Opcodes.ASM4,writer) {
                    @Override public MethodVisitor visitMethod(int access,String method,String descriptor,String signature,String[] exceptions) {
                        MethodVisitor out=writer.visitMethod(access,method,descriptor,signature,exceptions);
                        out.visitCode();
                        if(method.equals("<init>")){out.visitVarInsn(Opcodes.ALOAD,0);out.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/Object","<init>","()V");}
                        Type result=Type.getReturnType(descriptor);
                        switch(result.getSort()) {
                            case Type.VOID:out.visitInsn(Opcodes.RETURN);break;
                            case Type.LONG:out.visitInsn(Opcodes.LCONST_0);out.visitInsn(Opcodes.LRETURN);break;
                            case Type.DOUBLE:out.visitInsn(Opcodes.DCONST_0);out.visitInsn(Opcodes.DRETURN);break;
                            case Type.FLOAT:out.visitInsn(Opcodes.FCONST_0);out.visitInsn(Opcodes.FRETURN);break;
                            case Type.OBJECT:case Type.ARRAY:out.visitInsn(Opcodes.ACONST_NULL);out.visitInsn(Opcodes.ARETURN);break;
                            default:out.visitInsn(Opcodes.ICONST_0);out.visitInsn(Opcodes.IRETURN);
                        }
                        int locals=(access&Opcodes.ACC_STATIC)==0?1:0;
                        for(Type argument:Type.getArgumentTypes(descriptor))locals+=argument.getSize();
                        out.visitMaxs(2,locals);out.visitEnd();return null;
                    }
                },ClassReader.SKIP_CODE|ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
                return writer.toByteArray();
            }
        });
        System.setProperty("brasfoot.host.ui.disabled","true");
    }
}
