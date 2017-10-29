package be.error.rpi.heating;

public enum EbusDeviceAddress {

	GROUND_FLOOR("30"),
	FIRST_FLOOR("70");

	private final String ebusAddressPrefix;

	EbusDeviceAddress(final String ebusAddressPrefix) {
		this.ebusAddressPrefix = ebusAddressPrefix;
	}

	public String getEbusAddressPrefix() {
		return ebusAddressPrefix;
	}
}
