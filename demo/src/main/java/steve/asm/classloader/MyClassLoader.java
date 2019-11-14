package steve.asm.classloader;

import jdk.internal.loader.ClassLoaders;

import java.util.Vector;

/**
 * @author steve
 */
public class MyClassLoader extends ClassLoader {

    public Class<?> initClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
