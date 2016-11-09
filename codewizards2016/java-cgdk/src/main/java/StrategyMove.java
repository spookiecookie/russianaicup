import model.*;

/**
 * Created by m.zilenas on 2016-11-08.
 */

public class StrategyMove
        extends AbstractStrategy
{
    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self,world,game,move);

        setSpeed();
        setStrafeSpeed();
        setTurn();
        move.setAction(ActionType.NONE);
    }

    void setSpeed()
    {
        move().setSpeed(game().getWizardForwardSpeed());
    }

    void setTurn(double angle)
    {
        turn(angle);
    }

    void setTurn()
    {
        turn(game().getWizardMaxTurnAngle());
    }

    void setStrafeSpeed()
    {
        move().setStrafeSpeed(game().getWizardStrafeSpeed());
    }
}
