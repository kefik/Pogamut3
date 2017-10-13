/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection;

import cz.cuni.amis.introspection.java.Introspectable;
import cz.cuni.amis.introspection.java.Introspector;
import cz.cuni.amis.introspection.java.JFolder;
import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.introspection.java.ReflectionObjectFolder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ik
 */
public class Test01_JavaIntrospection {

    static final int KNOWS_AGE = 25;

    public static class Person {

        @JProp
        public  String name;
        @JProp
        public int age;
        @JFolder
        public Person knows;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public class PersonCustomView extends Person implements Introspectable {

        Folder folder;

        public PersonCustomView(String name, int age) {
            super(name, age);
            
        }

        @Override
        public Folder getFolder(String name) {
            if(folder == null) {
                folder = new FolderUnion(
                    new ReflectionObjectFolder(name, this),
                    new Folder(name) {

                        @Override
                        public Folder[] getFolders() {
                            return new Folder[0];
                        }

                        @Override
                        public Property[] getProperties() {
                            return new Property[]{
                                        new Property("myCustomProp") {

                                            String myVal = "myCustomValue";

                                            @Override
                                            public Object getValue() throws IntrospectionException {
                                                return myVal;
                                            }

                                            @Override
                                            public void setValue(Object newValue) throws IntrospectionException {
                                                myVal = (String) newValue;
                                            }

                                            @Override
                                            public Class getType() {
                                                return String.class;
                                            }
                                        }
                                    };
                        }
                    }, name);
            }
            return folder;
        }
    }
    Person alice = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        alice = new Person("Alice", 23);
        alice.knows = new Person("Bob", KNOWS_AGE);
    }

    @After
    public void tearDown() {
    }    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void saveProperties() throws IOException, IntrospectionException {
        Folder folder = Introspector.getFolder("Root", alice);
        Properties props = folder.createProperties();
        props.store(new FileOutputStream("test.properties"), "Test file with properties");
    }

    @Test
    public void loadFromProperties() throws FileNotFoundException, IOException, IntrospectionException {
        // set new values
        alice.name = "AAlliiccee";
        alice.knows.age = 100;

        // rewrite them with the old ones in properties file
        Properties props = new Properties();
        props.load(new FileInputStream("target/test-classes/test.properties"));
        Folder folder = Introspector.getFolder("Root", alice);
        folder.loadFromProperties(props);
        assertEquals(KNOWS_AGE, alice.knows.age);
    }

    @Test
    public void saveCustomProperties() throws IOException, IntrospectionException {
        alice.knows = new PersonCustomView("Fred", 25);

        Folder folder = Introspector.getFolder("Root", alice);
        Properties props = folder.createProperties();
        props.store(new FileOutputStream("test.properties"), "Test file with properties");
    }
}