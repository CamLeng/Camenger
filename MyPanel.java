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
	private String directions = "Camenger uses shared folders created by cloud services\n" + 
								"such as Dropbox or Google Drive in order to message people.\n\n" + 
								
								"Step 1. If you already have your shared folders set up, skip to step 3.\n" + 
								"Step 2. Create a shared folder with every person with which you want to message.\n" + 
								"Step 3. Select your shared folder in the popup on the next screen and click \"Open\"";

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
					
					writeOutgoingMessage();
					
					break;
					
				case "receive":
					readIncomingMessage();
					break;
				
				}
		});

		add(sendMessage);
		add(checkNewMessage);
		add(submit);

	}
	
	/**
	* Writes the outgoing message to the file "message" 
	* located in the "messageFilePath" string.
	*/
	public void writeOutgoingMessage() {
		FileOutputStream outFile;
		ObjectOutputStream out;
		try {
			outFile = new FileOutputStream(messageFilePath);
			out = new ObjectOutputStream(outFile);
			out.writeObject(outgoingMessage);
			out.close();
			outFile.close();
		} catch (Exception e) {
			// if the user didn't choose a folder, display "No folder path chosen"
			JOptionPane.showMessageDialog(null, "No folder path chosen");
		}
	}
	
	/**
	* Reads the incoming message coming from the file "message" 
	* located in the "messageFilePath" string.
	*/
	public void readIncomingMessage() {
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
			inFile = new FileInputStream("messageFilePath");
			in = new ObjectInputStream(inFile);
			messageFilePath = (String) in.readObject();
			in.close();
			inFile.close();
		// if the program can't find a file called messageFilePath...
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, directions, "Directions", JOptionPane.PLAIN_MESSAGE);
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
				new File("messageFilePath").delete();
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
