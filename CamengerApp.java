/**
* @author Cameron Lengerich
*
* Camenger is a personal messenger. You can send text to and from
* someone else that has this same app, provided you have a shared
* folder of some sort. Camenger saves the serialized string object 
* to a file in your shared folder. The file can then be opened by 
*
* Camenger and displayed to the user in plain text. Camenger is a 
* very basic messenger, and is one of my first projects on GitHub.
*/

public class CamengerApp {

	/**
	* Creates a frame object and sets it visible
	*/
	public static void main(String[] args) {
		MyFrame frame = new MyFrame();
		frame.setVisible(true);
	}
	
}
