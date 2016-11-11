import model.*;

/**
 * Created by m.zilenas on 2016-11-11.
 */
public class FollowFriendlyWizard
    extends StrategyMove
{
    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self, world, game, move);
        LivingUnit friend = new Targets(world, game, self).wizards().friends().distance().firstOrNull();
        if (friend != null)
        {
            move().setTurn(self.getAngleTo(friend));
        }
    }
}
