package javaBot;

import javaBot.JoBotNanoController;
import org.openVBB.robotKit.interfaces.Units;
import org.openVBB.sim.rti.OpenVBBRTIImpl;

public class JoBotProKeeperRobot extends NanoRobot {
	private static final String IMAGE_FILE = Simulator
			.getRelativePath("./resources/jobot.gif");
	private static final String CONFIG_XML = Simulator
			.getRelativePath("./JobotPro/Keeper/joBotNano.xml");
	private static final int DIAMETER_IN_IMAGE = 100;

	public static final double BASE_RADIUS = 0.070;
	public static final double WHEEL_RADIUS = 0.035;
	public static final int RED_LED = 0;
	public static final int YELLOW_LED = 1;
	public static final int GREEN_LED = 2;
	public static final int BLUE_LED = 3;

	ServoMotor[] servoMotors;

	/**
	 * Creates a new UVMRobot instance
	 */
	public JoBotProKeeperRobot(String name, double positionX, double positionY) {
		/*
		 * friction = 7.0 diameter = 2.0 mass = 3.0
		 */
		super(name, 7.0, positionX, positionY, BASE_RADIUS * 2, 3);

		try {
			// If > 1.8, Jobot will get stuck to the wall with his butt
			// If < 1.8, you will see a space between the Jobot and the walls.
			this.distanceFromWallX = 1.2;
			this.distanceFromWallY = 1.2;
			// Create the sensors
			setSensors(new Sensor[10]);

			// addSensor parameters: sensorClassName, radius, position [,
			// direction]
			getSensors()[0] = addSensor("ContactSensor", BASE_RADIUS, 120, 300); // Left
			getSensors()[1] = addSensor("FieldSensor", BASE_RADIUS, 240);        // Left
			getSensors()[2] = addSensor("IRSensor", WHEEL_RADIUS, 300, 290);     // Left
			getSensors()[3] = addSensor("DistanceSensor", BASE_RADIUS, 0, 30); // Left
			getSensors()[4] = addSensor("DistanceSensor",BASE_RADIUS, 0, 150); // Right
	//		getSensors()[4] = addSensor("IRSensor", WHEEL_RADIUS, 240, 250);     // Right
			getSensors()[5] = addSensor("FieldSensor", BASE_RADIUS, 300);        // Right
			getSensors()[6] = addSensor("ContactSensor", BASE_RADIUS, 60, 240);  // Right
			getSensors()[7] = addSensor("DummySensor", 0, 270);
			getSensors()[8] = addSensor("ContactSensor", 0, 270);                // Ball switch
			getSensors()[8] = addSensor("DistanceSensor", BASE_RADIUS, 0, 270);  // Right
			getSensors()[9] = addSensor("IRSensor", WHEEL_RADIUS, 240, 250);     // Right
		}

		catch (ReflectionException e) {
			e.printStackTrace();
		}
		// Create the leds
		setLeds(new Led[4]);
		getLeds()[GREEN_LED] = new Led(java.awt.Color.GREEN, 0);
		getLeds()[RED_LED] = new Led(java.awt.Color.RED, 0);
		getLeds()[YELLOW_LED] = new Led(java.awt.Color.YELLOW, 0);
		getLeds()[BLUE_LED] = new Led(java.awt.Color.BLUE, 0);

		setLCD(new LCD());

		loadApp(CONFIG_XML);
		vbbRTI.start();

		if (servoMotors != null) {
			setActors(new Actor[3]);
			getActors()[0] = servoMotors[0];
			getActors()[1] = servoMotors[1];
			getActors()[2] = servoMotors[2];
		}
	}

	/**
	 * TODO METHOD: DOCUMENT ME!
	 * 
	 * @return $returnType$ TODO RETURN: return description
	 */
	protected String getConfigXMLDoc() {
		return CONFIG_XML;
	}

	/**
	 * This returns the sensor values
	 */
	public double getADCSample(int channel) {
		if (getSensors() == null) {
			System.out.println("Sample requested by null sensorValues");
			return 0;
		}
        double val = getSensors()[channel].getValue() / 1000;
        if (channel == 4) { // Dummy sensor used to set heading and read VM
			orientation = rotation;
		}
//		System.out.println("ProSoccer Chan=" + channel + ",Val="+ val);
		return val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javaBot.PhysicalObject#createGraphicalRepresentation()
	 */
	public GraphicalRepresentation createGraphicalRepresentation() {
		return new GraphicalRepresentation(this, IMAGE_FILE, DIAMETER_IN_IMAGE,
				true);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vout
	 *            DOCUMENT ME!
	 * @param pinId
	 *            DOCUMENT ME!
	 */
	public void digitalOutputPinChanged(boolean vout, int pinId) {
		if (vout == true) {
			getLeds()[pinId].setValue(1.0);
		} else {
			getLeds()[pinId].setValue(0.0);
		}
	}

	/**
	 * Loads the application
	 * 
	 * @param configXML
	 */
	public void loadApp(String configXML) {
		System.out.println("Loading from config file: " + configXML);

		/**
		 * If there is an existing simulation RTI then we need to rip it up and
		 * release the resources associated with the RTI so we can start over
		 */
		if (vbbRTI != null) {
			vbbRTI.ripUp();
		}

		vbbRTI = new OpenVBBRTIImpl();

		controller = new JoBotNanoController(this, .1, vbbRTI, configXML);

		/**
		 * Register JoBotServoMotors as ServoPulseListeners
		 */
		servoMotors = new ServoMotor[5];

		for (int i = 0; i < servoMotors.length; i++) {
			servoMotors[i] = new ServoMotor(0.0, 156,
					1 * Units.MILLISECONDS, 2 * Units.MILLISECONDS, "TReX");
			controller.registerServoPulseListener(servoMotors[i], i);
		}

		/**
		 * Register ADCSampleListeners to read the sensors
		 */
		controller.registerADCSampleListener(this, 0);
		controller.registerADCSampleListener(this, 1);
		controller.registerADCSampleListener(this, 2);
		controller.registerADCSampleListener(this, 3);
		controller.registerADCSampleListener(this, 4);
		controller.registerADCSampleListener(this, 5);
		controller.registerADCSampleListener(this, 6);
		controller.registerADCSampleListener(this, 7);
		controller.registerADCSampleListener(this, 8);
		controller.registerADCSampleListener(this, 9);

		/**
		 * Register DigitalOutputListeners to read update the LED values
		 */
		controller.registerDigitalOutputListener(this, RED_LED);
		controller.registerDigitalOutputListener(this, YELLOW_LED);
		controller.registerDigitalOutputListener(this, GREEN_LED);
		controller.registerDigitalOutputListener(this, BLUE_LED);
		controller.setPortBDIP(15); // Initialize the DIP settings
		controller.registerLCDListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javaBot.MovableObject#update(double) To make sure the user does not
	 * have to remember the motors are driven as if they are both running
	 * forward. Here the command for the right wheel is reversed to reflect the
	 * fact that both wheels run in opposite directions.
	 */
	public void updatePosition(double elapsed) {
		double w0 = 0 - servoMotors[0].getValue();
		double w1 = 0 - servoMotors[1].getValue();
		double w2 = 0 - servoMotors[2].getValue();

		if (servoMotors != null) {
			/*
			 * rvx and rvy are the x and y components of the driving velocity
			 * relative to the robot's orientation
			 */
			double rvx = (WHEEL_RADIUS / 3.) * ((-2 * w0) + w1 + w2);
			double rvy = (WHEEL_RADIUS / Math.sqrt(3.)) * (-w1 + w2);
			setDrivingVelocityX (0 - ((rvx * Math.cos(rotation)) - (rvy * Math
					.sin(rotation))));
			setDrivingVelocityY (((rvx * Math.sin(rotation)) + (rvy * Math
					.cos(rotation))));
			this.setRotationSpeed (((WHEEL_RADIUS / (3. * BASE_RADIUS)) * (w0
					+ w1 + w2)));
		}
	}

	public boolean getGreenLed() {
		return (getLeds()[GREEN_LED].getValue() != 0);
	}

	public boolean getRedLed() {
		return (getLeds()[RED_LED].getValue() != 0);
	}

	public boolean getYellowLed() {
		return (getLeds()[YELLOW_LED].getValue() != 0);
	}

	public boolean getBlueLed() {
		return (getLeds()[BLUE_LED].getValue() != 0);
	}

}
