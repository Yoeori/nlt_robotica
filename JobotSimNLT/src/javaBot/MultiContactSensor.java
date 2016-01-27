package javaBot;

import java.awt.Color;

/**
 * Multi Contact Sensor: returns a value based on which sensor is pressed
 * A maximum of five such sensors may be defined and the value returned is
 * an analog value, representing the sensor pressed.
 * 
 * Please note that a contactsensor is actually a modified distance sensor that
 * has a fixed distance of about 1 cm. It is handled as an analog sensor but
 * returns an analog value
 */
public class MultiContactSensor extends DistanceSensor {
	protected double maxDist = 0.20;
	protected static int value;
	public static final int BOUND_ERROR = -1;

	// Default constructor:
	public MultiContactSensor(Location position, double angle, double val) {
		// create sensor with position, angle and default value 0
		super("MultiContactSensor", position, angle, 0);
		value = (int) val;
	}
	
	public MultiContactSensor(Location position, double angle) {
		// create sensor with position, angle and default value 0
		super("MultiContactSensor", position, angle, 0);
	}

	/**
	 * Return the correspondig sensor value for a given sensor
	 */
	protected static int getValue(int distance) {
		// Not used, integrated with convertDistanceToValue
		int i = distance;
		if (i == 90) i = 30;
		if (i == 89) i = 150;
		if (i == 45) i = 360;
		return i;
	}

	/**
	 * Convert the distance reading to a (real) sensor value (the actual
	 * sensors) don't return a distance reading but a value
	 */
	public int convertDistanceToValue(double dist) {
		int i = 0;
		double val = Math.round(100.0 * dist);  // Get the distance
		if (val < 5) {
			val = this.position.getY() * 1000;
			if (val == 0) 
				i = 30;
			else {
				val = this.position.getX() * 1000;
				if (val < 0)
				   i = 150;
				else
					i = 360;
			}
		}
		return i;
	}

	/**
	 * Convert a (real) sensor value to a distance reading. Not applicable for
	 * contactsensor.
	 */
	public double convertValueToDistance(double val) {
		return this.getDistanceInCM((int) val) / 100.0;
	}

	/**
	 * Contactsensor: this method should not be used for ContactSensors
	 */
	public int getDistanceInCM(int value) {
		if (value == 1023)
			return 0;
		return 8;
	}

	/**
	 * Create a line going through the sensor under the correct angle.
	 * 
	 * @return The line of sight of the sensor at the maximum sight length
	 */
	public Line sensorLine(double posX, double posY, double rotationRobot) {
		double diam = Math.sqrt(Math.pow(getPosition().getX(), 2)
				+ Math.pow(getPosition().getY(), 2));
		diam = 0.05;
		Location point = new Location(posX
				+ Math.cos(getAngle() + rotationRobot) * diam, posY
				+ Math.sin(getAngle() + rotationRobot) * diam);
		Line aLine = new Line(point, getAngle() + rotationRobot, maxDist);
		aLine.setLineColor(Color.BLACK);
		return aLine;
	}

	/**
	 * Gets the Max distance of the sensorline.
	 */
	public double getMaxDist() {
		return this.maxDist;
	}

	/**
	 * Sets the Max distance of the sensorline.
	 * 
	 * @param value
	 *            The value of the max distance to set.
	 */
	public void setMaxDist(double value) {
		this.maxDist = value;
	}
}
