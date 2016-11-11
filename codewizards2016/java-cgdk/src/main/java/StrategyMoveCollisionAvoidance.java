import model.Game;
import model.Move;
import model.Wizard;
import model.World;

/**
 * Created by m.zilenas on 2016-11-11.
 */
public class StrategyMoveCollisionAvoidance
    extends StrategyMove
{
    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self, world, game, move);
        //If object ahead turn
        Obstacles obstacles = new Obstacles(self,world,game)
                .setParameters(parameters());
        Obstacles.Vector avoidanceForce = obstacles.avoidanceForce();
        if (!avoidanceForce.isZero())
        {
//            System.out.println("Turning");
            turn(self.getAngleTo(avoidanceForce.a1(), avoidanceForce.a2()));
        }
//        System.out.println("Avoidance force " + avoidanceForce);
    }
}
