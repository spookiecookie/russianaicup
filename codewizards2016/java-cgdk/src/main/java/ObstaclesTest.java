import model.CircularUnit;
import model.Faction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by m.zilenas on 2016-11-10.
 */
public class ObstaclesTest
{
    private class MyTestUnit
        extends CircularUnit
    {
        public MyTestUnit(double x, double y, double r)
        {
            super(1,x,y,0,0,0, Faction.ACADEMY,r);

        }
    }
    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void isNearWall() throws Exception
    {
        CircularUnit unit = new MyTestUnit(1,1,1);
        assertTrue(Obstacles.isNearWall(unit));
        CircularUnit unit2 = new MyTestUnit(50,50,9);
        assertTrue(Obstacles.isNearWall(unit2));
    }

    @Test
    public void isNotNearWall() throws Exception
    {
        CircularUnit unit = new MyTestUnit(50,50,1);
        assertFalse(Obstacles.isNearWall(unit));
    }

}