package be.error.rpi.test;

import static tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned.DPT_PERCENT_U8;
import static tuwien.auto.calimero.link.KNXNetworkLinkIP.TUNNELING;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

//0 = SK1
//1 = HAL
//2 = dressing
//3 = badkamer
/**
 * Created by koen on 01.07.16.
 */
public class TestMe2 {

	public static void main(String[] args) throws Exception {

		KNXNetworkLinkIP knxNetworkLinkIP = new KNXNetworkLinkIP(TUNNELING, new InetSocketAddress("192.168.0.192", 0), new InetSocketAddress("192.168.0.6", 3671), true,
				new TPSettings(true));

		ProcessCommunicatorImpl pc = new ProcessCommunicatorImpl(knxNetworkLinkIP);
		DPTXlator8BitUnsigned dDPTXlator8BitUnsigned = new DPTXlator8BitUnsigned(DPT_PERCENT_U8);
		dDPTXlator8BitUnsigned.setValue(2);
		pc.write(new GroupAddress("15/0/0"), dDPTXlator8BitUnsigned);
		pc.detach();
		knxNetworkLinkIP.close();

		//		knxNetworkLinkIP.close();

		/*KNXNetworkLinkIP knxNetworkLinkIP = new KNXNetworkLinkIP(TUNNELING, new InetSocketAddress("192.168.0.10", 0), new InetSocketAddress("192.168.0.6", 3671), true,
				new TPSettings(true));
		ProcessCommunicatorImpl pc = new ProcessCommunicatorImpl(knxNetworkLinkIP);

		DPTXlator8BitUnsigned dDPTXlator8BitUnsigned = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_PERCENT_U8);
		dDPTXlator8BitUnsigned.setValue(55);
		pc.write(new GroupAddress("15/0/0"), dDPTXlator8BitUnsigned);*/

		/*I2CBus bus = I2CFactory.getInstance(BUS_1);
		final I2CDevice i2CDeviceOne = bus.getDevice(0x59);

		byte[] b = convert(new BigDecimal(0));
		System.err.println(new BigInteger(b));
		//i2CDeviceOne.write(new byte[] { 1, b[1], b[0] }, 0, 3);
		sleep(15);
		i2CDeviceOne.write(new byte[] { 3, b[1], b[0] }, 0, 3);
		//i2CDeviceOne.write(new byte[] { 2, b[1], b[0] }, 0, 3);*/




/*			for (int i = 0; i <= 100; i++) {
				b = convert(new BigDecimal(i));
				i2CDeviceOne.write(new byte[] { 0, b[1], b[0] }, 0, 3);
				ArrayUtils.reverse(b);
				System.err.println(new BigInteger(b));
				sleep(10);
			}*/
		//sleep(2000);

		//final I2CDevice i2CDeviceTwo = bus.getDevice(0x5B);

/*		System.err.println("TOEEETERS");
		new Thread() {
			@Override
			public void run() {
				try {
					int i = 0;
					boolean up = true;
					while (true) {
						if (up) {
							i++;
						} else {
							i--;
						}
						byte[] b = convert(new BigDecimal(i));
						i2CDeviceOne.write(new byte[] { 0, b[1], b[0] }, 0, 3);
						Thread.sleep(2);
						i2CDeviceTwo.write(new byte[] { 0, b[1], b[0] }, 0, 3);
						Thread.sleep(2);
						i2CDeviceTwo.write(new byte[] { 0, b[1], b[0] }, 0, 3);
						Thread.sleep(2);
						i2CDeviceOne.write(new byte[] { 2, b[1], b[0] }, 0, 3);
						Thread.sleep(2);
						if (i == 100) {
							up = false;
						} else if(i==0) {
							up = true;
						}

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}.start();*/


		/*for(int i= 0; i< 100;i++) {
			 b = convert(new BigDecimal(i));
			i2CDevice.write(new byte[] { 0, b[1], b[0] }, 0, 3);
			Thread.sleep(30);
		}*/
		/*final BlockingQueue<String> commandQueue = new LinkedBlockingDeque();
		Thread t = new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						System.err.println("Going to take");
						String s = commandQueue.take();
						System.err.println("Read:" + s);
						Thread.sleep(1000);
					} catch (Exception e) {
						System.err.println("INTERUPTED");
						e.printStackTrace();
					}
				}
			}

			public BlockingQueue<String> getCommandQueue() {
				return commandQueue;
			}
		};
		t.start();
		Thread.sleep(1000);
		System.err.println("Calling interut");
		t.interrupt();
		Thread.sleep(1000);
		commandQueue.put("test");


		Thread.sleep(3000);*/





		/*for(double i = 100; i>=0; i=i-0.1){
			 b = convert(new BigDecimal(i));
			i2CDevice.write(new byte[] { 3, b[1], b[0] }, 0, 3);
			Thread.sleep(100);
		}*/

		//System.err.println(i2CDevice.read());

/*		final InetSocketAddress remote = new InetSocketAddress("192.168.0.6", 3671);
		KNXNetworkLinkIP knxNetworkLinkIP = new KNXNetworkLinkIP(TUNNELING, null, remote, true, new KnxIPSettings(new IndividualAddress("1.1.4")));
		ProcessCommunicatorImpl pc = new ProcessCommunicatorImpl(knxNetworkLinkIP);
		pc.write(new GroupAddress("5/1/0"), true);
		System.err.println("plop");*/

		//		knxNetworkLinkIP.close();

		/*KNXNetworkLinkIP knxNetworkLinkIP = new KNXNetworkLinkIP(TUNNELING, new InetSocketAddress("192.168.0.10", 0), new InetSocketAddress("192.168.0.6", 3671), true,
				new TPSettings(true));
		ProcessCommunicatorImpl pc = new ProcessCommunicatorImpl(knxNetworkLinkIP);

		DPTXlator8BitUnsigned dDPTXlator8BitUnsigned = new DPTXlator8BitUnsigned(DPTXlator8BitUnsigned.DPT_PERCENT_U8);
		dDPTXlator8BitUnsigned.setValue(55);
		pc.write(new GroupAddress("15/0/0"), dDPTXlator8BitUnsigned);*/

	/*
		new Thread(){
			@Override
			public void run() {


				pc.addProcessListener(new ProcessListenerEx() {
					@Override
					public void groupReadRequest(final ProcessEvent e) {

					}

					@Override
					public void groupReadResponse(final ProcessEvent e) {

					}

					@Override
					public void groupWrite(final ProcessEvent e) {
						if (e.getDestination().getMainGroup() == 5 && e.getDestination().getMiddleGroup() == 3 && e.getDestination().getSubGroup8() == 5) {
							try{
								System.err.println(asBool(e));
							}catch(Exception ex){

							}
							try{
								System.err.println(asControl(e));
							}catch(Exception ex){

							}
							try{
								System.err.println(asString(e));
							}catch(Exception ex){

							}
							try{
								System.err.println(asFloat(e,false));
							}catch(Exception ex){

							}
							System.err.println("------------------------------------------");

						}
					}

					@Override
					public void detached(final DetachEvent e) {

					}
				});
			}
		}.start();

		Thread.sleep(100000);*/
	}

	private static byte[] convert(BigDecimal bigDecimal) {
		BigDecimal result = new BigDecimal(1023).divide(new BigDecimal(100)).multiply(bigDecimal).setScale(0, RoundingMode.HALF_UP);
		byte[] b = result.toBigInteger().toByteArray();
		byte[] r = new byte[0];
		if (b.length == 1) {
			return new byte[] { 0, b[0] };
		}
		return b;
	}
}

