/*
 * Created on Jun 8, 2004
 */
package javaBot;

import java.awt.Color;

/**
 * Basic virtual robot class with three wheels and three IR sensors intended for
 * better ball searching. At this point only used for demo purposes with
 * BallLover agent
 */
public class SwarmBot extends Jobot {
	
	private Color color = Color.BLUE;
	
	/**
	 * Constructor, mainly creates a Jobot but overrides the sensor array
	 */

	public SwarmBot() {
		this("SwarmBot");
	}

	public SwarmBot(String name) {
		super();

		this.name = "SwarmBot";
        this.IMAGE_FILE = null;
        
		try {
			// Create the sensors
			setSensors(new Sensor[5]);

			// addSensor parameters: sensorClassName, radius, position [,
			// direction]
			getSensors()[0] = addSensor("IRSensor", 0, 270); // pointing south
			getSensors()[1] = addSensor("IRSensor", 0, 150); // upper left (northeast)
			getSensors()[2] = addSensor("IRSensor", 0, 30);  // upper right (northwest)
			getSensors()[3] = addSensor("ReflectionSensor", BASE_RADIUS, 0, -90); // South
			getSensors()[4] = addSensor("CompassSensor", BASE_RADIUS, 0 ,-90);
																					
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
		
		// ((IRSensor) sensors[0]).fieldOfView = Math.PI;
		// ((IRSensor) sensors[0]).maxDist = 5;
		// ((IRSensor) sensors[1]).fieldOfView = Math.PI;
		// ((IRSensor) sensors[1]).maxDist = 5;
		// ((IRSensor) sensors[2]).fieldOfView = Math.PI;
		// ((IRSensor) sensors[2]).maxDist = 5;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor (Color aColor) {		
		this.graphicalRepresentation.setColor(aColor);
	}
	
	public GraphicalRepresentation createGraphicalRepresentation() {
		int size  = (int) super.diameter;
		return new GraphicalRepresentation(this, null, size, color);
	}

}
