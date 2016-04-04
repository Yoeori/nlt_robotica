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

public class CuriousBehavior10b extends Behavior {
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
	int object_distance = 0;
	int previous_object_distance = 0;
	int max_distance = 450;
	
	int object_speed = 1;

	/**
	 * Lijst van mogelijke statussen
	 */
	private static final int stateInit = 10;
	private static final int stateSense = 20;
	private static final int stateWander = 30;
	private static final int StateWait = 21;
	private static final int stateBackoff = 22;
	private static final int stateFollow = 23;
	private static final int stateBackoffForward = 24;
	private static final int stateTurnBack = 25;

	/**
	 * Andere waarden
	 */
	private int threshold = 10; //waarde na waar hij een object zal zien.
	
	/**
	 * counter for various loops
	 */
	private int wait = 0;
	private int after_state = 0; //the state will be set after the wait period
	
	boolean turn_direction;

	/**
	 * Main functie van deze klasse
	 * @param initJoBot
	 * @param initServiceTick
	 * @param servicePeriod
	 */
	public CuriousBehavior10b(BaseController initJoBot,
			PeriodicTimer initServiceTick, int servicePeriod) {
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	/**
	 * Voert het gedrag van de robot uit
	 */
	public void doBehavior() {
		
		//bij de eerste run
		if (state == stateInit) {
			System.out.println("Starting curious behaviour loop");
			joBot.setStatusLeds(false, false, false);
			state = stateSense;
		}

		//state om te bekijken wat er nu moet gebeuren
		if (state == stateSense) {
			
			// Sla de vorige afstand tot object op
			previous_object_distance = object_distance;
			
			// Lees nieuwe waarde voor afstandssensor
			object_distance = joBot.getSensorValue(BaseController.SENSOR_DS);
			object_speed = absDiff(object_distance, previous_object_distance) + 1;
			
			//check of er een object te vinden is of niet
			if (object_distance < threshold) {
				state = stateWander; //ga gewoon willekeurig rijden
			} else if(object_distance >=  max_distance) {
				state = stateBackoff;
			} else {
				state = stateFollow;
			}
				
			
		}

		//Voor state uit
		int next_state = stateSense;
		if(state == stateWander) { //Just a little bit of speed, mostly turn around to look for object
			
			System.out.println("Wandering..");
			joBot.setStatusLeds(false, false, false);
			
			//wait = 3; // Change wait to random value
			boolean des = random(0x7F) < 64;
			int speed = random1(0x3F);
			
			if(des)
				joBot.drive((int)(speed*1.5), (int)(speed*0.3));
			else
				joBot.drive((int)(speed*0.3), (int)(speed*1.5));
			//next_state = StateWait;	
			
		} else if(state == stateFollow) {
			joBot.drive(20, 20);
		} else if(state == stateBackoff) {
			System.out.println("backing off");
			turn_direction =  random(0x7F) < 64;
			if(turn_direction)
				joBot.drive(80, 0);
			else
				joBot.drive(0, 80);
			wait = 6;
			next_state = StateWait;	
			after_state = stateBackoffForward;
		} else if(state == stateBackoffForward) {
			joBot.drive(70, 70);
			wait = 10;
			next_state = StateWait;	
			after_state = stateTurnBack;
		} else if(state == stateTurnBack) {  
			if(turn_direction)
				joBot.drive(0,80);
			else
				joBot.drive(80, 0);
			
			wait = 8;
			next_state = StateWait;	
			
		} else if(state == StateWait) {
			if(wait-- > 0) {
				next_state = StateWait;
			} else if(after_state != 0) {
				next_state = after_state;
				after_state = 0;
			}
			
		}
		
		state = next_state;
	}

	/**
	 * Generate random value
	 * @param mask
	 * @return random value
	 */
	private int random(int mask) {
		return (int) System.currentTimeMillis() & mask;
	}
	
	/**
	 * Generate random value
	 * @param mask
	 * @return random value
	 */
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
}
