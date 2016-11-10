import model.Game;
import model.Move;
import model.Wizard;
import model.World;

/**
 * Created by m.zilenas on 2016-11-08.
 */
public interface IStrategy
{
    Wizard self();
     World world();
      Game game();
      Move move();
    void run(Wizard wizard, World world, Game game, Move move);
}
