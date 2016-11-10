import java.util.*;
import model.*;

/**
 * Created by m.zilenas on 2016-11-09.
 */
public class StrategyShootNearest
    extends StrategyShoot
{
    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self, world, game, move);
        //Look around for the possible targets
        LivingUnit target = new Targets(world, game, self).wizards().foes().distance().visible().shootable().firstOrNull();
        if (target == null)
        {
            return;
        }
        else
        {
            dontMove();
            strafeRandom();
            setTarget(target);
            move().setTurn(self().getAngleTo(target));
            shoot(target);
        }
    }

    public void strafeRandom()
    {
        move().setStrafeSpeed(
                game().getWizardStrafeSpeed()
                        *( (double)(new Random().nextInt(200)) - 100)
        );
    }

    public void dontMove()
    {
        move().setSpeed(0);
    }

    public void cast()
    {
        move().setCastAngle(self().getAngleTo(target()));
    }

    public void shoot(LivingUnit target)
    {
        super.shoot();
    }
}
