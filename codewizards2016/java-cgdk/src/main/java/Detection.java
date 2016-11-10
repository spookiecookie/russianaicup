import model.CircularUnit;

/**
 * Created by m.zilenas on 2016-11-08.
 */
public class Detection
{
    public class Detector
    {
        private CircularUnit unit1;
        private CircularUnit unit2;
        private double withX;
        private double withY;

        public CircularUnit getUnit1()
        {
            return unit1;
        }

        public void setUnit1(CircularUnit unit1)
        {
            this.unit1 = unit1;
        }

        public CircularUnit getUnit2()
        {
            return unit2;
        }

        public void setUnit2(CircularUnit unit2)
        {
            this.unit2 = unit2;
        }

        double getWithX()
        {
            return withX;
        }

        void setWithX(double withX)
        {
            this.withX = withX;
        }

        double getWithY()
        {
            return withY;
        }

        void setWithY(double withY)
        {
            this.withY = withY;
        }

        public Detector(CircularUnit unit)
        {
            setUnit1(unit);
        }

        public boolean at(double x, double y)
        {
            return getUnit1().getRadius() + getUnit2().getRadius() <= getUnit1().getDistanceTo(x,y);
        }

        public Detector with(CircularUnit unit2)
        {
            setUnit2(unit2);
            return this;
        }

    }
    /**
     * Unit collides into another if radius1 + radius2 is less than the distance between unit1 and unit2 (at position withX,withY).
     * @return
     */
    public Detector collides(CircularUnit unit1)
    {
        return new Detector(unit1);
    }

    /**
     * Check if unit collides() with another.
     * @return
     */
    boolean ObstacleAhead()
    {
        return false;
    }
}
