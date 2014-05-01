import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class MyPanel extends JPanel  {
	private JRadioButton sendMessage;
	private JRadioButton checkNewMessage;
	private ButtonGroup bg;
	private JButton submit;
	private String outgoingMessage;
	private String incomingMessage;
	private String errorMessage = "Hmm.. Something went wrong.";
	private String messageFilePath;

	public MyPanel() {
		getFilePath();
		
		sendMessage = new JRadioButton("Send message");
		checkNewMessage = new JRadioButton("Check for new message");
		bg = new ButtonGroup();
		bg.add(sendMessage);
		bg.add(checkNewMessage);

		submit = new JButton("Submit");

		submit.addActionListener((e) -> {
				
				String transaction = "";
				
				if (sendMessage.isSelected()) {
					transaction = "send";
				} else if (checkNewMessage.isSelected()) {
					transaction = "receive";
				}
				
				switch (transaction) {
				
				case "send":
					try {
						outgoingMessage = JOptionPane.showInputDialog("New message");
						if (outgoingMessage.equals(String.valueOf(JOptionPane.CANCEL_OPTION))) {
							break;
						}
					} catch (NullPointerException npe) {
						break;
					}
					
					FileOutputStream outFile;
					ObjectOutputStream out;
					try {
						outFile = new FileOutputStream(messageFilePath);
						out = new ObjectOutputStream(outFile);
						out.writeObject(outgoingMessage);
						out.close();
						outFile.close();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "No folder path chosen");
					}
					break;
					
				case "receive":
					FileInputStream inFile;
					ObjectInputStream in;
					try {
						inFile = new FileInputStream(messageFilePath);
						in = new ObjectInputStream(inFile);
						incomingMessage = (String) in.readObject();
						in.close();
						inFile.close();
						JOptionPane.showMessageDialog(null, incomingMessage);
					} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, "No message");
					}
					break;
				
				}
		});

		add(sendMessage);
		add(checkNewMessage);
		add(submit);

	}
	
	public void getFilePath() {
		FileInputStream inFile;
		ObjectInputStream in;
		try {
			inFile = new FileInputStream("messageFilePath");
			in = new ObjectInputStream(inFile);
			messageFilePath = (String) in.readObject();
			in.close();
			inFile.close();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Directions: " + 
					"\n1. Create a shared folder using a service such as Dropbox or Google Drive or \nselect " + 
					"a shared folder you have already created" + 
					"\n\n2. Select the shared folder you just created");
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.showOpenDialog(null);
			File myFile = fileChooser.getSelectedFile();
			
			try {
				messageFilePath = fixSlashes(myFile.toString()) + "/message";
			} catch (NullPointerException e) {}
				
			FileOutputStream outFile;
			ObjectOutputStream out;
			try {
				outFile = new FileOutputStream("messageFilePath");
				out = new ObjectOutputStream(outFile);
				out.writeObject(messageFilePath);
				out.close();
				outFile.close();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, errorMessage);
			}
		}
	}
	
	public StringBuffer fixSlashes(String filePath) {
		StringBuffer newFilePath = new StringBuffer(filePath);
		for (int i=0; i<filePath.length(); i++) {
			if (newFilePath.charAt(i) == '\\') {
				newFilePath.replace(i, i+1, "/");
			}
		}
		return newFilePath;
	}

	public void changeMessageFilePath() {
		int option = JOptionPane.showConfirmDialog(null,
			"Do you wish to delete the current conversation?", "Warning", 
			JOptionPane.YES_NO_CANCEL_OPTION);
		try {
			switch (option) {
			case JOptionPane.YES_OPTION:
				new File("messageFilePath").delete();
				new File(messageFilePath).delete();
				break;
			case JOptionPane.NO_OPTION:
				new File("messageFilePath").delete();
				break;
			case JOptionPane.CANCEL_OPTION:
				break;
			}
		} catch (Exception e) {}
		getFilePath();
	}
	
}
