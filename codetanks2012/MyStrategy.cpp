// Marius Zilenas, mzilenas@gmail.com
// Vilnius, Lietuva
// Tel. +370 64643939

#include "MyStrategy.h"

#define _USE_MATH_DEFINES
#include <cmath>
#include <vector>

using namespace model;
using namespace std;

#define MAX_SUVIO_KAMPAS 2.85
#define MATOMUMO_SPINDULYS 18.0
#define MAX_ATSTUMAS 9999

double rad2deg(const double rad) { return rad * 180/M_PI; }

double sign(const double a) {
	if (a > 0) {
		return 1.0;
	} else if (a < 0) {
		return -1.0;
	} else {
		return 0;
	}
}

long long artimiausiasBonusas (const int tipas, const Tank& self, const World& world) {
	vector <Bonus> bonuses = world.bonuses();
	long long bonus_id     = 0;
	double    min_atstumas = MAX_ATSTUMAS;
	for(size_t i = 0; i < bonuses.size(); i++) {		
		Bonus b = bonuses.at(i);
		if (b.type() == tipas) {
			if (self.GetDistanceTo(b) < min_atstumas) {
				min_atstumas = self.GetDistanceTo(b);
				bonus_id     = b.id();
			}
		}
	}
	return bonus_id;
}

long long artimiausiaVaistinele(const Tank& self, const World& world) {
	//rask artimiausius vaistus	
	return artimiausiasBonusas(MEDIKIT, self, world);
}

long long artimiausiasRemontas(const Tank& self, const World& world) {
	//rask artimiausia remonta	
	return artimiausiasBonusas(REPAIR_KIT, self, world);
}

long long artimiausiSoviniai(const Tank& self, const World& world) {
	//rask artimiausius sovinius
	return artimiausiasBonusas(AMMO_CRATE, self, world);
}

Tank tankasPagalId(const long long id, const World& world) {	
	vector<Tank> tanks = world.tanks();
	for(size_t i = 0; i < tanks.size(); i++) {
		if (tanks.at(i).id() == id) {
			return tanks.at(i);
		}		
	}
	return tanks.at(0);
}

Bonus bonusPagalId(const long long id, const World& world) {
	vector<Bonus> bonuses = world.bonuses();
	for (size_t i = 0; i < bonuses.size(); i++) {
		if (bonuses.at(i).id() == id) {
			return bonuses.at(i);
		}
	}
	return bonuses.at(0);
}

long long artimiausiasGyvasTankas(const Tank& self, const World& world) {
	vector <Tank> tanks = world.tanks();	
	double min_atstumas = MAX_ATSTUMAS; 
	long long tank_id = 0;
	for(size_t i = 0; i < tanks.size(); i++) {		
		Tank tank = tanks.at(i);
		if (!tank.teammate() && tank.crew_health() > 0 && tank.hull_durability() > 0 && tank.GetDistanceTo(self) < min_atstumas) {
			min_atstumas = tank.GetDistanceTo(self);
			tank_id      = tank.id();
		}
	}
	return tank_id;
}

double vaistine(const Tank& self) {
	return (double)self.crew_health() / self.crew_max_health();
}

double remontas(const Tank& self) {
	return (double)self.hull_durability() / self.hull_max_durability();
}

int soviniai(const Tank& self) {
	return self.premium_shell_count();
}

bool vaziuotiSoviniu(const Tank& self) {
	return soviniai(self) == 0;
}

bool isBetween(const double a, const double b, const double x) {
	return x > a && x < b;
}

bool vaziuotiAtbulam(const double posukio_kampas, const Tank& self, const long long vaziuotiLink) {
	//atbulas gali vaziuoti tik link bonuso
	return vaziuotiLink != 0 && fabs(posukio_kampas) > 120;
}

long long bonusasTaikinys(const Tank& self, const World& world) {
	long long vaziuotiLink = 0;

	if (vaistine(self) < 0.80) {
		vaziuotiLink = artimiausiaVaistinele(self, world);		
	}

	if (vaziuotiLink == 0 && remontas(self) < 0.30) {
		vaziuotiLink = artimiausiasRemontas(self, world);
	}

    if (vaziuotiLink == 0 && vaziuotiSoviniu(self)) {		
		vaziuotiLink = artimiausiSoviniai(self, world);		
	}

	return vaziuotiLink;
}

void MyStrategy::Move(Tank self, World world, model::Move& move) {	
	vector <Tank> tanks    = world.tanks();

	Tank aGTankas          = tankasPagalId(artimiausiasGyvasTankas(self, world), world);
	long long vaziuotiLink = bonusasTaikinys(self, world); //bonusas, kurio link vaziuoti	

	// judejimas
	double posukio_kampas = 0;	
	posukio_kampas = rad2deg( 
		( vaziuotiLink != 0 ) ? self.GetAngleTo(bonusPagalId(vaziuotiLink, world)) : self.GetAngleTo(aGTankas));
	
	double left    = 1.0   ,
		   right   = 1.0   ;
	bool   iKaire  = false , 
		   iDesine = false ;

	const bool atbulas = vaziuotiAtbulam(posukio_kampas, self, vaziuotiLink);

	if (atbulas) { // kai vaziuoja atbulas, tai turi absolucia posukio_kampo reiksme artinti prie 180		
		if (fabs(posukio_kampas) > 180-MATOMUMO_SPINDULYS ) {//jei pasisuko i objekta, tai visu greiciu i ji
			left  = -1.0;
			right = -1.0;
		} else { 
			(posukio_kampas < 0 ? iDesine : iKaire) = true; // didinti fabs(kampas)
		}
	} else { // link taikinio tanko vaziuoti priekiu
		if (fabs(posukio_kampas) < MATOMUMO_SPINDULYS) { //jei pasisuko i objekta, tai visu greiciu i ji
			left  = 1.0 ;
			right = 1.0 ;
		} else {
			(posukio_kampas > 0 ? iDesine : iKaire) = true; // mazinti fabs(kampas)
		}
	}

	if (iDesine) {
		left  =  1.0;
		right = -1.0; //0.5
	} else if (iKaire) {
		right =  1.0;
		left  = -1.0; //-0.5
	}
	
	move.set_left_track_power(left);
	move.set_right_track_power(right);

	// saudymas
	const double boksto_kampas = rad2deg(self.GetTurretAngleTo(aGTankas));
	double turn                = 0;
	
	if (fabs(boksto_kampas) > MAX_SUVIO_KAMPAS) {
		turn = 1.0 * sign(boksto_kampas) ;
	}

	FireType fire_type;
	fire_type = ( fabs(boksto_kampas) < MAX_SUVIO_KAMPAS ) ? PREMIUM_PREFERRED : NONE;

	move.set_turret_turn(turn);
	move.set_fire_type(fire_type);
}

TankType MyStrategy::SelectTank(int tank_index, int team_size) {
	return MEDIUM;
}
