package cz.cuni.amis.pogamut.sposh.elements;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Honza
 */
public class LapChainTest extends Assert {

    @Test
    public void linkPathIsSingleElement() {
        PoshPlan plan = LapElementsFactory.createPlan("life");
        LapPath linkPath = LapPath.getLinkPath(plan.getDriveCollection());
        LapPath expectedPath = new LapPath().concat(LapType.DRIVE_COLLECTION, 0);
        assertEquals(expectedPath, linkPath);
    }
    
    
}
