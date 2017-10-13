package cz.cuni.pogamut.posh.widget;

import java.awt.Color;

/**
 * Enum for types of elements in the POSH tree.
 * 
 * @author Honza
 */
public enum PoshNodeType {
	DOCSTRING(new Color(128, 128, 192), "Documentation"),
	
	COMPETENCE(new Color(255, 172, 122), "Competence"),
	COMPETENCE_ELEMENT(new Color(204, 77, 0), "Competence element"),
	
	DRIVE_COLLECTION_NODE(new Color(255,172,56), "Drive collection"),
	DRIVE_ELEMENT(new Color(255,218,107), "Drive element"),

	ACTION_PATTERN(new Color(153,153,255), "Action pattern"),
	GOAL(new Color(163,74, 221), "Goal"),
	ACT(new Color(153, 204, 255), "Action"),
	SENSE(new Color(89, 89, 230), "Sense"),
	ROOT(new Color(135, 213, 108), "Root"),
	TRIGGER(new Color(109, 217, 79), "Trigger"),
	TRIGGERED_ACTION(new Color(174, 255, 89), "Triggered action");

	private Color color;
	private String description;

	private static Color defaultColor = new Color(153, 204, 255);
	
	private PoshNodeType(Color color, String description) {
		this.color = color;
		this.description = description;
	}

	/**
	 * Get color that is associated with this type of element
	 * @return color that is representing this type of element
	 */
	public Color getColor() {
		return color == null ? defaultColor : color;
	}

	/**
	 * Get description of element
	 * @return description of element
	 */
	@Override
	public String toString() {
		return description;
	}
}
