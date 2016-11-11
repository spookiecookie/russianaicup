/**
 * Created by m.zilenas on 2016-11-09.
 */
public class Parameters
{
    static final boolean  TURNS_SMOOTHLY = true;
    static final    int        CLOCKWISE = +1;
    static final    int COUNTERCLOCKWISE = -1;
    static final double      SMOOTH_TURN = Math.toRadians(15);
    static final double             TURN = Math.toRadians(30);
    static final double    BEHIND_DELTA  = Math.toRadians(60);//Object is behind if angle to is 180 - BEHIND_DELTA.
    static final boolean DISABLE_NEAR_WALL= true;
    static final double WALL_DISTANCE_FACTOR = 3.0;
    static final double MAX_SEE_AHEAD_FACTOR = 4.0;
    static final double MAX_AVOID_FORCE      = 5.0;


    private double smoothTurn = SMOOTH_TURN;
    private double       turn = TURN       ; //default turn
    private double   turnSide = CLOCKWISE  ; //to which side turn
    private double behindDelta= BEHIND_DELTA;
    private boolean disableNearWall = true;
    private double wallDistanceFactor = WALL_DISTANCE_FACTOR;
    private double maxSeeAheadFactor = MAX_SEE_AHEAD_FACTOR;

    public double maxAvoidForce()
    {
        return maxAvoidForce;
    }

    public Parameters setMaxAvoidForce(double maxAvoidForce)
    {
        this.maxAvoidForce = maxAvoidForce;
        return this;
    }

    private double maxAvoidForce = MAX_AVOID_FORCE;

    public boolean turnsSmoothly()
    {
        return turnsSmoothly;
    }

    private boolean turnsSmoothly = TURNS_SMOOTHLY;

    public Parameters setDisableNearWall(boolean disableNearWall)
    {
        this.disableNearWall = disableNearWall;
        return this;
    }

    public boolean disableNearWall()
    {
        return disableNearWall;
    }

    public double behindDelta()
    {
        return behindDelta;
    }

    public double wallDistanceFactor()
    {
        return wallDistanceFactor;
    }

    public Parameters setWallDistanceFactor(double wallDistanceFactor)
    {
        this.wallDistanceFactor = wallDistanceFactor;
        return this;
    }

    public Parameters setBehindDelta(double behindDelta)
    {
        this.behindDelta = behindDelta;
        return this;
    }

    public double getTurn()
    {
        if (turnsSmoothly())
        {
            return getSmoothTurn();
        }
        else
        {
            return getTurn();
        }
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

    public Parameters setTurnsSmoothly(boolean turnsSmoothly)
    {
        this.turnsSmoothly = turnsSmoothly;
        return this;
    }

    public double maxSeeAheadFactor()
    {
        return maxSeeAheadFactor;
    }

    public void setMaxSeeAheadFactor(double maxSeeAheadFactor)
    {
        this.maxSeeAheadFactor = maxSeeAheadFactor;
    }
}
