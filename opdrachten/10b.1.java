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

public class CuriousBehavior10 extends Behavior {
	/**
	 * De robot
	 */
	private BaseController joBot;

	/**
	 * De status op dit moment
	 */
	private int state = 10;

	/**
	 * De sensorwaarde op dit moment
	 */
	int ds = 0;

	/**
	 * Lijst van mogelijke statussen
	 */
	private static final int stateInit = 10;
	private static final int stateSense = 20;
	private static final int stateSenseScared = 22;
	private static final int stateSenseCurious = 23;
	private static final int stateSenseNeutral = 24;
	private static final int stateReason = 30;
	private static final int stateAct = 40; // wordt niet gebruikt
	private static final int stateActWander = 41;
	private static final int stateScared = 60;

	/**
	 * Andere waarden
	 */
	private int threshold = 50;
	private int speedThreshold = 20;
	private int rnd1 = 0;
	private int rnd2 = 0;
	private int wait = 0;
	private int prevDs = 0;
	private int nextState = 0;
	private boolean senseFast = false;
	private boolean senseToMe = false;
	private int diff = 0;
	private int speed = 50;

	// Substates toegevoegd voor opdracht 10a – stap 1
	private static final int stateBackup = 51;
	private int subState = 0;
	private int subCount = 0;
	private int dl = 0;
	private int dr = 0;

	/**
	 * Main functie van deze klasse
	 * 
	 * @param initJoBot
	 * @param initServiceTick
	 * @param servicePeriod
	 */
	public CuriousBehavior10(BaseController initJoBot,
			PeriodicTimer initServiceTick, int servicePeriod) {
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	/**
	 * Voert het gedrag van de robot uit
	 */
	public void doBehavior() {
		// Anders dan in FleeBehavior wordt hier de state getest.
		// Deze dient voor opdracht 2 op nul (0) te staan.
		// In volgende lessen gaan we deze variabele gebruiken.
		// De rest van de code is hetzelfde als in FleeBehavior.

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
				// Opdracht 9a.1
				// -------------------------------------------------------
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
			nextState = avoidObstacle(ds);
			// Opdracht 10a - Stap 2
		} else if (state == stateSenseCurious) {
			joBot.setStatusLeds(false, true, false);
			// show curious = green
			System.out.println("Curious");
			joBot.drive(speed / 2, speed / 2);
			// Drive forward Opdracht 8c.2 -------------------
		} else if (state == stateSenseNeutral) {
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
		} else if (state == stateActWander) {
			// Show wandering = yellow
			joBot.setStatusLeds(true, false, false);
			if (wait-- > 0)
				nextState = stateActWander;
		}
		// And prepare for the next cycle
		state = nextState;
	}

	// -------------------------------------------------------------------
	// Extra functies die in de opdrachten worden gebruikt.
	// -------------------------------------------------------------------
	private int random(int mask) {
		return (int) System.currentTimeMillis() & mask;
	}

	private int random1(int mask) {
		return (int) (System.currentTimeMillis() >> 4) & mask;
	}

	/**
	 * absDiff determines the difference between the two given numbers. If the
	 * result is negative, it is made into a positive number so the absolute
	 * value of the difference is returned
	 */
	private int absDiff(int a, int b) {
		int diff = a - b;
		if (diff < 0)
			return 0 - diff;
		return diff;
	}

	// Opdracht 10a.3 ------------------------------------------------------
	/**
	 * Avoid Obstacle subroutine AvoidObstacle will make sure the robot gets
	 * away from the object. It needs a number of steps to do so and will stay
	 * in the stateScared state. To execute the steps we introduce a substate.
	 */
	private int avoidObstacle(int dist) {
		// we gebruiken de variabel diff om te bekijken hoe snel een object er op af komt:
		int speedmodifier = diff/30/6;
		if(speedmodifier < 1) speedmodifier = 1;
		
		int next = stateSenseScared; // Make sure we get back here
		if (subState < stateBackup) {
			rnd1 = random(0x7F);
			subState = stateBackup;
			subCount = 0;
			System.out.print("Avoid obstacle: ");
			System.out.print(rnd1);
			rnd2 = rnd1 & 0x3F; // Make less than 64
			
			//In plaats van eerst recht achteruit rijden laten we het robotje meteen weer de bocht maken proporioneel op de afstand van het object
			if (rnd1 > 64) {
				dl = -100*(dist/100);
				dr = -60*(dist/100);
			} else {
				dl = -60*(dist/100);
				dr = -100*(dist/100);
			}
			System.out.print(" L=");
			System.out.print(dl);
			System.out.print(" R=");
			System.out.println(dr);
		}
		if (subState == stateBackup) {
			if (subCount++ < 10) {
				joBot.drive(dl*speedmodifier < -100 ? -100 : dl*speedmodifier , dr*speedmodifier < -100 ? -100 : dr*speedmodifier); //de snelheid wordt aangepast aan de snelheid van het object
			} else {
				subCount = 0;
				subState = 0;
				next = stateSense; // Now we are ready
			}
		}
		return next;
	}
}
