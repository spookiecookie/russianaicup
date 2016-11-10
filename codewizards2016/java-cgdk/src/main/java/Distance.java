import model.Unit;

/**
 * Created by m.zilenas on 2016-11-08.
 */
public class Distance
{
    double x, y;

    public static double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public Distance from(Unit unit)
    {
        return new Distance(unit.getX(), unit.getY());
    }

    public Distance(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double to(double x, double y)
    {
        return distance(this.x, this.y, x, y);
    }

//    public double to(Obstacles.WallSegment wallSegment)
//    {
//        return distance(x, y, wallSegment.x(), wallSegment().y());
//    }

    public double to(Unit unit)
    {
        return unit.getDistanceTo(x, y);
    }
}
