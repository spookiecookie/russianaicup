import model.Game;
import model.Move;
import model.Wizard;
import model.World;

/**
 * Created by m.zilenas on 2016-11-08.
 */
public class AbstractStrategy
        implements IStrategy
{
    private Wizard _self;
    private World _world;
    private Game _game;
    private Move _move;

    public AbstractStrategy()
    {
    } //do not pass self, world, game, move because they are recreated on each tick.

    public Wizard self()
    {
        return _self;
    }

    public World world()
    {
        return _world;
    }

    public Game game()
    {
        return _game;
    }

    public Move move()
    {
        return _move;
    }

    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
         _self = self;
        _world = world;
         _game = game;
         _move = move;
    }

    public void turn(double turn)
    {
        move().setTurn(turn);
    }
}
