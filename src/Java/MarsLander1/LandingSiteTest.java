package Java.MarsLander1;

import junit.framework.TestCase;
import org.junit.Test;

public class LandingSiteTest extends TestCase {

    LandingSite landingSite;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        landingSite = new LandingSite(new GroundPoint(10, 10), new GroundPoint(20, 10));
    }

    @Test
    public void testDistanceNearestBorder() {
        assertEquals(0, landingSite.getDistanceNearestBorder(10));
        assertEquals(0, landingSite.getDistanceNearestBorder(20));
        assertEquals(2, landingSite.getDistanceNearestBorder(12));
        assertEquals(5, landingSite.getDistanceNearestBorder(15));
        assertEquals(1, landingSite.getDistanceNearestBorder(19));
    }

}