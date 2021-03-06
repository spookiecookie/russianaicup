/**
 * Created by m.zilenas on 2016-11-09.
 */
public class Parameters
{
    static final    int        CLOCKWISE = +1;
    static final    int COUNTERCLOCKWISE = -1;
    static final double      SMOOTH_TURN = Math.toRadians(15);
    static final double             TURN = Math.toRadians(30);

    private double smoothTurn = SMOOTH_TURN;
    private double       turn = TURN       ; //default turn
    private double   turnSide = CLOCKWISE  ; //to which side turn

    public double getTurn()
    {
        return turn;
    }

    public Parameters setTurn(double turn)
    {
        this.turn = turn;
        return this;
    }

    public double getSmoothTurn()
    {
        return smoothTurn;
    }

    public Parameters setSmoothTurn(double smoothTurn)
    {
        this.smoothTurn = smoothTurn;
        return this;
    }

    public Parameters turnsSmoothly(boolean turnsSmoothly)
    {
        setTurn(getSmoothTurn());
        return this;
    }

}
