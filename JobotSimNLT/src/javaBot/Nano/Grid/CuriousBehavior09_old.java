package javaBot.Nano.Grid;

/** 
 * CuriousBeheavior is een aangepaste versie van FleeBehavior.
 * Leerlingen passen het gedrag aan in een aantal stappen:
 * 
 * Opdracht 2C - Bekijk deze code 
 * 
 * Opdracht 2D - Pas de code aan:
 * 	Zorg dat de robot naar je toe komt rijden.
 * 
 * Opdracht 2G - Zorg dat de robot niet te dichtbij komt.
 * 	Blijf op een afstand van 10 cm. Zoek uit welke waarde dat is.
 * 	De robot zelf is ongeveer 18 cm, de sensor lijn is 25 cm.
 * 	Merk op dat de code wat anders is dan in FleeBehavior.
 *  Er zijn z.g. State variabelen toegevoegd.
 *  Dit is gedaan ter voorbereiding van latere lessen.
 */

import com.muvium.apt.PeriodicTimer;

public class CuriousBehavior09_old extends Behavior {
	// Declaraties voor opdracht 2
	private BaseController joBot;
	private int state = 10;
	int ds = 0;

	// Declaraties toegevoegd voor opdracht 8b
	private static final int stateInit = 10;
	private static final int stateSense = 20;
	private static final int stateSenseScared = 22;
	private static final int stateSenseCurious = 23;
	private static final int stateSenseNeutral = 24;
	private static final int stateReason = 30;
	// private static final int stateAct = 40; // Not used as state
	private static final int stateActWander = 41;
	private int threshold = 30;
	private int speedThreshold = 10;
	private int rnd1 = 0;
	private int rnd2 = 0;
	private int wait = 0;
	private int prevDs = 0;
	private int nextState = 0;
	private boolean senseFast = false;
	private boolean senseToMe = false;
	private int diff = 0;
	private int speed = 50;

	public CuriousBehavior09_old(BaseController initJoBot,
			PeriodicTimer initServiceTick, int servicePeriod) {
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	public void doBehavior() {

		if (state == 0) {
			ds = joBot.getSensorValue(BaseController.SENSOR_DS);

			joBot.setStatusLeds(false, false, false); // Turn leds off
			joBot.drive(0, 0);

			// Zorg voor opdracht 2G dat de robot niet te dichtbij komt
			// Dat doe je door gebruik te maken van een extra test die
			// kijkt of de waarde niet hoger wordt dan bij 10cm afstand
			// Om die samen te voegen met de sensor waarde, gebruik je de
			// && (and) operator als volgt:
			// if ((ds > 200) && (ds < ???)) // Vul hier de juiste waarde in

			if (ds > 200) {
				joBot.setLed(BaseController.LED_GREEN, true);
				// Show sensor sees something
				joBot.drive(100, 100);
			}
		}

		// Anders dan in FleeBehavior wordt hier de state getest.
		// Deze dient voor opdracht 2 op nul (0) te staan.
		// In volgende lessen gaan we deze variabele gebruiken.
		// De rest van de code is hetzelfde als in FleeBehavior.

		// ==============================================================
		// Opdracht 8B
		// =============================================================
		// We maken eerst een infrastructuur volgens het state diagram
		// De robot doet nog niets, maar we kunnen dit wel testen
		// De leds geven aan of de robot iets ziet
		if (state == stateInit) {
			System.out.println("Reactive Behavior");
			joBot.setStatusLeds(false, false, false);
			state = stateSense;
		}
		// ---------------------------------------------------------------
		// Sense
		// ---------------------------------------------------------------
		// Hier start de Sense loop
		if (state == stateSense) {
			// Save voorgaande sensor waarde
			prevDs = ds;
			// Lees nieuwe waarde voor afstandssensor
			ds = joBot.getSensorValue(BaseController.SENSOR_DS);
			// Check of er beweging is
			if (ds < threshold)
				state = stateSenseNeutral;
			else {
				state = stateReason;
				// Hier gaan we later de snelheid en richting bepalen
				// Hier gaan we later de snelheid en richting bepalen
				// Opdracht 9a.1 -------------------------------------------------------
				// Bepaal snelheid en richting
				diff = (ds - prevDs);
				if (diff > threshold)
				senseToMe = true;
				else
				senseToMe = false;
				if ((absDiff(prevDs, ds) > speedThreshold))
				senseFast = true;
				else
				senseFast = false;
				System.out.print("Diff=");
				System.out.print(diff);
				System.out.print(" ToMe=");
				System.out.print(senseToMe);
				System.out.print(" Fast=");
				System.out.println(senseFast);
			}
		}
		// -----------------------------------------------------------------
		// Reason
		// -----------------------------------------------------------------
		// Het redeneer gedeelte is beperkt
		// We bepalen wanneer de robot bang
		// of nieuwsgierig wordt
		if (state == stateReason) {
			if (senseFast && senseToMe)
				state = stateSenseScared;
			else if (!senseFast || !senseToMe)
				state = stateSenseCurious;
			else
				state = stateSenseNeutral;
		}
		// ------------------------------------------------------------------
		// Act
		// ------------------------------------------------------------------
		// Als eerste laten we de actie zien
		// Opdracht 8c.2 --------------------------------------------------
		nextState = stateSense;
		if (state == stateSenseScared) {
			joBot.setStatusLeds(false, false, true);
			// show scared = blue
			System.out.println("Scared");
			joBot.drive(-speed, -speed);
			// Drive backward Opdracht 8c.2 ------------------
			wait = 15;
			state = stateActWander; // Start wandering
		}
		if (state == stateSenseCurious) {
			joBot.setStatusLeds(false, true, false);
			// show curious = green
			System.out.println("Curious");
			joBot.drive(speed / 2, speed / 2);
			// Drive forward Opdracht 8c.2 -------------------
		}
		if (state == stateSenseNeutral) {
			joBot.setStatusLeds(false, false, false);
			// show neutral = no lights
			// Random move Opdracht 8c.2 ---------------------
			rnd1 = random(0x3F); // Max 64
			rnd2 = random1(0x3F);
			wait = random(0x1F); // Change wait to random value
			System.out.print("Wander R1=");
			System.out.print(rnd1);
			System.out.print(" R2=");
			System.out.print(rnd2);
			System.out.print(" W=");
			System.out.println(wait);
			joBot.drive(rnd1, rnd2);
			state = stateActWander;
		}
		if (state == stateActWander) {
			joBot.setStatusLeds(true, false, false);
			// Show wandering = yellow
			if (wait-- > 0)
				nextState = stateActWander;
		}
		// And prepare for the next cycle
		state = nextState;
	}
		// -------------------------------------------------------------------
		// Extra functies die in de opdrachten worden gebruikt.
		// -------------------------------------------------------------------

		/**
		 * Take the current time and mask out a part of it This way the sequence
		 * in the timer is somewhat random - random takes a part of the system
		 * timer - random1 shifts the time to the right and then takes another
		 * part of the time. The mask size determines the maximum value
		 */

	

	private int random(int mask) {
		return (int) System.currentTimeMillis() & mask;
	}

	private int random1(int mask) {
		return (int) (System.currentTimeMillis() >> 4) & mask;
	}
	
	private int absDiff(int one, int two) {
		return Math.abs((one - two));
		
		
	}	
	
}
