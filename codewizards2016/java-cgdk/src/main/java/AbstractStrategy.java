import model.Game;
import model.Move;
import model.Wizard;
import model.World;
import model.ActionType;

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

    public Parameters parameters()
    {
        return parameters;
    }

    public AbstractStrategy setParameters(Parameters parameters)
    {
        this.parameters = parameters;
        return this;
    }

    private Parameters parameters;

    /**
     * do not pass self, world, game, move because they are recreated on each tick.
     */
    public AbstractStrategy()
    {
    }

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

    /**
     * Does not much.
     * @param self
     * @param world
     * @param game
     * @param move
     */
    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
         _self = self;
        _world = world;
         _game = game;
         _move = move;
    }

    public void speed(double speed)
    {
        move().setSpeed(speed);
    }

    public void speed()
    {
        speed(game().getWizardForwardSpeed());
    }

    public void turn(double turn)
    {
        move().setTurn(turn);
    }

    public void turn()
    {
        turn(game().getWizardMaxTurnAngle());
    }

    public void strafe()
    {
        strafe(game().getWizardStrafeSpeed());
    }

    public void strafe(double strafe)
    {
        move().setStrafeSpeed(strafe);
    }

    public void action()
    {
        action(ActionType.NONE);
    }

    public void action(ActionType action)
    {
        move().setAction(action);
    }
}
