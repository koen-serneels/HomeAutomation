package be.error.types;

/**
 * @author Koen Serneels
 */
public enum LocationId {

	NACHTHAL("Nachthal"),
	BADKAMER("Badkamer"),
	DRESSING("Dressing"),
	SK1("Slaapkamer1"),
	SK2("Slaapkamer2"),
	SK3("Slaapkamer3"),
	KEUKEN("Keuken"),
	EETHOEK("Eethoek"),
	INKOMHAL("Inkomhal"),
	BERGING("Berging"),
	VOORDEUR("Inkom buiten"),
	GANG("Gang"),
	WC("WC"),
	ZITHOEK("Zithoek"),
	GARAGE("Garage"),
	LZG("LZG"),
	AG("AG"),
	RZG("RZG"),
	VG("VG");

	private String dimmerDescription;

	LocationId(final String dimmerDescription) {
		this.dimmerDescription = dimmerDescription;
	}
}


