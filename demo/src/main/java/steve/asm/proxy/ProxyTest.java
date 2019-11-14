package steve.asm.proxy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import steve.asm.basic.People;
import steve.asm.basic.Person;
import steve.asm.classloader.MyClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author steve
 */
public class ProxyTest {
    public static void main(String[] args) throws IOException {

        People person = (People) Proxy.newProxyInstance(Person.class.getClassLoader(), new Class[]{People.class}, new PersonInvocationHandler());
        person.say();
    }

    private static void aopMethod() {
        System.out.println("aop method has been invoked......");
    }

    public static class PersonInvocationHandler implements InvocationHandler {
        private static final MyClassLoader CLASS_LOADER = new MyClassLoader();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("say")) {
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
                Class<?> aClass = CLASS_LOADER.initClass(Person.class.getName(), bytes);
                Object aInstance = CLASS_LOADER.loadClass(Person.class.getName()).getConstructor().newInstance();
                Method aMethod = aClass.getMethod("say");
                return aMethod.invoke(aInstance);
            }
            return null;
        }
    }

    public static class MyClassVisitor extends ClassVisitor {
        MyClassVisitor(int api, ClassVisitor classVisitor) {
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

    public static class AddTaskMethodAdapter extends MethodVisitor {


        AddTaskMethodAdapter(int api, MethodVisitor methodVisitor) {
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
