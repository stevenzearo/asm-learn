package steve.asm.basic;

import java.io.Serializable;

/**
 * @author steve
 */
public class Person implements Serializable, People {
    public String email;
    public String name;
    public Integer age;

    public Person() {
    }

    @Override
    public void say() {
        System.out.println("say method has been invoked......");
    }
/*
    static abstract class Child {
        String name;

        public Child() {
        }

        Child(String name) {
            this.name = name;
        }
    }

    public static class AChild extends Child {

        public AChild(String name) {
            super(name);
        }
    }*/

}
