package be.error.rpi.dac.dimmer.builder;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned.DPT_PERCENT_U8;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.process.ProcessCommunicator;

public class DimmerStatusFeedbackProcessor {

	private Collection<DimmerStatusFeedback> dimmerStatusFeedback = new ArrayList<>();

	private DimmerStatusFeedbackProcessor() {

	}

	/**
	 * From the moment the target dim value is reached, sends that dim value as status feedback to the configured {@link DimmerStatusFeedback#groupAddress}. If a
	 * {@link DimmerStatusFeedback#sourceIgnore} is configured, and the {@link DimmerCommand#getOrigin()} originates from that configured {@link IndividualAddress},
	 * the status is not sent. <p>
	 * Note: continious sending of the status would be possible, but not advisable as it would generated high bus load
	 */
	public void processFeedback(DimmerCommand dimmerCommand, BigDecimal curVal, ProcessCommunicator pc) throws Exception {
		if (curVal.compareTo(dimmerCommand.getTargetVal()) != 0) {
			return;
		}

		for (DimmerStatusFeedback dsf : dimmerStatusFeedback) {
			if (dsf.sourceIgnore.isPresent() && dsf.sourceIgnore.equals(dimmerCommand.getOrigin())) {
				continue;
			}

			DPTXlator8BitUnsigned dDPTXlator8BitUnsigned = new DPTXlator8BitUnsigned(DPT_PERCENT_U8);
			dDPTXlator8BitUnsigned.setValue(curVal.intValue());
			pc.write(dsf.groupAddress, dDPTXlator8BitUnsigned);
		}
	}

	/**
	 * The feedback is sent to the given {@link GroupAddress} only when the source of the dimmer event is different captured via {@link DimmerCommand#getOrigin()}
	 */
	static class DimmerStatusFeedback {

		private GroupAddress groupAddress;
		private Optional<IndividualAddress> sourceIgnore;

		public DimmerStatusFeedback(final GroupAddress groupAddress, final Optional<IndividualAddress> sourceIgnore) {
			this.groupAddress = groupAddress;
			this.sourceIgnore = sourceIgnore;
		}
	}

	public static class DimmerStatusFeedbackProcessorBuilder {

		private DimmerStatusFeedbackProcessor dimmerStatusFeedbackProcessor = new DimmerStatusFeedbackProcessor();

		public DimmerStatusFeedbackProcessorBuilder feedbackToGroupAddress(GroupAddress groupAddress) {
			dimmerStatusFeedbackProcessor.dimmerStatusFeedback.add(new DimmerStatusFeedback(groupAddress, empty()));
			return this;
		}

		public DimmerStatusFeedbackProcessorBuilder feedbackToGroupAddressWhenNotOriginatesFromIndividualAddress(GroupAddress groupAddress,
				IndividualAddress individualAddress) {
			dimmerStatusFeedbackProcessor.dimmerStatusFeedback.add(new DimmerStatusFeedback(groupAddress, of(individualAddress)));
			return this;
		}

		public DimmerStatusFeedbackProcessor build() {
			return dimmerStatusFeedbackProcessor;
		}
	}
}
