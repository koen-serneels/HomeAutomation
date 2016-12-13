package be.error.rpi.dac.dimmer.config.scenes;

import static be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType.CLOSED;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.process.ProcessEvent;

import be.error.rpi.adc.ObjectStatusTypeMapper.ObjectStatusType;
import be.error.rpi.dac.dimmer.builder.Dimmer;
import be.error.rpi.dac.dimmer.builder.DimmerCommand;
import be.error.rpi.knx.ProcessListenerAdapter;

public class Gang implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger("gang");

	private Dimmer dimmerInkomhal;
	private Dimmer dimmerGang;

	private boolean motionVg;
	private Optional<Boolean> armed = empty();

	private ObjectStatusType voordeur;

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
						logger.debug("Light channel VG:" + motionVg + "" + armed());
						if (!motionVg && (voordeur == null || voordeur == CLOSED)) {
							armed = empty();
							logger.debug("Light channel VG was false and voordeur closed, disarming" + armed());
						}
					} else if (e.getDestination().equals(new GroupAddress("2/0/0")) || e.getDestination().equals(new GroupAddress("2/1/0"))) {
						boolean motionLock = asBool(e);
						logger.debug("Lock channel VG:" + motionLock + "" + armed());
						if (motionLock && (voordeur == null || voordeur == CLOSED)) {
							motionVg = false;
							armed = empty();
							logger.debug("Lock channel VG was true and voordeur closed, disarming" + armed());
						}
					} else if (e.getDestination().equals(new GroupAddress("14/0/7"))) {
						int i = asUnsigned(e, SCALING);
						logger.debug("Inkomhal dimmer:" + i + "" + armed());
						if (i == 0) {
							logger.debug("Inkomhal dimmer was 0, turning of gang" + armed());
							dimmerGang.putCommand(new DimmerCommand(new BigDecimal(0), null, inactive(), true));
						} else if (armed.isPresent() && armed.get()) {
							logger.debug("Inkomhal dimmer was <> 0 and gang is armed, setting gang to:" + i + "" + armed());
							dimmerGang.putCommand(new DimmerCommand(new BigDecimal(i), null, inactive(), true));
						} else {
							logger.debug("Inkomhal dimmer was <> 0 but gang is not armed" + armed());
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
		voordeur = list.stream().filter(p -> p.getLeft().equals("25")).findFirst().get().getRight();

		if (!armed.isPresent() && voordeur == OPEN) {
			logger.debug("Voordeur open" + armed());
			if (motionVg) {
				logger.debug("Voordeur open, Light channel VG active" + armed());
				armed = of(true);
				if (dimmerInkomhal.getCurVal().compareTo(ZERO) > 0 && dimmerGang.getCurVal().compareTo(ZERO) == 0) {
					logger.debug("Setting gang dimmer to:" + dimmerInkomhal.getLastDimCommand().get().getTargetVal() + "" + armed());
					dimmerGang.putCommand(new DimmerCommand(dimmerInkomhal.getLastDimCommand().get().getTargetVal(), null));
				}
			} else {
				logger.debug("Voordeur open, Light channel VG not active, disarming" + armed());
				armed = of(false);
			}
		}

		if (armed.isPresent() && motionVg == false && voordeur == CLOSED) {
			armed = empty();
			logger.debug("Armed was present, motion not active and voordeur closed, disarming" + armed());
		}
	}

	public String armed() {
		return " [armed:" + (armed.isPresent() ? armed.get() : "N/A") + "]";
	}
}
