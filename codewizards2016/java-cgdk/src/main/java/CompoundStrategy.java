import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.*;

/**
 * Created by m.zilenas on 2016-11-09.
 * Compose strategies.
 */
public class CompoundStrategy
    extends AbstractStrategy
{
    private List<IStrategy> strategies = new LinkedList<>();
    private List<IStrategy> strategies()
    {
        return strategies;
    }
    private void setStrategies(List<IStrategy> strategies)
    {
        this.strategies = strategies;
    }

    public CompoundStrategy(IStrategy... strategies)
    {
        strategies().addAll(Arrays.asList(strategies));
    }

    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        for (IStrategy strategy : strategies())
            strategy.run(self, world, game, move);
    }
}
