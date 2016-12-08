package be.error.rpi.dac.dimmer.config;

/**
 * @author Koen Serneels
 */
public enum DimmerName {

	NACHTHAL("Nachthal"),
	BADKAMER("Badkamer"),
	DRESSING("Dressing"),
	SK1("Slaapkamer1"),
	KEUKEN("Keuken"),
	EETHOEK("Eethoek"),
	INKOMHAL("Inkomhal"),
	BERGING("Berging"),
	VOORDEUR("Inkom buiten"),
	GANG("Gang"),
	WC("WC"),
	ZITHOEK("Zithoek");

	private String dimmerDescription;

	DimmerName(final String dimmerDescription) {
		this.dimmerDescription = dimmerDescription;
	}
}

