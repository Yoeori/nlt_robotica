package javaBot.Nano.Rescue;

import com.muvium.apt.PeriodicTimer;

/**
 * Opdracht 4A - Vooruit rijden
 * Opdracht 4B - Twee seconden lang rijden
 * Opdracht 4C - Vooruit rijden en stop na 2 sec
 * Opdracht 4D - Een bocht maken
 * Opdracht 4E - maak een subroutine
 * Opdracht 4F - Rijd in een vierkantje
 * Opdracht 5C - Rijd tot aan de zwarte lijn 
 * Opdracht 5E - Doorzoek het moeras
 **/

public class DriveBehavior04 extends Behavior {
	private BaseController joBot;
	private int state = 0;
	private int	count = 0;
	int fl = 0;

	public DriveBehavior04(BaseController initJoBot,
			PeriodicTimer initServiceTick, int servicePeriod) {
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	public void doBehavior() {

		if (state == 0) {
			joBot.drive(50, 50);
			joBot.setStatusLeds(false, false, false); // Turn leds off
		}
	}
}
