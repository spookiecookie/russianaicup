import javafx.scene.paint.Color;
import model.*;

public final class MyStrategy
        implements Strategy
{
    public VisualClient client()
    {
        return client;
    }

    final static VisualClient client = new VisualClient("127.0.0.1", 31001);

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
//                    , new StrategyMoveCollisionAvoidance()
//                    .setParameters(new Parameters()
//                            .setMaxAvoidForce(4000.0)
//                            .setMaxSeeAheadFactor(3.0)
//                            .setTurnsSmoothly(false)
//                    )
            );

        if (world.getTickCount() > 50)
        {
            client().beginPost();
            client().fillCircle(3800, 3800, 20.0, java.awt.Color.BLACK);
            client().endPost();
        }
        setStrategy(strategy);
        getStrategy().run(self, world, game, move);
    }
}
