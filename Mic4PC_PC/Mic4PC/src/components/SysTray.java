package components;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * @author royen
 * @since 2010.2.10
 */
public class SysTray {
	
	public void show(){
		try{			 
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); 
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
		}
		catch (Exception ex) { 
			ex.printStackTrace(); 
		} 	

		/** �жϱ���ϵͳ�Ƿ�֧������ */
		if(SystemTray.isSupported()){ 
			this.tray(); 
		} 
		else{
			JOptionPane.showMessageDialog(null,"sdfasdfffffffffffffffff");
		}
	}
	
	/**  ������ش��� 	 */ 
	private void tray(){
		
		/**  ��ñ�����ϵͳ���̵�ʵ��  */
		tray = SystemTray.getSystemTray(); 
			
		URL imageUrl=SysTray.class.getResource("system.gif");
				
		/** ��������ͼ�� */
		ImageIcon icon = new ImageIcon(imageUrl);

		/** �����Ҽ�����ʽ�˵� */
		PopupMenu pop = new PopupMenu(); // ����һ���Ҽ�����ʽ�˵� 

		/** �Ҽ�����ʽ�˵���ѡ�� */		
		MenuItem exit = new MenuItem("exit"); 
		MenuItem about = new MenuItem("about"); 

		pop.add(exit);
		pop.add(about);
		
		trayIcon = new TrayIcon(icon.getImage(), "0000000000000000", pop); 
		
		try{
			/** ������ͼ����ӵ�ϵͳ������ʵ���� */
			tray.add(trayIcon); // 
		}
		catch(Exception e){
			e.printStackTrace();
		}

		/** ��������¼� */ 
		trayIcon.addMouseListener(new MouseAdapter() { 
			
			public void mouseClicked(MouseEvent e) { 
				if(e.getClickCount()==2){  
					JOptionPane.showMessageDialog(null,"Copyright @2010 Royen\nAll Right Reserved\nauthor fairyRT\nversion 1.0\n","about",JOptionPane.INFORMATION_MESSAGE);
				} 
			} 
		}); 

		/** �˳��˵����¼� */
		exit.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) { 
				try{
					//bluetooth.getBluetooth().closeServer();					
					
					tray.remove(trayIcon); 					
					tray=null;					
					
					System.gc();
					
					System.exit(0);
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			} 
		}); 
		
		/** ���ڲ˵����¼� */
		about.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) {				
				JOptionPane.showMessageDialog(null,"Copyright @2013 \nAll Right Reserved\nauthor fairyRT\nversion 1.0\n","about",JOptionPane.INFORMATION_MESSAGE);
			} 
		}); 	
	}
	
	/** ����ͼ�� */
	private TrayIcon trayIcon = null;
	
	/** ������ϵͳ���̵�ʵ��  */
	private SystemTray tray = null;

}
