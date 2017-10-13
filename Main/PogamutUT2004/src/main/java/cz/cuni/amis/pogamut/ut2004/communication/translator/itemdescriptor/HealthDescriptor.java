package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

/**
 * Health descriptor provides an additional information about health items like
 * vials, medkits etc.
 * 
 * @author Ondrej
 */
public class HealthDescriptor extends ItemDescriptor {

	private boolean superHealth;

	@Override
	public String toString() {
		return "HealthDescriptor[pickupType=" + getPickupType() + ", inventoryType = " + getInventoryType() + ", amount=" + getAmount() + "]";
	}

	/**
	 * If this health can heal above maximum health (usually above 100).
	 * 
	 * @return superHealth
	 */
	public boolean isSuperHealth() {
		return superHealth;
	}

}
