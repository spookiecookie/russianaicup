import model.*;

/**
 * Created by m.zilenas on 2016-11-09.
 */
public class AbstractShootStrategy
    extends AbstractStrategy
{
    private LivingUnit target;

    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self, world, game, move);
        shoot();
    }

    public void shoot()
    {
        action(ActionType.MAGIC_MISSILE);
    }

    public LivingUnit target()
    {
        return target;
    }

    public void setTarget(LivingUnit target)
    {
        this.target = target;
    }
}
