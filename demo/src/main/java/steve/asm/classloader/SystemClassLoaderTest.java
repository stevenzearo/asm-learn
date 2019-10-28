package steve.asm.classloader;

import steve.asm.basic.Person;

import java.lang.reflect.Method;

/**
 * @author steve
 */
public class SystemClassLoaderTest {
    public static void main(String[] args) throws Exception {
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass(Person.class.getName());
        Object o = aClass.getDeclaredConstructor().newInstance();
        Method say = aClass.getDeclaredMethod("say");
        Object invoke = say.invoke(o);
    }
}
