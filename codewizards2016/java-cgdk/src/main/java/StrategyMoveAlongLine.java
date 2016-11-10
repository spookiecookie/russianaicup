import model.*;

/**
 * Created by m.zilenas on 2016-11-08.
 * This strategy just moves forward and turns on obstacle.
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

        strafe(0);
        turn(0);

        long id = self.getId();
        moves.add(self.getId(), self.getX(), self.getY());

        /*
          Possible directions to move.
          Detect directions the unit can move next.
          "Move along the line" is moving forward until bumps, then turns right and moves until bumps, then turns right...
         */
        if (!moves.hasMoved(id))
        {
            turn();
        }
        else
        {
            turn(0);
        }
    }
}
