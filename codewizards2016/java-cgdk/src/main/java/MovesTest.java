import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by m.zilenas on 2016-11-09.
 */
public class MovesTest
{
    @Test
    public void moved() throws Exception
    {
        Moves moves = new Moves();
        moves.add(1, 1, 1);
        moves.add(1, 1, 2);
        moves.add(2, 2, 1);
        assertEquals(1, moves.moved(1), 1e-2);
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
    public void add() throws Exception
    {
        Moves moves = new Moves();
        moves.add(1, 1,1);
    }

    @org.junit.Test
    public void getCoordinates() throws Exception
    {
    }

    @org.junit.Test
    public void setCoordinates() throws Exception
    {

    }

    @org.junit.Test
    public void empty() throws Exception
    {
        Moves moves = new Moves();
        moves.add(1, 1,1);
        assertFalse(moves.empty());
    }

    @org.junit.Test
    public void hasMoved() throws Exception
    {
        Moves moves = new Moves();
        moves.add(1, 1,1);
        moves.add(1, 1,2);
        assertTrue(moves.hasMoved(1));
    }

}