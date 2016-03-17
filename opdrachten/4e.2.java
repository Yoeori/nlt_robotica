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
		System.out.println(count);
		joBot.printLCD("State=" + state);
		
		if (state == 10) {
			jobotDrive(10, 11, 100, 65, 14);
		} else if (state == 11) {
			jobotDrive(11, 12, 50, 50, 15);
		} else if (state == 12) {
			jobotDrive(12, 13, 40, 50, 7);
		} else if (state == 13) {
			jobotDrive(13, 14, 40, 40, 5);
		} else if (state == 14) {
			jobotDrive(14, 15, 80, 60, 10);
		} else if (state == 15) {
			jobotDrive(15, 16, 80, 50, 10);
		} else {
			joBot.drive(0, 0);
			joBot.printLCD("State=done");
		}
	}

	private void jobotDrive(int curState, int newState, int l, int r, int t) {
		if (state == curState) { // cur = Current, ofwel huidige waarde
			joBot.drive(l, r);
			if (count++ >= t) {
				state = newState;
				joBot.printLCD("State=" + state);
				count = 0;
			}
		}
	}
}