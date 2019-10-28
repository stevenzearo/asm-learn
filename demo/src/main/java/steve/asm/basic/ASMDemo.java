package steve.asm.basic;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

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
        System.out.println(aClass.getConstructors().length);
        Method say = aClass.getDeclaredMethod("say");
        Object invoke = say.invoke(o);

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
            return new AddTaskMethodAdapter(Opcodes.ASM7);
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

        AddTaskMethodAdapter(int api) {
            super(api);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (opcode == Opcodes.INVOKEVIRTUAL && "say".equals(name))
                aopMethod();
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
