package MarsLander1;

import junit.framework.TestCase;
import org.junit.Test;

public class MarsLanderTest extends TestCase {

    private MarsLander marsLander;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        marsLander = new MarsLander();
        marsLander.update(0, 100, 0, 0, 100, 0, 2);
    }

    @Test
    public void testCapAngle() {
        assertEquals(   15,     marsLander.capAngle(16));
        assertEquals(   -15,    marsLander.capAngle(-16));
        assertEquals(14, marsLander.capAngle(14));
        assertEquals(   -14,    marsLander.capAngle(-14));
        assertEquals(0, marsLander.capAngle(0));
    }

    @Test    public void testCapThrusterToLow() {       assertEquals(   1,      marsLander.capThruster(0));    }
    @Test    public void testCapThrusterToHigh() {      assertEquals(   3,      marsLander.capThruster(4));    }
    @Test    public void testCapThrusterPlusOne() {     assertEquals(   3,      marsLander.capThruster(3));    }
    @Test    public void testCapThrusterNoChange() {    assertEquals(   2,      marsLander.capThruster(2));    }
    @Test    public void testCapThrusterMinusOne() {    assertEquals(   1,      marsLander.capThruster(1));    }


    @Test
    public void testNextSpeed() {
        assertEquals(MarsLander.GRAVITY + 1,    marsLander.getNextSpeed(-1));
    }
}