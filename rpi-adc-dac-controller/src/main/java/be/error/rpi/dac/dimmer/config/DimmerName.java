package be.error.rpi.dac.dimmer.config;

/**
 * @author Koen Serneels
 */
public enum DimmerName {

	NACHTHAL("Nachthal"),
	BADKAMER("Badkamer"),
	DRESSING("Dressing"),
	SK1("Slaapkamer1"),
	ZITHOEK("Zithoek");

	private String dimmerDescription;

	DimmerName(final String dimmerDescription) {
		this.dimmerDescription = dimmerDescription;
	}
}
