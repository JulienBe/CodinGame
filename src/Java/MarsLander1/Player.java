package Java.MarsLander1;

import java.util.*;

/**
 * Flat land
 * Angle = 0
 * Horizontal Speed <= 20
 */
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        Ground ground = new Ground();
        ground.init(in);
        ground.landingSites = ground.findLandingSite();

        MarsLander lander = new MarsLander();

        // game loop
        while (true) {
            lander.update(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            lander.act(ground);
        }
    }
}


class Ground {

    // By linking all the points together in a sequential fashion, you form the surface of Mars.
    GroundPoint[] points;
    List<LandingSite> landingSites = new ArrayList<>();

    public void init(Scanner in) {
        points = new GroundPoint[in.nextInt()];
        for (int i = 0; i < points.length; i++)
            points[i] = new GroundPoint(in.nextInt(), in.nextInt());
    }

    List<LandingSite> findLandingSite() {
        List<LandingSite> landingSites = new ArrayList<>();
        for (int i = 1; i < points.length; i++) {
            if (points[i - 1].y == points[i].y) {
                GroundPoint a = points[i - 1];
                i = findLandingSiteLastPoint(i);
                landingSites.add(new LandingSite(a, points[i]));
            }
        }
        return landingSites;
    }

    int findLandingSiteLastPoint(int cursor) {
        for ( ;cursor < points.length; cursor++)
            if (points[cursor].y != points[cursor - 1].y)
                return cursor - 1;
        return points.length - 1;
    }

    int getNearestFlatPoint(MarsLander lander) {
        // /!\ null
        LandingSite bestCandidate = getBestLandingCandidate(lander);
        return bestCandidate.contains(lander.x) ? lander.x : bestCandidate.getDistanceNearestBorder(lander.x);
    }

    private LandingSite getBestLandingCandidate(MarsLander lander) {
        for (LandingSite site : landingSites)
            if (site.contains(lander.x))
                return site;
        int distance = 99999;
        LandingSite chosenOne = null;
        for (LandingSite site : landingSites) {
            int currentDistance = site.getDistanceNearestBorder(lander.x);
            if (currentDistance < distance) {
                distance = currentDistance;
                chosenOne = site;
            }
        }
        return chosenOne;
    }

}

class GroundPoint {
    int x, y;

    GroundPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class LandingSite {

    int y, xStart, xFinish;

    public LandingSite(GroundPoint a, GroundPoint groundPoint) {
        xStart = a.x;
        xFinish = groundPoint.x;
        y = a.y;
        if (a.y != groundPoint.y)
            throw new RuntimeException("You really should check your code");
    }

    int getDistanceNearestBorder(int x) {
        int fromLeft = Math.abs(xStart - x);
        int fromRight = Math.abs(xFinish - x);
        return fromRight < fromLeft ? fromRight : fromLeft;
    }

    boolean contains(int x) {
        return x >= xStart && x <= xFinish;
    }

}

class MarsLander {
    static final int MAX_ROTATION = 90, LANDING_MAX_SPEED = 45;
    static final float GRAVITY = -3.711f;
    int x, y, horizontalSpeed, verticalSpeed, remainingFuel, angle, thrusterPower;

    void update(int x, int y, int horizontalSpeed, int verticalSpeed, int remainingFuel, int angle, int thrusterPower) {
        this.x = x;
        this.y = y;
        this.horizontalSpeed = horizontalSpeed;
        this.verticalSpeed = verticalSpeed;
        this.remainingFuel = remainingFuel;
        this.angle = angle;
        this.thrusterPower = thrusterPower;
    }

    void act(Ground ground) {
        int steering = getSteering(ground);
        int thurster = getThruster(ground);
        System.out.println(steering + " " + thurster);
    }

    private int getThruster(Ground ground) {
        return Math.abs(getNextSpeed(thrusterPower)) > LANDING_MAX_SPEED ? capThruster(4) : capThruster(thrusterPower);
    }

    private int getSteering(Ground ground) {
        int target = ground.getNearestFlatPoint(this);
        if (target == x)
            return capAngle(-angle);
        return target > this.x ? 15 : -15;
    }

    /**
     * return the next speed based on the actual value of the thruster + the delta
     * @param thursterDelta
     * @return
     */
    float getNextSpeed(int thursterDelta) {
        return verticalSpeed + (GRAVITY + (thrusterPower + thursterDelta));
    }

    int capThruster(int wanted) {
        if (wanted - thrusterPower > 1)			return thrusterPower + 1;
        if (wanted - thrusterPower < -1)		return thrusterPower - 1;
        return wanted;
    }

    int capAngle(int i) {
        if (i > 15)		i = 15;
        if (i < -15)	i = -15;
        return i;
    }
}
