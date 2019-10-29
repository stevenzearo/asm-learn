package steve.asm.classloader;

/**
 * @author steve
 */
public class MyClassLoader extends ClassLoader {
    public Class<?> initClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
