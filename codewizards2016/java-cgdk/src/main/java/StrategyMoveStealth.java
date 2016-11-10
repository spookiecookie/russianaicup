import model.*;

/**
 * Created by m.zilenas on 2016-11-10.
 */
public class StrategyMoveStealth
    extends StrategyMove
{
    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self, world, game, move);
        //Go away from the nearest LivingUnit

        //Disable near wall since it is ineffective:(
        if (parameters().disableNearWall() && Obstacles.isNearWall(self))
        {
            return;
        }

        LivingUnit avoid = new Targets(world, game, self).livingUnits().foes().visible().distance().firstOrNull();
        if (avoid != null)
        {
            //move away from unit
            if (isBehind(self(), avoid))
            {
                //move forward
                forward();
                turn(0);
            }
            else
            {
                double angle = self().getAngleTo(avoid);
                double howMuch = Math.PI - Math.abs(angle);
                //Try to make angle 180.
                //While doing angle 180 strafe.
                if (howMuch != 0)
                {
                    turn(-1.0*Math.signum(angle)*howMuch);
                }
                if (Math.signum(angle) > 0)
                {
                    strafeLeft();
                }
                else
                {
                    strafeRight();
                }
                backward();
            }
        }
    }

    public boolean isBehind(Unit unit1, Unit unit2)
    {
        double angle = unit1.getAngleTo(unit2);
        return Math.abs(angle) < parameters().behindDelta();
    }
}
