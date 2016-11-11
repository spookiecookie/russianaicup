import model.*;
import java.util.*;
import java.awt.geom.Point2D;

/**
 * Created by m.zilenas on 2016-11-08.
 * Gets all nearest obstacles.
 */
public class Obstacles
{
    private LivingUnit unit;
    private World world;
    private Game game;
    private Parameters parameters;

    public Obstacles(LivingUnit unit, World world, Game game)
    {
        setUnit(unit);
        setWorld(world);
        setGame(game);
    }

    public Parameters parameters()
    {
        return parameters;
    }

    public Obstacles setParameters(Parameters parameters)
    {
        this.parameters = parameters;
        return this;
    }

    public World world()
    {
        return world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public Game game()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public class Vector
    {
        public boolean isZero()
        {
            return norm() == 0.0;
        }

        public List<Double> getList()
        {
            return list;
        }

        public String toString()
        {
            return Arrays.toString(getList().toArray());
        }

        public void setList(List<Double> list)
        {
            this.list = list;
        }

        List<Double> list = new LinkedList<>();

        public Vector(Unit unit)
        {
            this(unit.getX(), unit.getY());
        }

        public Vector(double... vals)
        {
            for(Double value : vals)
            {
                list.add(value);
            }
        }

        public int size()
        {
            return getList().size();
        }

        public Double a1()
        {return a(0);}

        public Double a2()
        {
            return a(1);
        }
        public Double a(int i)
        {
            return getList().get(i);
        }

        public Vector(List<Double> list)
        {
            this.list = new LinkedList<>(list);
        }

        public Vector multiplied(double value)
        {
            Vector vector = new Vector(size(), value);
            return multiplied(vector);
        }

        public Vector multiplied(Vector vector)
        {
            List<Double> list = new LinkedList<>(getList());
            ListIterator<Double> iterator = list.listIterator();
            Iterator<Double> itVector = vector.getList().iterator();

            double norm = norm();
            while(iterator.hasNext())
            {
                double value = iterator.next();
                iterator.set(value*itVector.next());
            }
            return new Vector(list);
        }

        public Vector (int size, double initialValue)
        {
            list = new LinkedList<>();
            for (int i = 0; i < size; i++)
            {
                list.add(initialValue);
            }
        }

        public Vector added(Vector vector)
        {
            List<Double> list = new LinkedList<>(getList());
            ListIterator<Double> iterator = list.listIterator();
            Iterator<Double> itVector = vector.getList().iterator();

            double norm = norm();
            while(iterator.hasNext())
            {
                double value = iterator.next();
                iterator.set(value+itVector.next());
            }
            return new Vector(list);
        }

        public Vector normalized()
        {
            List<Double> list = new LinkedList<>(getList());
            ListIterator<Double> iterator = list.listIterator();
            double norm = norm();
            while(iterator.hasNext())
            {
                double value = iterator.next();
                iterator.set(value/norm);
            }
            return new Vector(list);
        }

        public Vector half()
        {
            return multiplied(0.5);
        }

        public double norm()
        {
            double sum = 0.0;
            for(Double val : getList())
            {
                sum = Math.pow(val,2);
            }
            return Math.sqrt(sum);
        }

        //Not real distance
        public double distance(Vector vector)
        {
            return Math.sqrt(Math.pow(a1()-vector.a1(),2) + Math.pow(a2()-vector.a2(),2));
        }
    }
    /**
     * Check radius.
     * @return
     */
//    boolean ahead()
//    {
//        double radius = getUnit().getRadius();
//        return false;
//    }

    public double maxSeeAhead()
    {
        return unit.getRadius()*parameters().maxSeeAheadFactor();
    }

    public Vector avoidanceForce()
    {
        Vector avoidanceForce = new Vector(2,0.0);
        CircularUnit mostThreatening = mostThreatening(new Targets(world(),game(),unit).livingUnits());
        if (mostThreatening != null)
        {
            avoidanceForce = ahead()
                .added(
                        new Vector(mostThreatening).multiplied(-1.0))
                .normalized()
                .multiplied(parameters.maxAvoidForce());
        }
        else
        {
            avoidanceForce.multiplied(0.0);
        }
        return avoidanceForce;
    }

    /**
     * Finds most threatening object for collision detection.
     * @return
     */
    public CircularUnit mostThreatening(Targets.TargetsCollection units)
    {
        CircularUnit mostThreatening = null;
        for (CircularUnit underExamination : units.distance().list())
        {
            boolean collision = intersects(underExamination);
            if (collision)
            {
                mostThreatening = underExamination;
                break;
            }
        }
        return mostThreatening;
    }

    public boolean intersects(CircularUnit obstacle)
    {
        return ahead().distance(new Vector(obstacle)) <= obstacle.getRadius()
                || ahead2().distance((new Vector(obstacle))) <= obstacle.getRadius();
    }

    public Vector ahead()
    {
        //https://gamedevelopment.tutsplus.com/tutorials/understanding-steering-behaviors-collision-avoidance--gamedev-7777
        //ahead = position + normalize(velocity) * MAX_SEE_AHEAD
        Vector ahead = new Vector(unit.getX(), unit.getY())
                .added(
                        new Vector(unit.getSpeedX(), unit.getSpeedY())
                                .normalized()
                                .multiplied(new Vector(2, maxSeeAhead()))
                );

        return ahead;
    }

    public Vector ahead2()
    {
        return ahead().half();
    }

    /**
     * Wall segment.
     */
    private static class WallSegment
        extends CircularUnit
    {
        public static final double RADIUS = 1;

        public WallSegment(double x, double y)
        {
            super(1,x,y,0,0,0,Faction.OTHER, RADIUS);
        }
    }

    public static WallSegment nearestWallSegment(Unit unit)
    {
        List<WallSegment> list = new LinkedList<>(Walls.segments);
        Collections.sort(list, new ComparatorDistance(unit));
        return ((Deque<WallSegment>)list).peekFirst();
    }

    public static boolean isNearWall(Unit unit)
    {
        double near = ((CircularUnit) unit).getRadius() * 8.0;
        return unit.getX() <= near
                || unit.getX() >= Walls.worldX - near
                || unit.getY() <= near
                || unit.getY() >= Walls.worldY - near;
    }

    /**
     * Generated list of wall segments
     */
    private static class Walls
    {
        static final double worldX = 4000;
        static final double worldY = 4000;
        static final double ws = worldX/10;
        static List<WallSegment> segments = new LinkedList<>();

        static
        {
            for (int i = 0; i < ws; i++)
            {
                segments.add(new WallSegment(i , 0)) ;
                segments.add(new WallSegment(ws, i));
                segments.add(new WallSegment(ws-i ,ws)) ;//ws-i will run from the right to the left
                segments.add(new WallSegment(0 , ws-i));
            }
        }
    }

    private class WallDetctor
    {
        final int N = 360; //number of rays
        List<Point2D.Double> walls;

        public LivingUnit getUnit()
        {
            return unit;
        }

        public void setUnit(LivingUnit unit)
        {
            this.unit = unit;
        }

        LivingUnit unit;

        public void detectWalls()
        {
            Circle circle = new Circle(getUnit().getX(), getUnit().getY(), getUnit().getRadius());
            walls = new ArrayList<Point2D.Double>(N);

            for (int i = 0; i < N; i++)
            {
                walls.add(circle.point(i/2*Math.PI));
            }
        }

        private class Circle
        {
            public double getX0()
            {
                return x0;
            }

            public void setX0(double x0)
            {
                this.x0 = x0;
            }

            public double getY0()
            {
                return y0;
            }

            public void setY0(double y0)
            {
                this.y0 = y0;
            }

            double x0;
            double y0;

            public double getRadius()
            {
                return radius;
            }

            public void setRadius(double radius)
            {
                this.radius = radius;
            }

            double radius;
            public Circle(double x0, double y0, double radius)
            {
                setX0(x0);
                setY0(y0);
                setRadius(radius);
            }

            /**
             * Returns point on the circle.
             * @param rad
             * @return
             */
            public Point2D.Double point(double rad)
            {
                Point2D.Double p = new Point2D.Double();
                p.setLocation(
                        getX0() + getRadius()*Math.cos(rad),
                        getY0() + getRadius()*Math.sin(rad));
                return p;
            }
        }
        
        boolean outside(Point2D.Double p)
        {
            return outside(p.getX(), p.getY());
        }
        
        /**
         * Is the point x,y outside the walls.
         * @param x
         * @param y
         * @return
         */
        boolean outside(double x, double y)
        {
            return x < 0 || y < 0 || x > 4000 || y > 4000;
        }
    }

    private LivingUnit getUnit()
    {
        return unit;
    }

    private void setUnit(LivingUnit unit)
    {
        this.unit = unit;
    }
}
