package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.PoshTreeEvent;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Honza
 */
public class DriveCollectionTest {

    private int numDrives = 10;
    private DriveCollection instance;
    private String drivePrefix = "drive-no-";
    private List<DriveElement> drives;

    @Before
    public void setUp() {
        instance = LapElementsFactory.createDriveCollection();
        drives = new ArrayList<DriveElement>(numDrives);
        for (int driveNo = 0; driveNo < numDrives; ++driveNo) {
            drives.add(LapElementsFactory.createDriveElement(drivePrefix + driveNo));
        }
    }

    @Test
    public void testAddDrive() throws Exception {
        System.out.println("addDrive");
        for (DriveElement drive : drives) {
            CompetenceTest.DummyListener l = new CompetenceTest.DummyListener();
            instance.addElementListener(l);
            instance.addDrive(drive);

            assertTrue(instance.getDrives().contains(drive));
            assertEquals(drive, l.lastAddedChild);
            instance.removeElementListener(l);
        }
    }

    // XXX: Change to more general test for properties
    @Test
    public void testSetGetName() throws InvalidNameException {
        System.out.println("set/getName");

        System.out.println("get/setName");
        String namePrefix = "repeat.ad.nauseum-";
        String oldName = instance.getName();
        for (int i = 0; i < 1000; ++i) {
            CompetenceTest.DummyListener l = new CompetenceTest.DummyListener();
            instance.addElementListener(l);
            instance.setName(namePrefix + i);
            String expResult = namePrefix + i;
            String newName = instance.getName();
            assertEquals(expResult, newName);
            assertEquals(DriveCollection.dcName, l.lastPropertyChange.getPropertyName());
            assertEquals(oldName, l.lastPropertyChange.getOldValue());
            assertEquals(expResult, l.lastPropertyChange.getNewValue());
            instance.removeElementListener(l);
            oldName = instance.getName();
        }
    }

    @Test(expected=DuplicateNameException.class)
    public void testRemoveDriveDuplName() throws DuplicateNameException {
        System.out.println("removeDrive - duplicate name");

        instance.addDrive(LapElementsFactory.createDriveElement("Hello"));
        instance.addDrive(LapElementsFactory.createDriveElement("Hello"));
    }
    
    @Test
    public void testRemoveDrive() throws Exception {
        System.out.println("removeDrive");

        for (DriveElement drive : drives) {
            instance.addDrive(drive);
        }
        IElementMethod<DriveCollection, DriveElement> action = new IElementMethod<DriveCollection, DriveElement>() {

            @Override
            public void method(DriveCollection element, DriveElement child) {
                element.removeDrive(child);
            }
        };
        RemoveTest<DriveCollection, DriveElement> test = new RemoveTest<DriveCollection, DriveElement>(instance, drives, action, 1);
        test.runTest();
    }
}
