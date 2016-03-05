package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class UARTControl {
	public Thread t1;
	
	public static ArrayList<String> getPorts(){
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> list = new ArrayList<String>();
		while (ports.hasMoreElements()) {
			CommPortIdentifier commPortIdentifier = (CommPortIdentifier) ports.nextElement();
			System.out.println(commPortIdentifier.getName());
			list.add(commPortIdentifier.getName());
		}
		String[] l = new String[list.size()];
		return list;
	}
	
	public UARTControl(String portName) throws Exception{
		
		
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			int timeout = 2000;
			CommPort commPort = portIdentifier.open(this.getClass().getName(), timeout);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				t1 = new Thread(new SerialReader(in));
				t1.start();
				
				out.write('g');
				Thread.sleep(10);
				out.write(52);
				Thread.sleep(10);
				out.write(13);
				Thread.sleep(10);
		

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public static class SerialReader implements Runnable {

		InputStream in;
		boolean isRunning = true;
		
		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while (isRunning){
					if ((len = this.in.read(buffer)) > -1) {
						System.out.print(new String(buffer, 0, len));
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class SerialWriter implements Runnable {

		OutputStream out;

		public SerialWriter(OutputStream out) {
			this.out = out;
		}

		public void run() {
			try {
				
				this.out.write('l');
				Thread.sleep(100);
				this.out.write(55);
				Thread.sleep(100);
				this.out.write(13);
				Thread.sleep(100);
//				int c = 0;
//				while ((c = System.in.read()) > -1) {
//					this.out.write(c);
//				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			UARTControl u = new UARTControl("/dev/ttyUSB0");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
