package bluetooth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * ����ӳٷ�������¼ʱ�䣬����ӳٳ���һ��ʱ�䣬�򲻲��ţ���֤ʱ�̲������µģ�
 */
public class BlueService implements Runnable {
	// �����ʾ��
	private UUIDGen mUUIDGen = null;
	private UUID mUUID = null;
	private long time0;

	// ������֪ͨ��
	private StreamConnectionNotifier notifier;

	private LocalDevice localDevice = null;

	// �����¼
	ServiceRecord serviceRecord;

	public BlueService() {
		initialBluetooth();
		mUUIDGen = new UUIDGen(localDevice.getBluetoothAddress());
		mUUID = mUUIDGen.next();
	}

	/**
	 */
	public void startService() {
		new Thread(this).start();
		System.out.print("Start Service");
	}

	public void run() {
		try {
			notifier = (StreamConnectionNotifier) Connector.open(getConnectionStr());
			serviceRecord = localDevice.getRecord(notifier);
		} catch (Exception ex) {
			System.out.println("occur exception here: " + ex.getMessage());
		}

		while (true) {
			StreamConnection conn = null;
			try {
				conn = notifier.acceptAndOpen();
				time0 = new Date().getTime();

			} catch (Exception ex) {
				System.out.println("occur exception when accept connection~");
				continue;
			}

			System.out.println("connectted : " + conn.toString());
			new Thread(new ProcessConnection(conn)).start();
		}
	}

	/**
	 * ��ȡ�����ַ���
	 * 
	 * @return
	 */
	private String getConnectionStr() {
		StringBuffer sb = new StringBuffer("btspp://");
		sb.append("localhost").append(":");
		sb.append(mUUID.toString());
		sb.append(";name=BlueMessage");
		sb.append(";authorize=false");
		return sb.toString();
	}

	/**
	 * ������ʼ��
	 * 
	 * @return
	 */
	public boolean initialBluetooth() {
		boolean btReady = true;
		System.out.println("init...");
		try {
			localDevice = LocalDevice.getLocalDevice();
			/*
			 * if(!localDevice.setDiscoverable(DiscoveryAgent.GIAC)){
			 * btReady=false; }
			 */
		} catch (Exception e) {
			btReady = false;
			e.printStackTrace();
		}

		return btReady;
	}

	/**
	 * ����ͻ������ӵ��߳�
	 * 
	 * @author royen
	 * @since 2010.1.25
	 */
	private class ProcessConnection implements Runnable {
		// ������
		private StreamConnection conn = null;
		private int hz;
		private int bits;
		private int channel;
		private int bufSize;

		// ��ȡ����
		InputStream is;

		public ProcessConnection(StreamConnection conn) {
			this.conn = conn;
			// todo get hz,bits,channel,buffsize
			// try {
			//// is = conn.openInputStream();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// DataInputStream reader;
			try {
				is = conn.openInputStream();
				hz = readInt(is);
//				bits = readInt(is);
//				channel = readInt(is);
				bufSize = readInt(is);
				System.out.println("user connect at " + new Date().toString()+ "  " + bufSize + " buf size--hz--bits-channel"
						+ hz + " " + channel + "  " + bits);
				// reader.close();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		private SourceDataLine getLine(AudioFormat audioFormat) {
			SourceDataLine res = null;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			try {
				res = (SourceDataLine) AudioSystem.getLine(info);
				res.open(audioFormat);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getClass());
			}
			return res;
		}

		public long parse(byte[] bytes) {
			long ans = 0;
			for (int i = 0; i < 8; i++) {
				ans += ((int) (bytes[i] & 0xFF)) << ((7 - i) * 8);
			}
			return ans;
		}

		public void run() {
			System.out.println("client connected...");

			try {
				// is = conn.openInputStream();

				AudioFormat af = new AudioFormat(hz, 16,1, true, false);
//				int bufSize = 2048;
				SourceDataLine line = getLine(af);
				if (line == null)
					System.out.println("null");
				line.open(af, bufSize);
				System.out.println("hehehe");
				line.start();
				int inBytes = 0;
				long time = 0;// ���
				long dt = 0;
				long timex = -100;
				byte[] bytes = new byte[bufSize];
				int calc = 0;// �ж��Ƿ�ʧȥ������
				while (true) {
					try {
						is.read(bytes, 0, bytes.length);
						// time = parse(bytes);
						// dt = new Date().getTime() - time0;
						// String temp=new String(bytes);
						// System.out.println(temp);
					} catch (IOException e) {
						e.printStackTrace();
					}
					// System.out.println(dt - time);
					// if (time == -538976290) {// �ֻ�disconnect��
					// BlueService.this.startService();
					// line.close();
					// return;
					// }
					int outBytes = line.write(bytes, 0, bytes.length);
					// if ((inBytes >= 0) && (Math.abs(dt - time) < 200) &&
					// (timex != time)) {
					// int outBytes = line.write(bytes, 8, inBytes - 8);
					// System.out.println(outBytes +"write out bytes");
					// calc = 0;
					// } else if (timex == time) {
					// calc++;// �ֻ����ߣ��ӳ���
					// if (calc > 1000) {
					// BlueService.this.startService();
					// line.close();
					// return;
					// }
					//// System.out.println("here");
					// }
					// timex = time;
					// System.out.println("out bytes " + outBytes);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("occur exception ,message is " + ex.getClass());
			}
		}

	}

	public int readInt(InputStream in) throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

}
