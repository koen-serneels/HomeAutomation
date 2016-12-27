package be.error.rpi.dac.i2c;

import static be.error.rpi.config.RunConfig.getInstance;
import static org.apache.commons.lang3.ArrayUtils.add;
import static org.apache.commons.lang3.ArrayUtils.reverse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CDevice;

/**
 * @author Koen Serneels
 */
public class I2CCommunicator extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(I2CCommunicator.class);

	private BlockingQueue<WriteCommand> commandQueue = new LinkedBlockingDeque();

	@Override
	public void run() {
		logger.debug("Communicator thread starting");
		WriteCommand writeCommand = null;
		while (true) {
			try {
				writeCommand = commandQueue.take();
				writeCommand.i2cDevice.write(writeCommand.value, 0, writeCommand.value.length);
				sleep(5);
				//If there is a next command, and the command is meant for the same board as we have just written to, wait to avoid overload
				if (commandQueue.peek() == null || (commandQueue.peek() != null && commandQueue.peek().boardAddress == writeCommand.boardAddress)) {
					sleep(8);
				}
			} catch (Exception e) {
				logger.error("Communicator thread had exception. Write command: " + writeCommand, e);
			}
		}
	}

	public synchronized void write(int boardAddress, int channel, byte[] b) throws IOException {
		WriteCommand writeCommand = new WriteCommand();
		writeCommand.boardAddress = boardAddress;
		writeCommand.i2cDevice = getInstance().getBus().getDevice(boardAddress);
		writeCommand.channel = channel;
		reverse(b);
		writeCommand.value = add(b, 0, (byte) channel);
		try {
			commandQueue.put(writeCommand);
		} catch (InterruptedException interruptedException) {
			logger.error("Communicator thread had interrupted exception. Device: " + boardAddress, interruptedException);
		}
	}

	class WriteCommand {
		int boardAddress;
		I2CDevice i2cDevice;
		int channel;
		byte[] value;

		@Override
		public String toString() {
			try {
				return "WriteCommand{" + "i2cDevice=" + boardAddress + " to " + "channel " + channel + " value=" + new BigInteger(new byte[] { value[2], value[1] })
						+ '}';
			} catch (Exception e) {
				logger.error("Could not to string", e);
				throw new RuntimeException(e);
			}
		}
	}
}
