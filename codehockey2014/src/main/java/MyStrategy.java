import model.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;
import java.awt.Point;

import static java.lang.StrictMath.*;

public final class MyStrategy
	implements Strategy
{
	private static final double STRIKE_ANGLE  = 1.0D * PI / 180.0D;
	public  static final double NEAR_DISTANCE = 30.0D;

	public World     world;
	public Game      game ;
	public Hockeyist self ;
	public Move      move ;

	public void setWorld(World world)
	{
		this.world = world;
	}

	public World getWorld()
	{
		return world;
	}

	public void setGame(Game game)
	{
		this.game = game;
	}

	public Game getGame()
	{
		return game;
	}

	public void setSelf(Hockeyist self)
	{
		this.self = self;
	}

	public Hockeyist getSelf()
	{
		return self;
	}

	public void setMove(Move move)
	{
		this.move = move;
	}

	public Move getMove()
	{
		return move;
	}

	public double getNearDistance()
	{
		return NEAR_DISTANCE;
	}

	/**
	 * Utility class to make a grid from world.
	 */
	class Grid
	{
		int width ;
		int height;

		public Grid(int width, int height)
		{
			this.width  = width;
			this.height = height;
		}
		
		public int getWidth()
		{
			return width;
		}

		public int getHeight()
		{
			return height;
		}

		public Point getCenter()
		{
			return new Point(
					(int) getWidth()/2, (int) getHeight()/2);
		}

		public List<Rectangle> tiles(int n)
		{
			List<Rectangle> quarters = new ArrayList<Rectangle>(n);
			int x0     = 0; 
			int y0     = 0;
			int sqrtn  = (int) Math.sqrt(n);
			int width  = (int) getWidth()  / sqrtn;
			int height = (int) getHeight() / sqrtn; 
			for (int y = 0; y < sqrtn; y++)
			{
				y0 = y*height;
				for (int x = 0; x < sqrtn; x++)
				{
					x0 = x*width;
					quarters.add(
							new Rectangle(x0, y0, width, height));
				}
			}
			return quarters;
		}

		public Rectangle getTile(int tiles, int t)
		{
			return tiles(tiles).get(t);
		}

		/**
		 * Returns tiles's center coordinates.
		 */
		public Point getCenter(int tiles, int t)
		{
			return new Point(
					(int) getTile(tiles,t).getCenterX(), 
					(int) getTile(tiles,t).getCenterY());
		}
	}

	public Grid getGrid()
	{
		return new Grid(
				(int) getWorld().getWidth(),
				(int) getWorld().getHeight());
	}

	public void init(Hockeyist self, World world , Game game, Move move)
	{
		setWorld(world);
		setGame(game);
		setSelf(self);
		setMove(move);
	}

	public boolean isNear(Unit unit, Point point)
	{
		return getNearDistance() >= unit.getDistanceTo(
				point.getX(), point.getY());
	}

	public boolean isPuckOur()
	{
		return getWorld().getPuck().getOwnerPlayerId() == getSelf().getPlayerId();
	}

	public boolean hasPuckSelf()
	{
		return getWorld().getPuck().getOwnerHockeyistId() == getSelf().getId();
	}

	public int getOpponentTile()
	{
		return getWorld().getOpponentPlayer().getNetFront() < 500 ? 2 : 3;
	}

	public void takePuck()
	{
		getMove().setAction(
				ActionType.TAKE_PUCK);
	}

	public boolean isReachable(Unit unit)
	{ 
		return getSelf().getDistanceTo(unit) < getGame().getStickLength()
				&& abs(getSelf().getAngleTo(unit)) < 0.5D * getGame().getStickSector();
	}

	/**
	 * Get hockeyist by id.
	 */ 
	public Hockeyist getHockeyist(long id)
	{
		Hockeyist hockeyist = null;
		for (Hockeyist hTemp : getWorld().getHockeyists())
		{
			if (id == hTemp.getId())
			{
				hockeyist = hTemp;
				break;
			}
		}
		return hockeyist;
	}

	public void strike(Unit unit)
	{
		if (null != unit && isReachable(unit))
		{
			getMove().setAction(ActionType.STRIKE);
			getMove().setTurn(getSelf().getAngleTo(unit));
		}
	}

	public Hockeyist getPuckHolder()
	{
		Hockeyist puckHolder = getHockeyist(
				getWorld().getPuck().getOwnerHockeyistId());
		return puckHolder;
	}

	public Point point(Unit unit)
	{
		return new Point(
				(int) unit.getX(),
				(int) unit.getY());
	}

    @Override
    public void move(Hockeyist self, World world, Game game, Move move)
    { 
		init(self, world, game, move );

        if (isPuckOur())
        {
            if (hasPuckSelf())
            {
                Player opponentPlayer = world.getOpponentPlayer();

                double netX = 0.5D * (opponentPlayer.getNetBack() + opponentPlayer.getNetFront());
                double netY = 0.5D * (opponentPlayer.getNetBottom() + opponentPlayer.getNetTop());
                netY += (self.getY() < netY ? 0.5D : -0.5D) * game.getGoalNetHeight();

                double angleToNet = self.getAngleTo(netX, netY);
                move.setTurn(angleToNet);

				Point center = getGrid().getCenter(4, getOpponentTile());

                if (abs(angleToNet) < STRIKE_ANGLE)
                {
					move.setAction(ActionType.STRIKE);
                }
				else
				{
					getMove().setSpeedUp(1.0D);

					if (point(getSelf()).distance(getGrid().getCenter()) 
							< point(getSelf()).distance(new Point((int) center.getX(), (int) getGrid().getCenter().getY())))
					{
						getMove().setTurn(
								getSelf().getAngleTo(
								center.getX(),
								center.getY())); 
					}
				}
            }
            else
            {
                Hockeyist nearestOpponent = getNearestOpponent(self.getX(), self.getY(), world);
                if (nearestOpponent != null)
                {
                    if (self.getDistanceTo(nearestOpponent) > game.getStickLength())
                    {
                        move.setSpeedUp(1.0D);
                    }
                    else if (abs(self.getAngleTo(nearestOpponent)) < 0.5D * game.getStickSector())
                    {
                        move.setAction(ActionType.STRIKE);
                    }
                    move.setTurn(self.getAngleTo(nearestOpponent));
                }
            }
        }
        else
        {
            move.setSpeedUp(1.0D);
            move.setTurn(
					self.getAngleTo(
						world.getPuck()));
			if (null!= getPuckHolder() && isReachable(getPuckHolder()))
			{
				strike(getPuckHolder());
			}
			else
			{
				takePuck();
			}
        }
    }

    private static Hockeyist getNearestOpponent(double x, double y, World world)
    {
        Hockeyist nearestOpponent = null;
        double nearestOpponentRange = 0.0D;

        for (Hockeyist hockeyist : world.getHockeyists())
        {
            if (hockeyist.isTeammate() || hockeyist.getType() == HockeyistType.GOALIE
                    || hockeyist.getState() == HockeyistState.KNOCKED_DOWN
                    || hockeyist.getState() == HockeyistState.RESTING)
            {
                continue;
            }

            double opponentRange = hypot(x - hockeyist.getX(), y - hockeyist.getY());

            if (nearestOpponent == null || opponentRange < nearestOpponentRange)
            {
                nearestOpponent = hockeyist;
                nearestOpponentRange = opponentRange;
            }
        }

        return nearestOpponent;
    }
}
