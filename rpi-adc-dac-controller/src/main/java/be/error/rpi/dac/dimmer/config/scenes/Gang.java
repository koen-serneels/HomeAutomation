package be.error.rpi.dac.dimmer.config.scenes;

import static be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType.OPEN;
import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.dac.dimmer.builder.SceneContext.inactive;
import static java.math.BigDecimal.ZERO;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static tuwien.auto.calimero.process.ProcessCommunicationBase.SCALING;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.eventbus.Subscribe;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.process.ProcessEvent;

import be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType;
import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerCommand;
import be.error.rpi.knx.ProcessListenerAdapter;

public class Gang implements Runnable {

	private Dimmer dimmerInkomhal;
	private Dimmer dimmerGang;

	private boolean motionVg;
	private Optional<Boolean> armed = empty();

	public Gang(final Dimmer dimmerInkomhal, final Dimmer dimmerGang) {
		this.dimmerInkomhal = dimmerInkomhal;
		this.dimmerGang = dimmerGang;
		getInstance().registerAdcEventListener(this);
	}

	@Override
	public void run() {
		ProcessListenerAdapter processListenerAdapter = new ProcessListenerAdapter() {
			@Override
			public void groupWrite(final ProcessEvent e) {
				try {
					if (e.getDestination().equals(new GroupAddress("1/0/7"))) {
						motionVg = asBool(e);
						if (!motionVg) {
							armed = empty();
						}
					} else if (e.getDestination().equals(new GroupAddress("14/0/7"))) {
						int i = asUnsigned(e, SCALING);
						if (i == 0) {
							dimmerGang.putCommand(new DimmerCommand(new BigDecimal(0), null, inactive(), true));
						} else if (armed.isPresent() && armed.get()) {
							dimmerGang.putCommand(new DimmerCommand(new BigDecimal(i), null, inactive(), true));
						}
					}
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		};
		getInstance().getKnxConnectionFactory().createProcessCommunicator(processListenerAdapter);
	}

	@Subscribe
	public void objectStatusChanged(List<Pair<String, ObjectStatusType>> list) throws Exception {
		ObjectStatusType voordeur = list.stream().filter(p -> p.getLeft().equals("25")).findFirst().get().getRight();

		if (!armed.isPresent()) {
			if (voordeur == OPEN) {
				if (motionVg) {
					armed = of(true);
					if (dimmerInkomhal.getCurVal().compareTo(ZERO) > 0 && dimmerGang.getCurVal().compareTo(ZERO) == 0) {
						dimmerGang.putCommand(new DimmerCommand(dimmerInkomhal.getLastDimCommand().get().getTargetVal(), null));
					}
				} else {
					armed = of(false);
				}
			}
		}
	}
}
