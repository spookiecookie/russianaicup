import model.*;

import java.util.Random;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Stack;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.io.PrintStream;

/**
 * @author Marius Žilėnas <mzilenas@gmail.com>
 */
public final class MyStrategy 
	implements Strategy {

    private final Random random = new Random();
	private final PrintStream out = System.out;

	/** Home base */
	static Integer home = null;
	static List bases = 
		new ArrayList<Integer>();
	static List unraidedBases = 
		new ArrayList<Integer>();

	/** Players */
	List<Player> players;

	Trooper self ;
	World   world;
	Game    game ;
	Move    move ;
	List<CellType> cells;
	List<Integer>  freeCells;
	List<Trooper>  troopers;

	Trooper commander  ;
	Trooper field_medic;
	Trooper soldier    ;
	Trooper sniper     ;
	Trooper scout      ;

	Integer destination      = null;
	static Order globalOrder = null;

	void setUnraidedBases(List unraidedBases) {
		this.unraidedBases = unraidedBases;
	}
	List getUnraidedBases() {
		return unraidedBases;
	}
	List unraidedBases() {
		return getUnraidedBases();
	}
	Order getGlobalOrder() {
		return globalOrder;
	}
	Order globalOrder() {
		return getGlobalOrder();
	}
	void setGlobalOrder(Order globalOrder) {
		this.globalOrder = globalOrder;
	}
	/** @return center cell index */
	Integer getCenter() {
		return idx(getWidth()/2, getHeight()/2);
	}
	Integer center() {
		return getCenter();
	}
	void initHome() {
		/** Make bases() return foe's bases */
		/** Set home to commander's initial cell */
		if (null == home()) {
			setHome(idx(commander()));
		}
	}
	void initUnraidedBases() {
		setUnraidedBases(bases());
	}
	/** Puts coordinates of bases to the list */ 
	/**
	 * q1   q4
	 *    .
	 * q2   q3
	 */
	void initBases() {
		int xh = x(home());
		int yh = y(home());
		int xc = x(center());
		int yc = y(center());
		int x;
		int y;

		List bases = new ArrayList<Integer>();
		if (xh < xc && yh < yc) {        /** q1 */
			/** q2 */
			x = xh;
			y = getHeight() - yh;
			bases.add(idx(x,y));
			/** q3 */
			x = getWidth() - xh;
			y = getHeight() - yh;
			bases.add(idx(x,y));
			/** q4 */
			x = getWidth() - xh;
			y = yh;
			bases.add(idx(x,y));
		} else if (xh < xc && yh > yc) { /** q2 */
			/** q3 */
			x = getWidth() - xh; 
			y = yh;
			bases.add(idx(x,y));
			/** q4 */
			x = getWidth()  - xh;
			y = getHeight() - yh; 
			bases.add(idx(x,y));
			/** q1 */
			x = xh;
			y = getHeight() - yh;
			bases.add(idx(x,y));
		} else if (xh > xc && yh > yc) { /** q3 */
			/** q4 */
			x = xh;
			y = getHeight() - yh;
			bases.add(idx(x,y));
			/** q1 */
			x = getWidth() - xh;
			y = getHeight() - yh;
			bases.add(idx(x,y));
			/** q2 */ 
			x = getWidth() - xh;
			y = yh;
			bases.add(idx(x,y));
		} else if (xh > xc && yh < yc) { /** q4 */
			/** q1 */
			x = getWidth() - xh;
			y = yh;
			bases.add(idx(x,y));
			/** q2 */
			x = getWidth() - xh;
			y = getHeight() - yh;
			bases.add(idx(x,y));
			/** q3 */
			x = xh;
			y = getHeight() - yh;
			bases.add(idx(x,y));
		}
		setBases(bases);
	}
	void setHome(Integer home) {
		this.home = home;
	}
	Integer getHome() {
		return home;
	}
	Integer home() {
		return getHome();
	}
	void setBases(List bases) {
		this.bases = bases;
	}
	List getBases() {
		return bases;
	}
	List bases() {
		return getBases();
	}
	boolean isDead(Trooper trooper) {
		return null == trooper || 0 >= health(trooper);
	}
	boolean isMoving() {
		return ActionType.MOVE == action();
	}
	boolean isHealing() {
		return self() == medic() 
			? (ActionType.HEAL == action() 
					|| ActionType.USE_MEDIKIT == action())
			: ActionType.USE_MEDIKIT == action();
	}
	boolean isAttacking() {
		return isShooting() || isThrowingGrenade();
	}
	boolean isShooting() {
		return ActionType.SHOOT == action();
	}
	boolean isThrowingGrenade() {
		return ActionType.THROW_GRENADE == action();
	}
	void setSelf(Trooper self) {
		this.self = mySelf(self);
	}
	void setWorld(World world) {
		this.world = world;
	}
	void setMove(Move move) {
		this.move = move;
	}
	void setGame(Game game) {
		this.game = game;
	}
	void setCommander(Trooper commander) {
		this.commander = commander;
	}
	void setFieldMedic(Trooper field_medic) {
		this.field_medic = field_medic;
	}
	void setSoldier(Trooper soldier) {
		this.soldier = soldier;
	}
	void setSniper(Trooper sniper) {
		this.sniper = sniper;
	}
	void setScout(Trooper scout) {
		this.scout = scout;
	}
	Trooper getCommander() {
		return commander;
	}
	Trooper commander() {
		return getCommander();
	}
	Trooper getMedic() {
		return field_medic;
	}
	Trooper medic() {
		return getMedic();
	}
	Trooper getSoldier() {
		return soldier;
	}
	Trooper soldier() {
		return getSoldier();
	}
	Trooper getSniper() {
		return sniper;
	}
	Trooper sniper() {
		return getSniper();
	}
	Trooper getScout() {
		return scout;
	}
	Trooper scout() {
		return getScout();
	}
	World getWorld() {
		return world;
	}
	World world() {
		return getWorld();
	}
	Game getGame() {
		return game;
	}
	Game game() {
		return getGame();
	}
	Move getMove() {
		return move;
	}
	Move move() {
		return getMove();
	}
	Trooper getSelf() {
		return self;
	}
	Trooper self() {
		return getSelf();
	}
	Trooper mySelf(Trooper self) {
		Trooper t;
		switch (self.getType()) {
			case COMMANDER:
				t = commander();
				break;
			case FIELD_MEDIC:
				t = medic();
				break;
			case SOLDIER:
				t = soldier();
				break;
			case SNIPER:
				t = sniper();
				break;
			case SCOUT:
				t = scout();
				break;
			default:
				t = null;
		}

		return t;
	}
	void setTroopers(List<Trooper> troopers) {
		this.troopers = troopers;
	}
	List<Trooper> getTroopers() {
		return troopers;
	}
	List<Trooper> troopers() {
		return getTroopers();
	}
	List<Trooper> getMyTroopers() {
		List<Trooper> l = new ArrayList(
				Arrays.asList(
					commander(), medic(), soldier(), sniper(), scout()));
		l.removeAll(
				Collections.singleton(null));
		return l;
	}
	void setDestination(Integer destination) {
		this.destination = destination;
	}
	Integer getDestination() {
		return destination;
	}
	int size() {
		return getCellCount();
	}
	int getHeight() {
		return world().getHeight();
	}
	int getWidth() {
		return world().getWidth();
	}
	int getCellCount() {
		return getWidth()*getHeight();
	}
	void initTroopers() {
		setTroopers(
				new ArrayList<Trooper>(
					Arrays.asList(
						world().getTroopers())));
	}
	void initMyTroopers() {
		for (Trooper trooper : getTroopers()) {
			if (trooper.isTeammate()) {
				switch (trooper.getType()) {
					case COMMANDER:
						setCommander(trooper);
						break;
					case FIELD_MEDIC:
						setFieldMedic(trooper);
						break;
					case SOLDIER:
						setSoldier(trooper);
						break;
					case SNIPER:
						setSniper(trooper);
						break;
					case SCOUT:
						setScout(trooper);
						break;
				}
			}
		}
	}
	void initCells() {
		CellType[][] cells = world().getCells();
		List<CellType> cs = new ArrayList<CellType>(
				Collections.nCopies(
					getCellCount(), CellType.FREE));

		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cs.set(idx(x,y), cells[x][y]);
			}
		}

		setCells(cs);
	}
	void initFreeCells() {
		List<Integer> cells = new ArrayList<Integer>();

		for (int idx = 0 ; idx < size(); idx++) {
			if (CellType.FREE == getCell(idx)) {
				cells.add(idx);
			}
		}

		setFreeCells(cells) ;
	}
	void setFreeCells(List<Integer> freeCells) {
		this.freeCells = freeCells;
	}
	List<Integer> getFreeCells() {
		return freeCells;
	}
	void setCells(List<CellType> cells) {
		this.cells = cells;
	}
	List<CellType> getCells() {
		return cells;
	}
	CellType getCell(int x, int y) {
		return getCell(idx(x,y));
	}
	CellType getCell(int idx) {
		return getCells().get(idx);
	}
	void setPlayers(List<Player> players) {
		this.players = players;
	}
	List<Player> getPlayers() {
		return players;
	}
	List<Player> players() {
		return getPlayers();
	}
	void initPlayers() {
		setPlayers(
				new ArrayList<Player>(
					Arrays.asList(
						world().getPlayers())));
	}
	ActionType action() {
		return move().getAction();
	}
	int points() {
		return self().getActionPoints();
	}
	/**
	 * @return Troopers to be promoted to commander.
	 */
	List<Trooper> getCommandingList() {
		List<Trooper> l = new ArrayList(
				Arrays.asList(
					commander(), soldier(), medic(), sniper(), scout()));
		l.removeAll(
				Collections.singleton(null));
		return l;
	}
	Trooper getCommandingUnit() {
		return getCommandingList().get(0);
	}
	List<Trooper> teammates(List<Trooper> troopers) {
		List<Trooper> teammates = 
			new ArrayList<Trooper>();
		for (Trooper t : troopers) {
			if (t.isTeammate()) {
				teammates.add(t);
			}
		}
		return teammates;
	}
	List<Trooper> teammates() {
		return teammates(troopers());
	}
	/** @return foes from list */
	List<Trooper> foes(List<Trooper> troopers) {
		List<Trooper> trc = 
			new ArrayList<Trooper>(
					troopers);
		trc.removeAll(
				teammates(troopers));
		return trc;
	}
	/** @return visible foes */
	List<Trooper> foes() {
		return foes(troopers());
	}
	void makeNewDestination() {
		/** Choose a random free destination */
		int idx;
		do {
			idx = random.nextInt(
					getCellCount());
		} while (!isFree(idx) || occupied(idx));
		setDestination(idx);
	}
	boolean standsAtDestination(Trooper t) {
		return new Integer(
				idx(t)).equals(getDestination());
	}
	boolean standsAt(Trooper t, Integer at) {
		return (new Integer(idx(t))).equals(at);
	}
	boolean hasArrived(List<Integer> path) {
		return path.size() <= 1;
	}
	boolean isNear(Integer cell) {
		return dist(self(), cell) <= 3; 
	}
	boolean isNear(List<Integer> path) {
		return path.size() <= 1;
	}
	/** can reach a cell if it is adjancted to my. */
	boolean canReach(int idx) {
		return self() == at(idx) || adjancted(idx(self())).contains(
				new Integer(idx));
	}
	boolean canReach(Trooper trooper) {
		return null != trooper && canReach(idx(trooper));
	}
	/**
	 * Returns trooper at the cell.
	 */
	Trooper at(int idx) {
		Trooper at = null;

		/** Check troopers */
		for (Trooper trooper : getTroopers()) {
			if (idx(trooper) == idx) {
				at = trooper;
				break;
			}
		}

		return at;
	}
	Trooper at(int x, int y) {
		return at(idx(x,y));
	}
	Trooper foeAt(int idx) {
		Trooper t = at(idx);
		return (null == t || t.isTeammate()) ? null : t;
	}
	Trooper foeAt(int x, int y) {
		return foeAt(idx(x,y));
	}
	Trooper teammateAt(int idx) {
		Trooper t = (Trooper) at(idx);
		return (null == t || !t.isTeammate()) ? null : t;
	}
	Trooper teammateAt(int x, int y) {
		return teammateAt(idx(x,y));
	}
	/**
	 * @return troopers adjancted to cell.
	 */
	List<Trooper> adjanctedTo(int idx) {
		return troopersAt(
				adjancted(idx));
	}
	List<Trooper> adjanctedTo(Trooper t) {
		return adjanctedTo(idx(t));
	}
	/**
	 * @return adjancted cells indexes.
	 */
	List<Integer> adjancted(int idx) {
		List<Integer> adj = new ArrayList<Integer>();

		int x = x(idx);
		int y = y(idx);

		/** Is North available? */
		if (y - 1 >= 0) {
			adj.add(idx(x,y-1));
		}
		/** West? */
		if (x - 1 >= 0) {
			adj.add(idx(x-1, y));
		}
		/** South? */
		if (y + 1 < getHeight()) {
			adj.add(idx(x,y+1));
		}
		/** East? */
		if (x + 1 < getWidth()) {
			adj.add(idx(x+1, y));
		}

		return adj;
	}
	/** @return weakest trooper from list. */
	Trooper weakest(List<Trooper> troopers) {
		Trooper w = null;
		if (!troopers.isEmpty()) {
			List<Trooper> copyT = new ArrayList<Trooper>(
					troopers);
			Collections.sort(copyT,
				new HitpointsComparator());
			w = copyT.get(0);
		}
		return w;
	}
	/** @return weakest adjancted teammate. */
	Trooper getWeakestAdjancted() {
		return weakest(
				teammates(
					adjanctedTo(
						self())));
	}
	/** troopers reached by shot or grenade */
	List<Trooper> attackable(List<Trooper> troopers) {
		List attackable = 
			new ArrayList(
					shootable(troopers));
		attackable.addAll(grenadable(troopers));
		/** remove duplicates */
		Set temp =
			new LinkedHashSet(
				attackable);
		attackable = new ArrayList<Trooper>(temp);
		return attackable;
	}
	/** troopers reached by shot */
	List<Trooper> shootable(List<Trooper> troopers) {
		List<Trooper> shootable = 
			new ArrayList<Trooper>(
					troopers);
		Trooper foe = null;
		for (Iterator<Trooper> it = shootable.iterator(); it.hasNext(); ) {
			foe = it.next();
			if (!isVisible(foe)) {
				it.remove();
			}
		}
		return shootable;
	}
	boolean isVisible(Trooper t) {
		return world().isVisible(
						self().getShootingRange(),
						self().getX(), self().getY(),
						self().getStance(),
						t.getX()      , t.getY()  ,
						t.getStance());
	}
	/** troopers reached by grenade */
	List<Trooper> grenadable(List<Trooper> troopers) {
		List<Trooper> grenadable =
			new ArrayList<Trooper>(
					troopers);
		Trooper foe = null;
		for (Iterator<Trooper> it = grenadable.iterator(); it.hasNext(); ) {
			foe = it.next();
			if (!isGrenadeReachable(foe)) {
				it.remove();
			}
		}
		return grenadable;
	}
	/** @return shortest path to go from s to d. */
	List<Integer> getPath(int sx, int sy, int dx, int dy) {
		int s = idx(sx,sy);
		int d = idx(dx,dy);
		out.println("Source:"+coord(s)+",destination"+coord(d));
		List<Integer> ds = new ArrayList<Integer>();
		BreadthFirstPaths bfs = new BreadthFirstPaths(
				buildWorldGraph(), s);

		if (bfs.hasPathTo(d)) {
			for (int v : bfs.pathTo(d)) {
				/** Make reverse path */
				ds.add(0, v);
			}
		}

		return ds;
	}
	List<Integer> getPath(int s, int d) {
		return getPath(x(s), y(s), x(d), y(d));
	}
	List<Integer> getPath(Trooper t1, Trooper t2) {
		return getPath(x(t1), y(t1), x(t2), y(t2));
	}
	List<Integer> getPath(Trooper t, int cell) {
		return getPath(idx(t), cell);
	}
	/** @return index from coordinates. */
	int idx(int x, int y) {
		return y*getWidth() + x;
	}
	/** @return index from unit coordinates. */
	int idx(Unit u) {
		return idx(
				u.getX(), u.getY());
	}
	/**
	 * @return x from idx.
	 * idx = y*getWidth() + x;
	 */
	int x(int idx) {
		return idx % getWidth();
	}
	int x(Unit u) {
		return u.getX();
	}
	int y(int idx) {
		return (idx - x(idx)) / getWidth();
	}
	int y(Unit u) {
		return u.getY();
	}

	/**
	 * Trooper stance.
	 */
	boolean isStanding() {
		return self().getStance() == TrooperStance.STANDING;
	}
	boolean isKneeling() {
		return self().getStance() == TrooperStance.KNEELING;
	}
	boolean isProne() {
		return self().getStance() == TrooperStance.PRONE;
	}
	boolean canUseMedikit() {
		return game().getMedikitUseCost() <= points();
	}
	boolean canThrowGrenade() {
		return carriesGrenade() 
			&& game().getGrenadeThrowCost() <= points();
	}
	boolean hasMedikit() {
		return self().isHoldingMedikit();
	}
	boolean iAmLastLeft() {
		return 1 == getMyTroopers().size();
	}
	boolean isGrenadeReachable(int idx) {
		return game().getGrenadeThrowRange() >= self().getDistanceTo(x(idx), y(idx));
	}
	boolean isGrenadeReachable(Trooper trooper) {
		return isGrenadeReachable(
				idx(trooper));
	}
	boolean grenadeDamagesTeammates(int idx) {
		return null != teammateAt(idx) 
			|| 0 != teammates(adjanctedTo(idx)).size();
	}
	boolean grenadeDamages(int idx) {
		return grenadeDamage(idx) > 0;
	}
	boolean carriesGrenade() {
		return self().isHoldingGrenade();
	}

	void standUp() {
		if (game().getStanceChangeCost() <= points()){
			move().setAction(ActionType.RAISE_STANCE);
		}
	}
	void prone() {
		if (game().getStanceChangeCost() <= points()) {
			move().setAction(ActionType.LOWER_STANCE);
		}
	}
	void moveTo(int to) {
		out.println(info(self()) + " moving to "+coord(to));
		if (game().getStandingMoveCost() <= points()) {
			move().setAction(
					ActionType.MOVE);
			move().setX(
					x(to));
			move().setY(
					y(to));
		}
	}
	void standUpAndApproach(int to) {
		if (!isStanding()) {
			standUp();
		} else {
			List<Integer> path = getPath(self(), to);
			if (!isNear(path)) {
				moveTo(path.get(1));
			}
		}
	}
	void standUpAndMove(int to) {
		if (!isStanding()) {
			standUp();
		} else {
			List<Integer> path = getPath(self(), to);
			if (!standsAtDestination(self()) && 1 <= path.size()) {
				moveTo(path.get(1));
			}
		}
	}
	void proneAndShoot() {
		if (!isProne()) {
			prone();
		} else {
			shoot();
		}
	}
	void shootAt(Trooper f) {
		if (self().getShootCost() <= points()) {
			out.println(info(self)+" shoots "+coord(f));
			move().setAction(
					ActionType.SHOOT);
			move().setX(
					x(f));
			move().setY(
					y(f));
		}
	}
	/** Shoots at the weakest foe */
	void shoot() {
		shootAt(
				weakest(
					shootable(
						foes())));
	}
	/** If can kill without stance change then shoot. */
	void kill() {
		if (shootsToDeath(
					weakest(
						shootable(
							foes())))) {
			shootAt(
					weakest(
						shootable(
							foes())));
		} else {
			proneAndShoot();
		}
	}
	/** First throw grenade, then shoot. */
	void throwGrenadeAndKill() {
		if (canThrowGrenade()) {
			throwGrenade();
		} else {
			kill();
		}
	}
	void throwGrenade() {
		/** Identify the most damage cell and not affecting teammates. */
		Integer cell;
		List<Integer> cells = 
				notDamagingTeammates(
					grenadeReachableCells());
		Collections.sort(
				cells,
				Collections.reverseOrder(
					new GrenadeDamageComparator()));
		
		if (cells.size() > 0
				&& grenadeDamage(
					cells.get(0)) > 0) {
			cell = cells.get(0);
			out.println(
					info(self())+" throws grenade "+coord(cell));
			move().setAction(
					ActionType.THROW_GRENADE);
			move().setX(
					x(cell));
			move().setY(
					y(cell));
		}
	}

	void useMedikitAt(Integer idx) {
		out.println(info(self) + " using medikit " + coord(idx));
		move().setAction(
				ActionType.USE_MEDIKIT);
		move().setX(
				x(idx));
		move().setY(
				y(idx));
	}
	boolean shouldUseMedikit(Trooper t) {
		return self() == t
			? game().getMedikitHealSelfBonusHitpoints() + health(self()) <= self().getMaximalHitpoints() 
			: game().getMedikitBonusHitpoints() + health(t) <= t.getMaximalHitpoints();
	}
	boolean shouldHeal(Trooper t) {
		return health(t) < t.getMaximalHitpoints();
	}
	/** returns a list of troopers that should get help */
	List<Trooper> shouldUseMedikitOn(List<Trooper> troopers) {
		List<Trooper> weak = new ArrayList<Trooper>(troopers);
		Trooper trooper;
		for (Iterator<Trooper> it = weak.iterator(); it.hasNext(); ) {
			trooper = it.next();
			if (!shouldUseMedikit(trooper)) {
				it.remove();
			}
		}
		return weak;
	}
	/** medic heals */
	void heal(Trooper t) {
		if (shouldUseMedikit(t) 
				&& hasMedikit()
				&& canUseMedikit()) {
			if (!canReach(idx(t))) {
				approach(t);
			} else {
				useMedikitAt(idx(t));
			}
		} else if (canHeal() && shouldHeal(t)) {
			/** without medikit */
			if (!canReach(idx(t))) {
				approach(t);
			} else {
				out.println(info(self()) + " healing " +info(t));
				move().setAction(ActionType.HEAL);
				move().setX(
						x(t));
				move().setY(
						y(t));
			}
		}
	}
	boolean canHeal() {
		return self() == medic() && points() >= game().getFieldMedicHealCost();
	}
	void healSelf() {
		useMedikitAt(idx(self()));
	}
	void approach(Trooper t) {
		out.println(info(self()) + " approaching "+info(t));
		standUpAndApproach(
				idx(t));
	}
	int health(Trooper t) {
		return t.getHitpoints();
	}
	double dist(Unit u, Integer cell) {
		return dist(u.getX(), u.getY(), x(cell), y(cell));
	}
	double dist(Unit u1, Unit u2) {
		return dist(
				u1.getX(), u2.getX(),
				u1.getY(), u2.getY());
	}
	double dist(int s, int d) {
		return dist(
				x(s), y(s), 
				x(d), y(d));
	}
	double dist(int sx, int sy, int dx, int dy) {
		return Math.sqrt(
				(sx - dx)*(sx - dx) 
				+ (sy - dy)*(sy - dy));
	}
	/** @return damage inflicted by shooting at the cell. */
	int shootingDamage(int idx) {
		Trooper foe = foeAt(idx);
		int damage = 0;
		if (null != foe) {
			damage = self().getDamage();
		}
		return damage;
	}
	/**
	 * @return Sum damage of grenade throwed at cell.
	 * Do not throw grenade if damages teammates.
	 */
	int grenadeDamage(int idx) {
		Trooper target;
		int sum = 0;
		/** Direct hit */
		if (null != foeAt(idx)) {
			target = foeAt(idx);
			sum += health(foeAt(idx)) >= game().getGrenadeDirectDamage() 
				? game().getGrenadeDirectDamage()
				: health(foeAt(idx));
		}
		/** Collateral hit */
		for (Trooper t : foes(adjanctedTo(idx))) {
			sum += (health(t) >= game().getGrenadeCollateralDamage() ? 
					game().getGrenadeCollateralDamage() 
					: health(t));
		}
		return sum;
	}
	int grenadeDamage(int x, int y) {
		return grenadeDamage(idx(x,y));
	}
	int grenadeDamage(Unit u) {
		return grenadeDamage(idx(u));
	}
	/**
	 * @return points for throwing grenade.
	 */
	int grenadeDamagePoints(int idx) {
		return 
			/** damage inflicted */
			grenadeDamage(idx) 
			/** dead soldiers */
			+ game().getTrooperEliminationScore()*grenadeDead(idx);
	}
	/**
	 * @return number of dead foes if grenade thrown.
	 */
	int grenadeDead(int idx) {
		int dead = 0;
		/** Direct hit */
		if (null != foeAt(idx) 
				&& dies(foeAt(idx), game().getGrenadeDirectDamage())) {
			dead++;
		}
		/** Collateral hit */
		for (Trooper t : foes(adjanctedTo(idx))) {
			if (dies(t, game().getGrenadeCollateralDamage())) {
				dead++;
			}
		}

		return dead;
	}
	int grenadeDead(Trooper t) {
		return grenadeDead(idx(t));
	}
	
	/** @return if trooper dies on damage inflicted. */
	boolean dies(Trooper trooper, int damage) {
		return health(trooper) <= damage;
	}
	boolean isReachableForShooting(int idx) {
		return self().getShootingRange() >= self().getDistanceTo(x(idx), y(idx));
	}
	boolean shootsToDeath(Integer idx) {
		return isReachableForShooting(idx)
			&& dies(foeAt(idx), self().getDamage());
	}
	boolean shootsToDeath(Trooper trooper) {
		return shootsToDeath(idx(trooper));
	}
	/** @return true If trooper shoots: it will kill a foe. */
	boolean killsWhenShooting() {
		/** check all reachable cells */
		List<Integer> cells = shootingReachableCells();
		Trooper foe ;
		boolean kws = false;
		for (Integer cell : cells) {
			foe  = foeAt(cell);

			if (null != foe && shootsToDeath(cell)) {
				kws = true;
				break;
			}

		}

		return kws;
	}

	boolean isFree(int idx) {
		return CellType.FREE == getCell(idx);
	}
	boolean isFree(int x, int y) {
		return isFree(idx(x,y));
	}
	boolean occupied(int idx) {
		return null != at(idx);
	}
	boolean occupied(int x, int y) {
		return occupied(idx(x,y));
	}
	/**
	 * @return cells to which can throw grenade
	 */
	List<Integer> grenadeReachableCells() {
		List<Integer> cells = new ArrayList<Integer>(); 
		for (Integer cell = 0; cell < size(); cell++) {
			if (isGrenadeReachable(cell)) {
				cells.add(cell);
			}
		}
		return cells;
	}
	/**
	 * @return a list of cells where to throw grenade without damaging teammates
	 */
	List<Integer> notDamagingTeammates(List<Integer> cells) {
		List<Integer> list = new ArrayList(
				cells);
		for (Integer cell : cells) {
			if (!grenadeDamagesTeammates(cell)) {
				list.add(cell);
			}
		}

		return list;
	}
	/**
	 * @return reachable for shooting cells.
	 */
	List<Integer> shootingReachableCells() {
		List<Integer> cells = new ArrayList<Integer>();
		for (Integer cell : getFreeCells()) {
			if (isReachableForShooting(cell)) {
				cells.add(cell);
			}
		}
		return cells;
	}
	/**
	 * @return troopers at cells from list.
	 */
	List<Trooper> troopersAt(List<Integer> cells) {
		List<Trooper> troopers = new ArrayList<Trooper>();

		for (Integer cell : cells) {
			if (null != at(cell)) {
				troopers.add(at(cell));
			}
		}

		return troopers;
	}

	public enum OrderType {
		GOTO   ,  /** reach a destination */
		STOP   ,
		STAY   ,
		FLEE   ,
		RETREAT,
		EXPLORE,
		WANDER ,
		SEARCH ,
		SPY    ,
		PATROL ,
		DEFEND ,
		GUARD  ,
		ATTACK , 
		SURROUND,
		SHUN   ,
		AVOID  ,
		FOLLOW ,
		GROUP  ,  /** form a group */
		WORK   ,
		RAID   ,
		DISTANCE, /** approach cell with distance */
		DETECT  , /** detects foes */
		ELIMINATE /** eliminates detected foes */
	}

	/**
	 * Order can have suborders.
	 */
	public class Order {
		OrderType type   ;
		Integer   cell   ;
		Trooper   trooper;

		/** For example new Order(OrderType.WANDER) */
		public Order(OrderType type) {
			setType(type);
		}
		/** For example new Order(OrderType.FOLLOW, commander()) */
		public Order(OrderType type, Trooper trooper) {
			setType(type)      ;
			setTrooper(trooper);
		}
		/** For example new Order(OrderType.GOTO, cell) */
		public Order(OrderType type, Integer cell) {
			this(type);
			setCell(cell);
		}
		public Order(OrderType type, Trooper trooper, Integer cell) {
			this(type, trooper);
			setCell(cell);
		}
		void setCell(Integer cell) { 
			this.cell = cell;
		}
		Integer getCell() {
			return cell;
		}
		void setType(OrderType type) {
			this.type = type;
		}
		OrderType getType() {
			return type;
		}
		void setTrooper(Trooper trooper) {
			this.trooper = trooper;
		}
		Trooper getTrooper() {
			return trooper;
		}
	}
	/** Detects other player's coordinates */
	void detect() {
		if (self() == commander() 
				&& points() >= game().getCommanderRequestEnemyDispositionCost()
				&& !dispositionsKnown()
				&& !isRequestingEnemyDisposition()) {
			requestEnemyDisposition();
		}
	}
	void requestEnemyDisposition() {
		move().setAction(
				ActionType.REQUEST_ENEMY_DISPOSITION);
	}
	boolean isRequestingEnemyDisposition() {
		return action() == ActionType.REQUEST_ENEMY_DISPOSITION;
	}
	boolean dispositionsKnown() {
		return !disposition().isEmpty();
	}
	List<Integer> getDisposition() {
		List<Integer> dispositions = new ArrayList<Integer>();
		for (Player player : players()) {
			if (-1 != player.getApproximateX() 
					&& -1 != player.getApproximateY()) {
				dispositions.add(
						idx(player.getApproximateX(), 
							player.getApproximateY()));
			}
		}
		return dispositions;
	}
	List<Integer> disposition() {
		return getDisposition();
	}
    /** Stop: Stay in the current location */
	void stop() {
	}
	int moveCostInStance(TrooperStance stance) {
		int cost = 0;
		switch (stance) {
			case PRONE:
				cost = game().getProneMoveCost();
				break;
			case KNEELING:
				cost = game().getKneelingMoveCost();
				break;
			case STANDING:
				cost = game().getStandingMoveCost();
				break;
		}
		return cost;
	}
	/** return range of movement available */
	int range() {
		return points() / moveCostInStance(
				self().getStance());
	}
	/** Flee: Move to a safe area */
	Integer cellToFlee() {
		Integer safeCell = null;
		/** Find a place not visible by foes and take cover */
		/** Move in to the cell not seen by visible foes */
		/** what cells foes see */
		List<Integer> unSeenCells = new ArrayList<Integer>(); 
		for (Trooper foe : foes()) {
			for (Integer cell : getFreeCells()) {
				if (!world().isVisible(
							foe.getShootingRange(),
							foe.getX(), foe.getY(),
							foe.getStance(),
							self().getX(), self.getY(),
							self().getStance())) {
					unSeenCells.add(cell);
				}
			}
		}
		/** go to closest unseen by foes cell */
		Collections.sort(
				unSeenCells,
				new ProximityComparator());

		if (!unSeenCells.isEmpty()) {
			safeCell = unSeenCells.get(0);
		}
		return safeCell;
	}
	int manhattanDist(int sx, int sy, int dx, int dy) {
		return getPath(sx, sy, dx, dy).size();
	}
	int manhattanDist(int s, int d) {
		return manhattanDist(x(s), y(s), x(d), y(d));
	}
	/** Retreat: Move to a safe area, while fighting off enemy units */
	void retreat(int idx) {
	}
	/** Explore: Find and learn about areas for which little information is known */
	void explore() {
	}
	/** Wander: Move around aimlessly */
	void wander() {
	}
	/** Search: Look for a particular object */
	void search() {
	}
	/** Spy: Go near an object or unit to learn more about it, without being seen */
	void spy() {
	}
	/** Patrol: Repeatedly walk through an area to make sure no enemy units go through it */
	void patrol() {
	}
	/** Defend: Stay near some object or unit to keep enemy units away */
	void defend() {
	}
	/** Guard: Stay near the entrance to some area to keep enemy units out */
	void guard() {
	}
	/** Attack: Move to some object or unit to capture or destroy it */
	void attack() {
	}
	/** Raid a base */
	void raid(Integer cell) {
		if (!isRaiding()) {
			setGlobalOrder(
					new Order(
						OrderType.RAID,
						cell));
		}
	}
	/** Surround: With other units, try to surround an enemy unit or object */
	void surround() {
	}
	/** Shun: Move away from some object or unit */
	void shun() {
	}
	/** Avoid: Stay away from any other units */
	void avoid() {
	}
	/** Follow: Stay near some unit as it moves around */
	void follow(Trooper trooper) {
		if (self() != trooper) {
			approach(trooper);
		}
	}
	/** Move together to commanding unit. */
	void group() {
		if (self() != getCommandingUnit()) {
			approach(getCommandingUnit());
		}
	}
	void distance(int cell, int distance) {
		if (distance > dist(self(), cell)) {
			standUpAndApproach(cell);
		} else {
			/** arrived */
		}
	}
	void raidCenter() {
		if (!isNear(center())) { 
			raid(center());
		}
	}
	void raidBases() {
		/** Unraided bases left */
		if (!unraidedBases().isEmpty()) {
			/** raid in a group */
			Integer raidBase = (Integer) unraidedBases().remove(0);
			raid(raidBase);
		}
	}

	/** has raid base orders? */
	boolean isRaiding() {
		return null != globalOrder() 
			&& OrderType.RAID == globalOrder().getType();
	}

    @Override
    public void move(Trooper self, World world, Game game, Move move) {
		out.println("Move: "+world.getMoveIndex());

		Integer tcell = null; //target cell
		List<Integer> path;

		/** On each move must "zero" troopers because world().getTroopers() returns only alive troopers. */
		setCommander(null); setFieldMedic(null); setSoldier(null); setSniper(null); setScout(null);

		setWorld(world);
		setGame(game)  ;
		setMove(move)  ;

		initCells()    ;
		initFreeCells();
		initTroopers() ;
		initMyTroopers() ;
		setSelf(self)  ;
		initHome();
		initBases(); 
		initUnraidedBases();
		initPlayers();

		if(!attackable(foes()).isEmpty()) { 
		out.println("Me:" + info(self())); 
		out.println("troopers:" + list(getTroopers()));
		out.println("Commanding unit:" + info(getCommandingUnit()));
		out.println("My troopers:" + list(getMyTroopers()));
		out.println("Weakest unit:" + info(weakest(getMyTroopers())));
		out.println("Attackable foes:" + list(attackable(foes())));
		}

		/** Personal orders */
		/** 
		 * If there are visible foes: 
		 * attack visible weakest foe ;
		 * else proceed to following global orders.
		 */
		/** attacking */
		if (!attackable(foes()).isEmpty()) {
			if (!grenadable(foes()).isEmpty()) {
				/** foes visible */
				if (canThrowGrenade()) {
					throwGrenade();
				}
			}
			if (!isThrowingGrenade() 
					&& self() != medic() 
					&& !canReach(
						medic())) {
				heal(self());
			}
			if (!isThrowingGrenade()
					&& !shootable(foes()).isEmpty()
					&& !isHealing()) {
				kill();
			}
		} else {
			/** 
			 * Follow global order.
			 * Move only when no foes visible and not healing 
			 */
			/** Build orders stack */
			/** Commanding unit commands troopers */
			/** If I am the commanding unit, then set global orders */
			if (self() == getCommandingUnit() 
					&& null == globalOrder()) { 
					raidBases();
					if (!isRaiding()) {
						raidCenter();
					}
					if (!isRaiding()) {
						/** do nothing */
					}
			} else if (null != globalOrder) {
				switch (globalOrder().getType()) {
					case RAID:
						Integer raidCell = globalOrder().getCell();
						out.println("global order: raid"+raidCell);
						/** soldier goes first in raid */
						/** medic heals other unit before raid */
						if (!isHealing() && !isMoving()) {
							standUpAndApproach(
									raidCell);
							/** target reached */
							if (!isMoving()) {
								/** check all troopers if all in raid base then order completed */
								boolean allArrived = true;
								for (Trooper trooper : teammates() ) {
									if (!isNear(raidCell)) {
										allArrived = false;
										break;
									}
								}
								if (allArrived) {
									unraidedBases().remove(
											globalOrder().getCell());
									globalOrder = null;
								}
							}
						}
						break;
				}
			}
		}

		if (self() == medic() && !isAttacking()) {
			heal(weakest(teammates())); 
		}
		if (isDead(medic()) 
				&& !shouldUseMedikitOn(teammates()).isEmpty() 
				&& !isAttacking()
				&& !isHealing()) {
			heal(weakest(teammates()));
		}
	}
	String info(Trooper t) {
		String o = ""+t;
		if (null != t) {
			o = t.getType()+"@"+coord(t)+lives(t)+stance(t);
		}
		return o;
	}
	void showPath(List<Integer> path) {
		out.print("Path:");
		Integer p;
		for (Iterator<Integer> it = path.iterator(); it.hasNext(); ) {
			p = it.next();
			out.print(coord(p)+",");
		}
		out.println();
	}
	String coord(int idx) {
		return ""+idx + "(" + x(idx) + ";" + y(idx) + ")";
	}
	String coord(Unit u) {
		return coord(idx(u));
	}
	String stance(Trooper t) {
		return "s("+t.getStance()+")";
	}
	String lives(Trooper t) {
		return "h("+t.getHitpoints()+")";
	}
	String list(List<Trooper> troopers) {
		String o = "" ;
		Trooper t;
		for (Iterator<Trooper> it = troopers.iterator(); it.hasNext(); ) {
			t = it.next();
			o += info(t);
		}
		return o;
	}

	public class ProximityComparator
		implements Comparator {

		public int compare(Object o1, Object o2) {
			Integer cell1 = (Integer) o1;
			Integer cell2 = (Integer) o2;
			return (new Double(dist(idx(self()), cell1))).compareTo(new Double(dist(idx(self), cell2)));
		}

	}
	public class DistanceComparator
		implements Comparator {

		Trooper self;

		public DistanceComparator(Trooper self) {
			this.self = self;
		}

		public int compare(Object o1, Object o2) {
			Trooper t1 = (Trooper) o1;
			Trooper t2 = (Trooper) o2;
			Double d = Math.abs(
					new Double(self.getDistanceTo(t1)));
			return d.compareTo(
					Math.abs(
						new Double(
							self.getDistanceTo(t2))));
		}
	}
	public class HitpointsComparator
		implements Comparator {

		public int compare(Object o1, Object o2) {
			Trooper t1 = (Trooper) o1;
			Trooper t2 = (Trooper) o2;
			return new Integer(t1.getHitpoints()).compareTo(
					new Integer(t2.getHitpoints()));
		}
	}
	public class GrenadeDamageComparator
		implements Comparator {
		
		public int compare(Object o1, Object o2) {
			Integer d1 = new Integer(
					grenadeDamagePoints(
						(Integer) o1));
			Integer d2 = new Integer(
					grenadeDamagePoints(
						(Integer) o2));

			return d1.compareTo(d2);
		}
	}

	/**
	 * Breadth first search.
	 */
	class Graph {
		/** vertexes */
		final int v;
		int       e;
		/** adjacency lists */
		List<Integer>[] adj;

		public int v() {
			return v;
		}
		
		public int e() {
			return e;
		}

		public Graph(int v) {
			this.v = v;
			this.e = 0;

			adj = (List<Integer>[]) new List[v]; 

			for (int i = 0 ; i < v ; i++) {
				adj[i] = new ArrayList<Integer>();
			}
		}

		public void addEdge(int v, int w) {
			adj[v].add(w);
			adj[w].add(v);
			e++;
		}

		public Iterable<Integer> adj(int v) {
			return adj[v];
		} 
	}
	class BreadthFirstPaths {
		boolean[] marked;

		/** last vertex on known path to this vertex */
		int[]     edgeTo;

		/** source */
		final int s; 

		public BreadthFirstPaths(Graph g, int s) {
			marked = new boolean[g.v()];
			edgeTo = new int[g.v()];
			this.s = s;
			bfs(g, s);
		}

		private void bfs(Graph g, int s) {
			Queue<Integer> q = new LinkedList<Integer>();
			marked[s] = true;
			q.add(s);
			while (q.size() > 0) {
				int u = q.remove();
				for (int v : g.adj(u)) {
					if (!marked[v]) {
						edgeTo[v] = u;
						marked[v] = true;
						q.add(v);
					}
				}
			}
		}

		public boolean hasPathTo(int v) {
			return marked[v];
		}

		Iterable<Integer> pathTo(int v) {
			Stack<Integer> path = new Stack<Integer>();

			if (hasPathTo(v)) {
				for (int x = v; x != s; x = edgeTo[x]) {
					path.push(x);
				}
				path.push(s);
			}

			return path;
		}

	}
	Graph buildWorldGraph() {
		Graph g = new Graph(size());

		/**
		 * Build a grid.
		 */
		int current;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				current = idx(x,y);
				if (isFree(current) || self() == at(x,y)) {
					for (Integer i : adjancted(current)) {
						if (isFree(i) && !occupied(i)) {
							g.addEdge(current, i);
						}
					}
				}
			}
		}

		return g;
	}
}

/**
 * Strategija.
 * Vadovaujantis eina i prieki.
 * Medikas: eina paskui vadovaujanti, jei pamato serganti tai ji gydo.
 * Kiekvienas: jei turi vaistinele, tai turi gyditis pats.
 * Jei medikas: jei nera arti saviskiu, tai saudyk priesus, o ne gydykis.
 * Medikas: jei atakuoja, tai pradzioje mesti granata, jei savu nesuzeidzia.
 */
