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

/**
* In short, this class is where the magic happens.
*
* This class creates the panel that will be added 
* to the JFrame. It adds two radio buttons (send, 
* receive), and a submit button.
*/
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

		// what happens when the button is clicked
		submit.addActionListener((e) -> {
				
				String transaction = "";
				
				// sets whether the transaction is outgoing or incoming
				if (sendMessage.isSelected()) {
					transaction = "send";
				} else if (checkNewMessage.isSelected()) {
					transaction = "receive";
				}
				
				switch (transaction) {
				
				case "send":
					try {
						// gets the message from the user
						outgoingMessage = JOptionPane.showInputDialog("New message");
						
						// if the user clicks "Cancel," break out of the switch statement
						if (outgoingMessage.equals(String.valueOf(JOptionPane.CANCEL_OPTION))) {
							break;
						}
					// if the user tries to send an empty string, break out of the switch statement
					} catch (NullPointerException npe) {
						break;
					}
					
					writeData();
					
					break;
					
				case "receive":
					readData();
					break;
				
				}
		});

		add(sendMessage);
		add(checkNewMessage);
		add(submit);

	}
	
	public void writeData() {
		FileOutputStream outFile;
		ObjectOutputStream out;
		try {
			outFile = new FileOutputStream(messageFilePath);
			out = new ObjectOutputStream(outFile);
			out.writeObject(outgoingMessage);
			out.close();
			outFile.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No folder path chosen");
		}
	}
	
	public void readData() {
		FileInputStream inFile;
		ObjectInputStream in;
		try {
			inFile = new FileInputStream(messageFilePath);
			in = new ObjectInputStream(inFile);
			incomingMessage = (String) in.readObject();
			in.close();
			inFile.close();
			JOptionPane.showMessageDialog(null, incomingMessage);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No message");
		}
	}
	
	public void getFilePath() {
		FileInputStream inFile;
		ObjectInputStream in;
		try {
			inFile = new FileInputStream("messageFilePath.ser");
			in = new ObjectInputStream(inFile);
			messageFilePath = (String) in.readObject();
			in.close();
			inFile.close();
		// if the program can't find a file called messageFilePath...
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
				outFile = new FileOutputStream("messageFilePath.ser");
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
		/* ask if the user wants to delete their current conversation
		when they change to a new folder */ 
		int option = JOptionPane.showConfirmDialog(null,
			"Do you wish to delete the current conversation?", "Warning", 
			JOptionPane.YES_NO_CANCEL_OPTION);
		try {
			switch (option) {
			
			// if they say yes...
			case JOptionPane.YES_OPTION:
				// delete the message file path
				new File("messageFilePath.ser").delete();
				// delete the message
				new File(messageFilePath).delete();
				break;
			// if they say no...
			case JOptionPane.NO_OPTION:
				/* delete the message file path (even though we don't 
				wish to delete the current message, we still want to 
				delete the current file path */
				new File("messageFilePath").delete();
				break;
				
			// if they hit "Cancel"...
			case JOptionPane.CANCEL_OPTION:
				// don't do anything
				break;
			}
		} catch (Exception e) {}
		getFilePath();
	}
	
}
