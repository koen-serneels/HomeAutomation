package be.error.rpi.adc;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.i2c.I2CBus;

/**
 * @author Koen Serneels
 */
public class AdcControllerConfiguration {

	public List<AdcBoard> getAdcBoards(I2CBus i2CBus) {
		List<AdcBoard> boards = new ArrayList<>();

		/**
		 * NOTE: "Raam badkamer rechts" and "Raam badkamer WC" via Loxone analog in because max. ADCs reached
		 */

		boards.add(new AdcBoard(i2CBus) {
			{
				setLeftAdc(new Adc(new AdcConfiguration(0x6E), this) {
					{
						addChannel("Raam dressing rechts (kip)", "1");
						addChannel("Raam dressing links (kip)", "2");
						addChannel("Raam dressing links (draai)", "3");
						addChannel("Raam dressing rechts (draai)", "4");
					}
				});
				setRightAdc(new Adc(new AdcConfiguration(0x6F), this) {
					{
						addChannel("Raam badkamer rechts (kip)", "5");
						addChannel("Raam links SK2 (draai)", "6");
						addChannel("Raam links SK2 (kip)", "7");
						addChannel("Raam badkamer rechts (draai)", "8");
					}
				});
				initialize();
			}
		});

		boards.add(new AdcBoard(i2CBus) {
			{
				setLeftAdc(new Adc(new AdcConfiguration(0x68), this) {
					{
						addChannel("Raam woonkamer vooraan rechts (draai)", "9");
						addChannel("Raam woonkamer vooraan links (kip)", "10");
						addChannel("Raam woonkamer vooraan links (draai)", "11");
						addChannel("Raam woonkamer vooraan rechts (kip)", "12");
					}
				});
				setRightAdc(new Adc(new AdcConfiguration(0x69), this) {
					{
						addChannel("Raam garage links (kip)", "13");
						addChannel("Schuifraam keuken", "14");
						addChannel("Schuifraam zithoek", "15");
						addChannel("Buitendeur berging", "16");
					}
				});
				initialize();
			}
		});

		boards.add(new AdcBoard(i2CBus) {
			{
				setLeftAdc(new Adc(new AdcConfiguration(0x6A), this) {
					{
						addChannel("Raam rechts SK2 (draai)", "17");
						addChannel("Raam nachthal (draai)", "18");
						addChannel("Raam nachthal (kip)", "19");
						addChannel("Raam rechts SK2 (kip)", "20");
					}
				});
				setRightAdc(new Adc(new AdcConfiguration(0x6B), this) {
					{
						addChannel("Raam rechts SK1 (draai)", "21");
						addChannel("Raam links SK1 (kip)", "22");
						addChannel("Raam links SK1 (draai)", "23");
						addChannel("Raam rechts SK1 (kip)", "24");
					}
				});
				initialize();
			}
		});

		boards.add(new AdcBoard(i2CBus) {
			{
				setLeftAdc(new Adc(new AdcConfiguration(0x6C), this) {
					{
						addChannel("Voordeur", "25");
						addChannel("Raam garage links (draai)", "26");
						addChannel("Raam garage rechts (draai)", "27");
						addChannel("Raam garage rechts (kip)", "28");
					}
				});
				setRightAdc(new Adc(new AdcConfiguration(0x6D), this) {
					{
						addChannel("Raam links SK3 (draai)", "29");
						addChannel("Raam rechts SK3 (draai)", "30");
						addChannel("Raam rechts SK3 (kip)", "31");
						addChannel("Raam links SK3 (kip)", "32");
					}
				});
				initialize();
			}
		});

		/**
		 * NOTE: "Raam badkamer links " and "Raam badkamer WC" via Loxone analog in because max. ADCs reached
		 */

		return boards;
	}
}
