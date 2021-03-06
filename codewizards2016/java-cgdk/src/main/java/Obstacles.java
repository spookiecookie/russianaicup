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

    public Obstacles(LivingUnit unit)
    {
        setUnit(unit);
    }

    /**
     * Check radius.
     * @return
     */
    boolean ahead()
    {
        double radius = getUnit().getRadius();
        return false;
    }

    /**
     * A container for wall objects. A wall object is with radius 1.
     */
    private static class WallSegment
    {
        double x, y;

        public double x()
        {
            return x;
        }

        public void setX(double x)
        {
            this.x = x;
        }

        public double y()
        {
            return y;
        }

        public void setY(double y)
        {
            this.y = y;
        }

        public double radius()
        {
            return radius;
        }

        final double radius = 1;

        public WallSegment(double x, double y)
        {
            setX(x);
            setY(y);
        }
    }

    /**
     * Generated list of wall segments
     */
    private static class Walls
    {
        static final double worldX = 4000;
        static final double worldY = 4000;
        static List<WallSegment> segments;

        static
        {
            for (int x = 0; x < worldX; x++)
            {
                for (int y = 0; y < worldY; y++)
                {
                    segments.add(new WallSegment(x,y));
                }
            }
        }
    }

    private class WallDetector
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
