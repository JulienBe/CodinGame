package Java.MarsLander1;

import junit.framework.TestCase;
import org.junit.Test;

import javax.xml.transform.sax.SAXSource;
import java.util.List;

import static org.junit.Assert.*;

public class GroundTest extends TestCase {

    Ground ground;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ground = new Ground();
        /**      .
         *   .  . .  .
         *    ..   ..
         */
        ground.points = new GroundPoint[9];
        ground.points[0] = new GroundPoint(0, 10);
        ground.points[1] = new GroundPoint(1, 0);
        ground.points[2] = new GroundPoint(2, 0);
        ground.points[3] = new GroundPoint(3, 10);
        ground.points[4] = new GroundPoint(4, 20);
        ground.points[5] = new GroundPoint(5, 10);
        ground.points[6] = new GroundPoint(6, 0);
        ground.points[7] = new GroundPoint(7, 0);
        ground.points[8] = new GroundPoint(8, 10);
        ground.landingSites =  ground.findLandingSite();
    }

    @Test
    public void testNumberOfFindLandingSites() {
        assertEquals(2, ground.landingSites.size());
    }

    @Test
    public void testWidthOfLandingSites() {
        for (LandingSite site : ground.landingSites)
            assertEquals(1, site.xFinish - site.xStart);
    }

}