import java.util.*;
import model.*;

/**
 * Created by m.zilenas on 2016-11-09.
 */
public class Targets
{
    private final World world;
    private final  Game  game;
    private final  Unit    of;

    class TargetsCollection
    {
        private List<LivingUnit> targets = new LinkedList<>();

        public TargetsCollection(List<LivingUnit> targets)
        {
            this.targets = new LinkedList<>(targets);
        }

        public List<LivingUnit> getTargets()
        {
            return targets;
        }

        private boolean renegadeOrAcademy(Unit unit)
        {
            return unit.getFaction() == Faction.ACADEMY || unit.getFaction() == Faction.RENEGADES;
        }

        public boolean foes(Unit unit1, Unit unit2)
        {
            return renegadeOrAcademy(unit1)
                        && renegadeOrAcademy(unit2)
                        && unit1.getFaction() != unit2.getFaction();
        }

        public TargetsCollection foes()
        {
            Wizard wizard = (Wizard) of;
            List<LivingUnit> list = getTargets();
            ListIterator<LivingUnit> iterator = list.listIterator();
            LivingUnit unit = null;
            while (iterator.hasNext())
            {
                unit = iterator.next();
                if (!foes(of, unit))
                {
                    iterator.remove();
                }
            }
            return new TargetsCollection(list);
        }

        public LivingUnit firstOrNull()
        {
            if (getTargets().size() > 0)
                return getTargets().get(0);
            else
                return null;
        }

        public List<LivingUnit> list()
        {
            return getTargets();
        }

        public TargetsCollection order(Comparator comparator)
        {
            List<LivingUnit> list = getTargets();
            Collections.sort(list, comparator);
            return new TargetsCollection(list);
        }

        public TargetsCollection distance()
        {
            return order(new ComparatorDistance(of));
        }

        public TargetsCollection health()
        {
            return order(new ComparatorHealth());
        }

        public TargetsCollection visible()
        {
            Wizard wizard = (Wizard) of;
            List<LivingUnit> list = getTargets();
            ListIterator<LivingUnit> iterator = list.listIterator();
            LivingUnit unit = null;
            while (iterator.hasNext())
            {
                unit = iterator.next();
                if (wizard.getVisionRange() <= wizard.getDistanceTo(unit))
                {
                    iterator.remove();
                }
            }
            return new TargetsCollection(list);
        }

        public TargetsCollection shootable()
        {
            List<LivingUnit> list = getTargets();
            LivingUnit unit = null;
            ListIterator<LivingUnit> iterator = list.listIterator();
            Wizard wizard = (Wizard) of;
            while (iterator.hasNext())
            {
                unit = iterator.next();
                double angle = wizard.getAngleTo(unit);
                if (!(Math.abs(angle - of.getAngle()) <= getGame().getStaffRange()/2))
                {
                    iterator.remove();
                }
            }
            return new TargetsCollection(list);
        }
    }

    private class ComparatorDistance
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
            Unit unit1 = (Unit)o1;
            Unit unit2 = (Unit)o2;

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

    private class ComparatorHealth
        implements Comparator
    {
        @Override
        public int compare(Object o1, Object o2)
        {
            LivingUnit unit1 = (LivingUnit) o1;
            LivingUnit unit2 = (LivingUnit) o2;
            return Integer.compare(unit1.getLife(), unit2.getLife());
        }

        @Override
        public boolean equals(Object obj)
        {
            return false;
        }
    }

    public Targets(World world, Game game, Unit of)
    {
        this.world = world;
        this.game  =  game;
        this.of    =    of;
    }

    public World getWorld()
    {
        return world;
    }
    public Game  getGame()
    {
        return game;
    }

    public List<LivingUnit> buildingsList()
    {
        return new LinkedList<>(Arrays.asList(getWorld().getBuildings()));
    }

    public List<LivingUnit> minionsList()
    {
        return new LinkedList<>(Arrays.asList(getWorld().getMinions()));
    }

    public List<LivingUnit> treesList()
    {
        return new LinkedList<LivingUnit>(Arrays.asList(getWorld().getTrees()));
    }

    public List<LivingUnit> wizardsList()
    {
        return new LinkedList<>(Arrays.asList(getWorld().getWizards()));
    }

    public TargetsCollection wizards()
    {
        return new TargetsCollection(wizardsList());
    }

    /**
     * @return All living units from the world.
     */
    public TargetsCollection livingUnits()
    {
        List<LivingUnit> units = new LinkedList<>();

        units.addAll(buildingsList());
        units.addAll(minionsList());
        units.addAll(treesList());
        units.addAll(wizardsList());

        return new TargetsCollection(units);
    }
}
