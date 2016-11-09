import model.*;

public final class MyStrategy
        implements Strategy
{
    public IStrategy getStrategy()
    {
        return strategy;
    }

    public void setStrategy(IStrategy strategy)
    {
        this.strategy = strategy;
    }

    private IStrategy strategy;

    @Override
    public void move(Wizard self, World world, Game game, Move move)
    {
        if (strategy == null)
            strategy = new StrategyMoveAlongLine();
        setStrategy(strategy);
        getStrategy().run(self, world, game, move);
    }
}
