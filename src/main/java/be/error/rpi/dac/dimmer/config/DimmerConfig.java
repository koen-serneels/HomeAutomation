package be.error.rpi.dac.dimmer.config;

import java.util.Map;

import be.error.rpi.dac.dimmer.builder.Dimmer;

/**
 * @author Koen Serneels
 */
public interface DimmerConfig {

	Dimmer start() throws Exception;
}
