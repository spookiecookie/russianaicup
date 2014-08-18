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
#define MAX_SUVIO_KAMPAS_DIDELIU_ATSTUMU MAX_SUVIO_KAMPAS/2
#define MATOMUMO_SPINDULYS 18.0
#define MAX_ATSTUMAS 9999
#define MIN_VAISTAI 0.80
#define MIN_REMONTAS 0.50

int teamSize = 1;

double rad2deg(const double rad);

bool gyvasTankas(const Tank& tank); //pasako ar tankas gyvas ir ne mano
bool kertaTankas(const Tank& self, const Tank& tankas, const Tank& tikrinamas);
bool sautiGalima(const World& world, const Tank& self, const Tank& taikinys);
bool isBetween(const double a, const double b, const double x);
pair<double, double> kampas(size_t i, const World& world, const Tank& self);
bool kampasUzimtas(const pair<double, double> xkyk, const World& world);

std::pair<double, double> artimiausiasLaisvasKampas(const Tank& self, const World& world);

bool gyvasTankas(const Tank& tank) {//pasako ar tankas gyvas ir ne mano
	return !tank.teammate() && tank.crew_health() > 0 && tank.hull_durability() > 0 ;
}

double tankoR(const Tank& tankas) {
	return std::sqrt(tankas.width()*tankas.width() + tankas.height()*tankas.height())/2;
}

//x0, y0, r      - objekto, su kuriuo susikerta tiese, centro koordinates ir dydis
//x1, y1, x2, y2 - tieses taskai
bool kerta(const double x0, const double y0, const double r, const double x1, const double y1, const double x2, const double y2 ) {
	const double k = -(y1 - y2)/(x2-x1), b = -(x1*y2-x2*y1)/(x2-x1);
	const double d = std::pow(-2*x0 + 2*k*(b-y0), 2) - 4*(1+k*k)*(x0*x0 + pow(b-y0, 2) - r*r);
	return d>0;
}

bool kertaTankas(const Tank& self, const Tank& tankas, const Tank& tikrinamas) {
	return kerta(tikrinamas.x(), tikrinamas.y(),
			    tankoR(tikrinamas),
				self.x()  , self.y(),
				tankas.x(), tankas.y());	
}

bool kertaObjektas(const Tank& self, const Tank& tankas, const World& world) {
	vector<Obstacle> obstacles = world.obstacles();	
	if (obstacles.size() == 0) {
		return false;
	} else { 
		const Obstacle kvadratas = obstacles.at(0);
		return kerta(
				kvadratas.x(), kvadratas.y(),
				kvadratas.height()/2,
				self.x(), self.y(),
				tankas.x(), tankas.y());
	}
}

bool kertaSovinys(const Tank& self, const Shell& shell) {
	const double x2 = shell.x() + shell.speed_x()*10; //rasime koordinates po 10 tiku
	const double y2 = shell.y() + shell.speed_y()*10;

	return kerta(self.x(), self.y(),
				 tankoR(self),
				 shell.x(), shell.y(),
			     x2, y2);
}

long long artimiausiasSovinysPataikantis(const Tank& self, const World& world) {
	vector<Shell> shells = world.shells();
	double max_atstumas  = MAX_ATSTUMAS;
	long long shell_id   = 0;
	for(size_t i=0; i < shells.size() ; i++) {
		Shell shell = shells.at(i);
		double atstumas = self.GetDistanceTo(shell);		
		if(atstumas < max_atstumas && kertaSovinys(self, shell)) {
			max_atstumas = self.GetDistanceTo(shell);
			shell_id = shell.id();
		}
	}
	return shell_id;
}

Shell shellPagalId(const long long shell_id, const World& world) {
	vector<Shell> shells = world.shells();
	for(size_t i=0; i < shells.size() ; i++) {
		if (shells.at(i).id() == shell_id) {
			return shells.at(i);
		}
	}
	return shells.at(0);
}

bool kampasUzimtas(const pair<double, double> xkyk, const World& world) {
	//patikrinti ar yra nors vienas tankas per tanko r nuo kampo
	vector<Tank> tanks = world.tanks();
	for(size_t i=0; i < tanks.size(); i++) {
		const Tank tank = tanks.at(i);
		if(tank.GetDistanceTo(xkyk.first, xkyk.second) < tankoR(tank)) {
			return true;
		}
	}
	return false;
}

vector< pair<double, double> > getKampai(const World& world, const Tank& self) {
	vector< std::pair<double, double> > kampai;
	const double r = tankoR(self); const double atstumas = 2*r;
	const double nuoTS = 4*r; const double nuoAS = 1.5*r;//nuo TolimosSienos, ArtimosSienos

	const double xne = world.width() - atstumas; const double yne = 0 + atstumas;
	kampai.push_back(make_pair(xne, yne));

	//du papildomi kampai
	kampai.push_back(make_pair(world.width() - nuoTS, 0 + nuoAS));
	kampai.push_back(make_pair(world.width() - nuoAS, 0 + nuoTS));

	const double xse = world.width() - atstumas; const double yse = world.height() - atstumas; 
	kampai.push_back(make_pair(xse, yse));

	kampai.push_back(make_pair(world.width() - nuoAS, world.height() - nuoTS));
	kampai.push_back(make_pair(world.width() - nuoTS, world.height() - nuoAS));

	const double xsw = 0 + atstumas; const double ysw = world.height() - atstumas; 
	kampai.push_back(make_pair(xsw, ysw));
	kampai.push_back(make_pair(0 + nuoTS, world.height() - nuoAS));
	kampai.push_back(make_pair(0 + nuoAS, world.height() - nuoTS));

	const double xnw = 0 + atstumas; const double ynw = 0 + atstumas; 
	kampai.push_back(make_pair(xnw, ynw));
	kampai.push_back(make_pair(0 + nuoAS, 0 + nuoTS));
	kampai.push_back(make_pair(0 + nuoTS, 0 + nuoAS));

	return kampai;
}

std::pair<double, double> kampas(size_t i, const World& world, const Tank& self) {
	return getKampai(world, self).at(i);
}

double atstumasIkiArtimiausioKampo(const Tank& self, const World& world) {
	const pair<double, double> xkyk = artimiausiasLaisvasKampas(self, world);
	return self.GetDistanceTo(xkyk.first, xkyk.second);
}

std::pair<double, double> artimiausiasLaisvasKampas(const Tank& self, const World& world) {
	double min_atstumas = MAX_ATSTUMAS;	
	vector< pair<double, double> > kampai = getKampai(world, self);
	size_t kidx = 0;

	for(size_t i = 0; i < kampai.size(); i++) {
		pair<double, double> xkyk = kampai.at(i);
		double atstumas = self.GetDistanceTo(xkyk.first, xkyk.second);
		if (atstumas < min_atstumas && !kampasUzimtas(xkyk, world)) {
			min_atstumas = atstumas;
			kidx = i;
		}
	}

	return kampas(kidx, world, self);
}

void atsitrauktiIsTrajektorijos(const Tank& self, model::Move& move, const long long shell_id, const World& world) {
	const Shell& shell = shellPagalId(shell_id, world);

	//i kuria puse trauktis,
	// jei posukis i shell yra tarp 30 ir 150 laipsniu, tai pavaziuojam i prieki arba atgal
	// jei posukis i shell yra tarp 90 ir 150 tai i prieki
	// jei posukis i shell yra tarp 30 ir 90 tai atgal

	const double kampas = rad2deg(self.GetAngleTo(shell));
	double left = 0, right = 0;
	if (isBetween(90, 150, fabs(kampas))) {		
		left = 1.0; right = 1.0;		
	} else if (isBetween(30, 90, fabs(kampas))) {
		left = -1.0; right = -1.0;		
	}
	move.set_left_track_power(left);
	move.set_right_track_power(right);
}

double atstumasIkiObstacle(const Tank& tank, const World& world) { 
	vector<Obstacle> obstacles = world.obstacles();
	return (obstacles.size() > 0) ? tank.GetDistanceTo(obstacles.at(0)) : 0.0;
}

bool sautiGalima(const World& world, const Tank& self, const Tank& taikinys) {
	//rasti visus negyvus arba mano tankus
	vector<Tank> tanks = world.tanks();
	const double atstumas = self.GetDistanceTo(taikinys);	
	for(size_t i = 0; i < tanks.size(); i++) {
		Tank tank = tanks.at(i);
		if (				
				(tank.id() != self.id() && tank.id() != taikinys.id() &&
			self.GetDistanceTo(tank) < atstumas && !gyvasTankas(tank) &&
			kertaTankas(self, taikinys, tank)) ||
				(atstumas > atstumasIkiObstacle(self, world) && kertaObjektas(self, tank, world) )
				) {
				return false;
    	}
	}
	return true;
}

double rad2deg(const double rad) { return rad * 180/M_PI; }
double sign(const double a) { return (a >= 0) ? 1.0 : -1.0; }

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
		if (gyvasTankas(tank) && tank.GetDistanceTo(self) < min_atstumas) {
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

	if (vaistine(self) < MIN_VAISTAI) {
		vaziuotiLink = artimiausiaVaistinele(self, world);		
	}

	if (vaziuotiLink == 0 && remontas(self) < MIN_REMONTAS) {
		vaziuotiLink = artimiausiasRemontas(self, world);
	}

    if (vaziuotiLink == 0 && vaziuotiSoviniu(self)) {		
		vaziuotiLink = artimiausiSoviniai(self, world);		
	}

	return vaziuotiLink;
}

size_t priesu(const World& world) {
	size_t pi = 0;
	vector<Tank> tanks = world.tanks();
	for(size_t i = 0; i < tanks.size(); i++) {
		Tank tank = tanks.at(i);
		if (gyvasTankas(tank)) {pi++;}
	}
	return pi; 
}

size_t musu(const World& world) {
	size_t mi = 0;
	vector<Tank> tanks = world.tanks();
	for(size_t i = 0; i < tanks.size(); i++) {
		Tank tank = tanks.at(i);
		if (tank.teammate() && tank.crew_health() > 0 && tank.hull_durability() > 0) {mi++;}

	}
	return mi; 
}

bool arVaziuotiLinkKampo(const Tank& tank, const World& world) {
	//vaziuoti link kampo tada kai priesu daugiau nei musu 
	return (teamSize == 1 && priesu(world) > 2) || (teamSize == 2 && priesu(world) > 3) || (teamSize == 3 && priesu(world) - musu(world) > 0 ) ;
}

//stovi kampe kai atstumas maziau uz 10
bool stoviKampe(const Tank& self, const World& world) {	
	return atstumasIkiArtimiausioKampo(self, world) < tankoR(self);
}

double maxSuvioKampas(const Tank& self, const Tank& taikinys, const World& world) {
	return self.GetDistanceTo(taikinys) < std::sqrt(world.width()*world.width() + world.height()*world.height()) ? MAX_SUVIO_KAMPAS : MAX_SUVIO_KAMPAS_DIDELIU_ATSTUMU;
}

void MyStrategy::Move(Tank self, World world, model::Move& move) {	
	vector <Tank> tanks    = world.tanks();

	Tank aGTankas          = tankasPagalId(artimiausiasGyvasTankas(self, world), world);
	long long vaziuotiLink = bonusasTaikinys(self, world); //bonusas, kurio link vaziuoti	

	// judejimas

	// link ko vaziuoti
	// link bonuso
	// link kampo
	// link tankoTaikinio	
	// link koordinates
	double posukio_kampas = 0;
	bool iKampa   = false;
	bool iBonusa  = false;
	bool iTaikini = false;
	pair<double, double> xkyk;
	if (vaziuotiLink != 0) { //link bonuso
		iBonusa = true;
		posukio_kampas = self.GetAngleTo(bonusPagalId(vaziuotiLink, world));
	} else if(!stoviKampe(self, world) && arVaziuotiLinkKampo(self, world) ) {//vaziuoti link kampo
		iKampa         = true;
		xkyk           = artimiausiasLaisvasKampas(self, world); 
		posukio_kampas = self.GetAngleTo(xkyk.first, xkyk.second);
	} else {//link tankoTaikinio
		posukio_kampas = self.GetAngleTo(aGTankas);
		iTaikini = true;
	}
	posukio_kampas = rad2deg(posukio_kampas);

	double left    = 1.0   ,
		   right   = 1.0   ;
	bool   iKaire  = false , 
		   iDesine = false ;

	const bool atbulas = vaziuotiAtbulam(posukio_kampas, self, vaziuotiLink) || (iKampa && fabs(posukio_kampas) > 120 );

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
		right = -1.0;
	} else if (iKaire) {
		right =  1.0;
		left  = -1.0;
	}

	//atsitraukti jei yra pataikantis sovinys
	long long shell_id = artimiausiasSovinysPataikantis(self, world);
	if (shell_id != 0) {
		atsitrauktiIsTrajektorijos(self, move, shell_id, world);
	} else {

		//kitu atveju judeti normaliai
		move.set_left_track_power(left);
		move.set_right_track_power(right);

	}

	// saudymas
	const double boksto_kampas = rad2deg(self.GetTurretAngleTo(aGTankas));
	double turn                = 0;
	const double max_kampas    = maxSuvioKampas(self, aGTankas, world);
	
	if (fabs(boksto_kampas) > max_kampas) {
		turn = 1.0 * sign(boksto_kampas) ;
	}

	FireType fire_type;
	fire_type = ( fabs(boksto_kampas) < max_kampas && sautiGalima(world, self, aGTankas) ) ? PREMIUM_PREFERRED : NONE;

	move.set_turret_turn(turn);
	move.set_fire_type(fire_type);
}

TankType MyStrategy::SelectTank(int tank_index, int team_size) {
	teamSize = team_size;
	return MEDIUM;
}
