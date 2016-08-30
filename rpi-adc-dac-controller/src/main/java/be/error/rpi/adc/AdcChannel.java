package be.error.rpi.adc;

/**
 * @author Koen Serneels
 */
class AdcChannel {

	private int channel;
	private String object;
	private String id;
	private Adc adc;

	public AdcChannel(final int channel, final Adc adc, final String object, final String id) {
		this.channel = channel;
		this.object = object;
		this.adc = adc;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public int getChannel() {
		return channel;
	}

	public String getObject() {
		return object;
	}

	public Adc getAdc() {
		return adc;
	}
}
