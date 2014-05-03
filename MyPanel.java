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
	private boolean messageExists = false;
	private String outgoingMessage;
	private String incomingMessage;
	private String errorMessage = "Hmm.. Something went wrong.";
	private String messageFilePath;
	private String directions = "Camenger uses shared folders created by cloud services\n" + 
								"such as Dropbox or Google Drive in order to message people.\n\n" + 
								
								"Step 1. If you already have your shared folders set up, skip to step 3.\n" + 
								"Step 2. Create a shared folder with every person with which you want to message.\n" + 
								"Step 3. Select your shared folder in the popup on the next screen and click \"Open\"";

	/**
	* This constructor adds all of the components to the
	* panel and contains the switch statement for the 
	* different types of transactions (send or receive)
	*/
	public MyPanel() {
	
		// get the file path of "message"
		getFilePath();
		
		// create two radio buttons
		sendMessage = new JRadioButton("Send message");
		checkNewMessage = new JRadioButton("Check for new message");
		
		// create a button group and add the two radio buttons to it
		bg = new ButtonGroup();
		bg.add(sendMessage);
		bg.add(checkNewMessage);

		// create a submit button
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
				
				// if the transaction is outgoing...
				case "send":
					try {
					
						// gets the message from the user
						outgoingMessage = JOptionPane.showInputDialog("New message");
						
						// if the user clicks "Cancel," break out of the switch statement
						if (clickedCancel()) {
							break;
						}
						
					// if the user tries to send an empty string, break out of the switch statement
					} catch (NullPointerException npe) {
						break;
					}
					
					// if it made it this far, write the message
					writeOutgoingMessage();
					break;
					
				// if the transaction is incoming...
				case "receive":
				
					// read the incoming message
					readIncomingMessage();
					
					// if the message exists...
					if (getMessageExists()){
					
						// display the message
						JOptionPane.showMessageDialog(null, incomingMessage);
					}
					break;
				}
		});

		// add the components to the panel
		add(sendMessage);
		add(checkNewMessage);
		add(submit);

	}
	
	/**
	* Decides if the user clicked "Cancel" on the "Input message" dialog
	*/
	public boolean clickedCancel() {
		
		if (outgoingMessage.equals(String.valueOf(JOptionPane.CANCEL_OPTION))) {
			return true;
		}
		return false;
	}
	
	/**
	* Writes the outgoing message to the file "message" 
	* located in the "messageFilePath" string.
	*/
	public void writeOutgoingMessage() {
		try {
			FileOutputStream outFile = new FileOutputStream(messageFilePath);
			ObjectOutputStream out = new ObjectOutputStream(outFile);
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
			messageExists = true;
			inFile = new FileInputStream(messageFilePath);
			in = new ObjectInputStream(inFile);
			incomingMessage = (String) in.readObject();
			in.close();
			inFile.close();
			
		// if there is no message
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No message");
			messageExists = false;
		}
	}
	
	/**
	* This method reads the file "messageFilePath" which contains 
	* the file path for the file "message."
	*/
	public void readFilePath() throws Exception {
		FileInputStream inFile = new FileInputStream("messageFilePath");
		ObjectInputStream in = new ObjectInputStream(inFile);
		messageFilePath = (String) in.readObject();
		in.close();
		inFile.close();
	}
	
	/**
	* This method writes the file path of "message" to the file "messageFilePath"
	*/
	public void writeFilePath() throws Exception {
		FileOutputStream outFile = new FileOutputStream("messageFilePath");
		ObjectOutputStream out = new ObjectOutputStream(outFile);
		out.writeObject(messageFilePath);
		out.close();
		outFile.close();
	}
	
	/**
	* This file gets the folder that the user selects
	* and returns it
	* 
	* @return folderPath
	*/
	public File getSelectedFile() {
		JOptionPane.showMessageDialog(null, directions, "Directions", JOptionPane.PLAIN_MESSAGE);
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(null);
		File folderPath = fileChooser.getSelectedFile();
		
		return folderPath;
	}
	
	/**
	* This method either reads the file path from the file 
	* "messageFilePath" if it exists. If it does not exist,
	* it asks the user to select a folder.
	* 
	*/
	public void getFilePath() {
		
		// read the file path
		try {
			readFilePath();
			
		// if the program can't find a file called messageFilePath...
		} catch (Exception e1) {
			
			// get the folder path that the user selects
			File folderPath = getSelectedFile();
			
			/* fix the slashes and add "/message" to the end.
			I can do this because the message file is called 
			"message" and is in the file path called folderPath */
			try {
				messageFilePath = fixSlashes(folderPath.toString()) + "/message";
			} catch (NullPointerException e) {}
			
			// write the file path of "message" to the file called "messageFilePath"
			try {
				writeFilePath();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, errorMessage);
			}
		}
	}
	
	/**
	* Windows shows file paths by backslashes (\), I need to 
	* convert those to forward slashes (/). This method changes 
	* each backslash to a forward slash.
	* 
	* This method will only be useful on Windows machines
	*
	* @param filepath
	* @return newFilePath
	*/
	public StringBuffer fixSlashes(String filePath) {
		StringBuffer newFilePath = new StringBuffer(filePath);
		for (int i=0; i<filePath.length(); i++) {
			if (newFilePath.charAt(i) == '\\') {
				newFilePath.replace(i, i+1, "/");
			}
		}
		return newFilePath;
	}

	/**
	* This method is called when the user wants to change the 
	* file path. This method contains a switch statement which
	* handles each case of what could happen (Yes, No, Cancel).
	*/
	public void changeMessageFilePath() {
		/* ask if the user wants to delete their current conversation
		when they change to a new folder */ 
		int option = JOptionPane.showConfirmDialog(null,
			"Do you wish to delete the current conversation?", "Warning", 
			JOptionPane.YES_NO_CANCEL_OPTION);
		try {
			switch (option) {
			
			// if they hit "Yes"...
			case JOptionPane.YES_OPTION:
				// delete the message file path
				new File("messageFilePath").delete();
				// delete the message
				new File(messageFilePath).delete();
				break;
			// if they hit "No"...
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
		
		// finally get the new file path
		getFilePath();
	}
	
	public boolean getMessageExists() {
		return messageExists;
	}
	
}
