import model.*;

/**
 * Created by m.zilenas on 2016-11-08.
 */
public class StrategyMoveAlongLine
    extends StrategyMove
{
    Moves moves = new Moves();

    public Moves moves()
    {
        return moves;
    }

    @Override
    public void run(Wizard self, World world, Game game, Move move)
    {
        super.run(self, world, game, move);
        setStrafeSpeed(0);
        setTurn(0);

        long id = self.getId();
        moves.add(self.getId(), self.getX(), self.getY());

        /*
          Possible directions to move.
          Detect directions the unit can move next.
          "Move along the line" is moving forward until bumps, then turns right and moves until bumps, then turns right...
         */
        if (!moves.hasMoved(id))
        {
            setTurn();
        }
        else
        {
            setTurn(0);
        }
    }

    void setStrafeSpeed(double strafeSpeed)
    {
        super.setStrafeSpeed();
        move().setStrafeSpeed(strafeSpeed);
    }

    void setSpeed(double speed)
    {
        super.setSpeed();
        move().setSpeed(speed);
    }

    /**
     * If obstacle turn right and make step forward.
     */
    void setTurn(double angle)
    {
        super.setTurn(angle);
    }

    @Override
    void setTurn()
    {
        super.setTurn();
    }

}
