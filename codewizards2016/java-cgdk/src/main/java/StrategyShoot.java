import model.*;

/**
 * Created by m.zilenas on 2016-11-09.
 */
public class StrategyShoot
    extends AbstractShootStrategy
{
    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self, world, game, move);
        shoot();
    }
}
