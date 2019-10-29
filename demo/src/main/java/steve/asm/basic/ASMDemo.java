package steve.asm.basic;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;

/**
 * @author steve
 */
public class ASMDemo {
    public static void main(String[] args) throws Exception {
        String currentPath = Person.class.getResource("").getFile();
        String classSimpleName = Person.class.getSimpleName();
        File file = new File(currentPath, classSimpleName + ".class");
        FileInputStream fileInputStream = new FileInputStream(file);
        ClassReader classReader = new ClassReader(fileInputStream);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        MyClassVisitor myClassVisitor = new MyClassVisitor(Opcodes.ASM7, classWriter);
        classReader.accept(myClassVisitor, ClassReader.EXPAND_FRAMES);
        byte[] bytes = classWriter.toByteArray();
        fileInputStream.close();
        MyClassLoader classLoader = new MyClassLoader();
        Class<?> aClass = classLoader.initClass(Person.class.getName(), bytes);
        Object o = classLoader.loadClass(Person.class.getName()).getConstructor().newInstance();
        Method say = aClass.getDeclaredMethod("say");
        say.invoke(o);
    }

    private static void aopMethod() {
        System.out.println("aop method has been invoked......");
    }

    public static class MyClassVisitor extends ClassVisitor {
        public MyClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if ("say".equals(name)) aopMethod();
            return new AddTaskMethodAdapter(Opcodes.ASM7, super.visitMethod(access, name, descriptor, signature, exceptions));
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }
    }

    public static class MyClassLoader extends ClassLoader {
        private byte[] data;

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return initClass(name, data);
        }

        public Class<?> initClass(String name, byte[] data) {
            this.data = data;
            return super.defineClass(name, data, 0, data.length);
        }

    }

    public static class AddTaskMethodAdapter extends MethodVisitor {


        public AddTaskMethodAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            System.out.println(name + ": enter method inside......");
            if (opcode == Opcodes.INVOKEVIRTUAL && "println".equals(name))
                System.out.println("println method has been invoked......");
            ;
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
