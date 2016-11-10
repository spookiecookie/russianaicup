import model.Unit;

import java.util.Comparator;

/**
 * Created by m.zilenas on 2016-11-10.
 */
class ComparatorDistance
        implements Comparator
{
    Unit unit;

    public ComparatorDistance(Unit unit)
    {
        this.unit = unit;
    }

    @Override
    public int compare(Object o1, Object o2)
    {
        Unit unit1 = (Unit) o1;
        Unit unit2 = (Unit) o2;

        return Double.compare(
                unit.getDistanceTo(unit1),
                unit.getDistanceTo(unit2));
    }

    @Override
    public boolean equals(Object obj)
    {
        return false;
    }
}
