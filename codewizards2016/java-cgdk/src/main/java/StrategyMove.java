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

        speed();
        strafe();
        turn();
        action(ActionType.NONE);
    }
}
