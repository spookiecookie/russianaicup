import model.*;

public final class MyStrategy
        implements Strategy
{
    private IStrategy strategy;
     public IStrategy getStrategy()
    {
        return strategy;
    }
     public      void setStrategy(IStrategy strategy)
    {
        this.strategy = strategy;
    }

    @Override
    public void move(Wizard self, World world, Game game, Move move)
    {
        if (strategy == null)
            strategy = new CompoundStrategy(
                    new StrategyMoveAlongLine()
                    .setParameters(
                        new Parameters()
                            .turnsSmoothly(true)
                    )
                    , new StrategyShootNearest()
            );

        setStrategy(strategy);
        getStrategy().run(self, world, game, move);
    }
}
