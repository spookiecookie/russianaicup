import java.util.*;
/**
 * Records coordinates for all objects on the map each tick.
 * Created by m.zilenas on 2016-11-08.
 */
public class Moves
{
    public static class Point
    {
        double x;

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

        double y;
        public Point(double x, double y)
        {
            setX(x);
            setY(y);
        }
    }

    public Moves()
    {
        setCoordinates(new HashMap<>());
    }

    public Map<Long, Deque<Point>> getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Map<Long, Deque<Point>> coordinates)
    {
        this.coordinates = coordinates;
    }

    public Map<Long, Deque<Point>> coordinates;

    public boolean empty()
    {
        return getCoordinates().isEmpty();
    }

    public void add(long id, double x, double y)
    {
        Long key = id;
        Point point = new Point(x,y);

        if (!getCoordinates().containsKey(key))
        {
            Deque<Point> list = new LinkedList<>();
            list.addFirst(point);
            getCoordinates().put(key, list);
        }
        else
        {
            Deque<Point> list = getCoordinates().get(key);
            list.addFirst(point);
        }
    }

    public boolean hasMoved(long key)
    {
        return moved(key) != 0;
    }

    private Deque<Point> getPoints(Long key)
    {
        if (getCoordinates().containsKey(key))
            return getCoordinates().get(key);
        else
            return null;
    }


    /**
     * Returns distance moved on last move.
     */
    public double moved(long key)
    {
        Deque<Point> points = getPoints(key);
        if (points == null)
        {
            return 0;
        }
        else if (points.size() <= 1)
        {
            return 0;
        }
        else
        {
            Iterator<Point> iterator = points.iterator();
            Point p1 = iterator.next(), p2 = iterator.next();
            return Distance.distance(p1.x(), p1.y(), p2.x(), p2.y());
        }
    }
}
