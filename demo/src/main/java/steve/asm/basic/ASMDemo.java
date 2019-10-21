package steve.asm.basic;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * @author steve
 */
public class ASMDemo {
    public static void main(String[] args) throws Exception{
        String currentPath = Person.class.getResource("").getFile();
        String classSimpleName = Person.class.getSimpleName();
        File file = new File(currentPath, classSimpleName + ".class");
        FileInputStream fileInputStream = new FileInputStream(file);
        ClassReader classReader = new ClassReader(fileInputStream);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        MyClassVisitor myClassVisitor = new MyClassVisitor(Opcodes.ASM7, classWriter);
        classReader.accept(myClassVisitor, ClassReader.EXPAND_FRAMES);
    }

    public static class MyClassVisitor extends ClassVisitor {
        public MyClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if ("say".equals(name)) {
                System.out.println("hello, word!");
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }
    }

    public static class MyClassLoader extends ClassLoader {
        private Module unnamedModule = null;

        public Class<?> defineClass(String name, byte[] data) {
            this.unnamedModule = this.getUnnamedModule();
            return this.defineClass(name, data, 0, data.length);
        }
    }
}
