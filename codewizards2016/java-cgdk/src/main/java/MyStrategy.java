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
                            .setTurnsSmoothly(true)
                    )
                    , new StrategyShootNearest()
                    , new StrategyMoveStealth()
                    .setParameters(
                            new Parameters()
                                    .setDisableNearWall(true)
                                    .setWallDistanceFactor(3.0)
                                    .setTurnsSmoothly(true)
                    )
                    , new StrategyMoveCollisionAvoidance()
                        .setParameters(new Parameters()
                                .setMaxAvoidForce(100.0)
                                .setTurnsSmoothly(false)
                        )
            );

        setStrategy(strategy);
        getStrategy().run(self, world, game, move);
    }
}
