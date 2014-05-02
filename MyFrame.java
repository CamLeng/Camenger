import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
* This class creates a JFrame with a menubar
* used to access other options
*/
public class MyFrame extends JFrame {
	MyPanel panel = new MyPanel();

	public MyFrame() {
		setLocation(500, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		JMenu options = new JMenu("Options");
		bar.add(options);
		JMenuItem changeSharedFolder = new JMenuItem("Change Shared Folder");
		options.add(changeSharedFolder);
		
		changeSharedFolder.addActionListener((e) -> { panel.changeMessageFilePath(); });
		
		add(panel);
		pack();
	}
}
