package steve.asm.basic;

import java.io.Serializable;

/**
 * @author steve
 */
public class Person implements Serializable {
    public String email;
    public String name;
    public Integer age;

    public void say() {}

    static abstract class Child {
        String name;

        Child(String name) {
            this.name = name;
        }
    }

    public static class AChild extends Child {

        public AChild(String name) {
            super(name);
        }
    }

}
