package javaBot.Nano.Rescue;

import com.muvium.apt.PeriodicTimer;

/**
 * Opdracht 4A - Vooruit rijden Opdracht 4B - Twee seconden lang rijden Opdracht
 * 4C - Vooruit rijden en stop na 2 sec Opdracht 4D - Een bocht maken Opdracht
 * 4E - maak een subroutine Opdracht 4F - Rijd in een vierkantje Opdracht 5C -
 * Rijd tot aan de zwarte lijn Opdracht 5E - Doorzoek het moeras
 **/

public class DriveBehavior04 extends Behavior {
	private BaseController joBot;
	private int state = 10;
	private int count = 0;
	int fl = 0;

	public DriveBehavior04(BaseController initJoBot,
			PeriodicTimer initServiceTick, int servicePeriod) {
		super(initJoBot, initServiceTick, servicePeriod);
		joBot = initJoBot;
	}

	public void doBehavior() {

		if (state == 0) {
			joBot.drive(100, 100);
			joBot.setStatusLeds(false, false, false); // Turn leds off
			joBot.printLCD("State=0");
			count++;
			if (count >= 25) {
				joBot.setStatusLeds(true, false, false); // Turn yellow on
				state = 1;
				count = 0;
			}
		}

		if (state == 1) {
			joBot.drive(100, 30);
			joBot.setStatusLeds(false, false, false); // Turn leds off
			joBot.printLCD("State=0");
			count++;
			if (count >= 7) {
				joBot.setStatusLeds(true, false, false); // Turn yellow on
				state = 2;
				count = 0;
			}
		}

		if (state == 2) {
			joBot.drive(0, 0);
			joBot.setStatusLeds(false, false, false); // Turn leds off
			joBot.printLCD("State=0");
			count++;
			if (count >= 10) {
				joBot.setStatusLeds(true, false, false); // Turn yellow on
				state = 3;
				count = 0;
			}
		}

		if (state == 3) {
			joBot.drive(0, 0);
			joBot.printLCD("State=3");
		}
		if (state == 10) {
			joBotDrive(10, 10, 50, 50, 20);
		}
	}

	private void joBotDrive(int curState, int newState, int l, int r, int t) {
		if (state == curState) { // cur = Current, ofwel huidige waarde
			if (count < 6) {
				joBot.drive(100, 0);
			} else if (count <= 25) {
				joBot.drive(100, 100);
			}
			if (count++ >= t) {
				state = newState;
				joBot.printLCD("State=" + state);
				count = 0;
			}

		}

	}
}