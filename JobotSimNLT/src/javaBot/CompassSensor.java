package javaBot;

import java.awt.Color;

/**
 * Compass Sensor
 */
public class CompassSensor extends DistanceSensor {
	protected double maxDist = 0.30;
	public static final int BOUND_ERROR = -1;

	// Default constructor:
	public CompassSensor(Location position, double angle, double direction) {
		// create sensor with position, angle and default value 0
		super("CompassSensor", position, angle, direction);
		maxSensorValue = 360;
	}

	public CompassSensor(Location position, double angle) {
		// create sensor with position, angle and default value 0
		super("CompassSensor", position, angle, 0);
		maxSensorValue = 360;
	}
	
	// We get here when channel 4 is being read (also VM)
	// This is a dummy routine to make the simulator return the current rotation of the robot
	// This is only called from JoBotPro and SIM robots at the moment
	public int getValue (double value) {
		double val = this.getRobot().rotation;
//		System.out.println("Degrees :" + val);
		return getPositionInDegrees(val);
	}
	
	public int getPositionInDegrees (double value) {
//		System.out.println("Position in radians: " + value);	
		int val = (int) Math.round(value / Math.PI * 180);
		if (val < 0) val = val + 360;
//      If higher than 360, use mod(360)
//		System.out.println("Position in degrees: " + val);	
		return val;
	}

	/**
	 * Create a line going through the sensor under the correct angle.
	 */
	public Line sensorLine(double posX, double posY, double orientationRobot) {
		double diam = Math.sqrt(Math.pow(getPosition().getX(), 2)
				+ Math.pow(getPosition().getY(), 2));
		diam = 0.05;
		Location point = new Location(posX
				+ Math.cos(getAngle() + orientationRobot) * diam, posY
				+ Math.sin(getAngle() + orientationRobot) * diam);
		Line aLine = new Line(point, getAngle() + orientationRobot, maxDist);
		aLine.setLineColor(Color.YELLOW);
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
