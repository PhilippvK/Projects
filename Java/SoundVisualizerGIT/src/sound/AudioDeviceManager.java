/*
* created by Philipp van Kempen (phvankempen@googlemail.com)
* last change 21.02.2016
* 
*/

package sound;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/*
 * this part of the program can list supported audio devices and control them
 */
public class AudioDeviceManager {
	/*
	 * list with recording devices
	 */
	public ArrayList<Mixer> mixers = new ArrayList<Mixer>();
	/*
	 * list with the supported controls
	 */
	public ArrayList<Mixer> volumes = new ArrayList<Mixer>();

	/*
	 * scan all devices on creation
	 */
	public AudioDeviceManager() {
		createDeviceList();
	}

	/*
	 * do setVol with every volume control
	 */
	public void setAllVol(float v) {
		for (Mixer mixer : volumes) {
			setVol(mixer, v);
		}
	}

	/*
	 * set a specific control to a volume v
	 */
	public void setVol(Mixer mixer, float v) {
		Line.Info lineInfo = mixer.getTargetLineInfo()[0]; // get line info from
															// given mixer
		Line line = null; // create line

		boolean opened = true;

		try {
			
			line = mixer.getLine(lineInfo); // get the line from the info object
			opened = line.isOpen() || line instanceof Clip; // set boolean
			if (!opened) {
				line.open(); // open if not already opened
			}
			FloatControl volCtrl = (FloatControl) line.getControl(FloatControl.Type.VOLUME); // get control
																							 // from line
			volCtrl.setValue(v); // set volume control to a value between 0.0 and 1.0
									
		} catch (LineUnavailableException e) { // if the line.open() fails...
			System.out.println("Fehler: " + e.getMessage());
		}
	}

	/*
	 * fill to lists with supported devices and controls
	 */
	public void createDeviceList() {
		Mixer.Info[] allMixerInfos = AudioSystem.getMixerInfo();
		Mixer[] allMixers = new Mixer[allMixerInfos.length];
		for (int i = 0; i < allMixerInfos.length; i++) {
			allMixers[i] = (Mixer) AudioSystem.getMixer(allMixerInfos[i]);
		}
		for (int i = 0; i < allMixers.length; i++) { // for every device
			if (testRec(allMixers[i])) { // if not failig it ist supported
				mixers.add(allMixers[i]);
			}
			if (testVol(allMixers[i])) { // if not failig it ist supported
				volumes.add(allMixers[i]);
			}

		}

	}

	/*
	 * if recording from line with given mixer works, then return true, if
	 * failing, return false
	 */
	public boolean testRec(Mixer mixer) {
		final AudioFormat format = new AudioFormat(22050, 16, 1, true, false);
		final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line;
		try {
			/*
			 * Pruefen ob Format von der Soundkarte unterstuetzt wird
			 */
			if (!AudioSystem.isLineSupported(dataLineInfo)) {
				return false;
			}
			/*
			 * open line and test
			 */
			line = (TargetDataLine) mixer.getLine(dataLineInfo);
			final int numberOfSamples = 256;
			line.open(format, numberOfSamples);
			line.start();
			line.stop();
			line.close();
			return true;
		} catch (LineUnavailableException | IllegalArgumentException e) {
			return false;
		}

	}

	/*
	 * if changing volume given control works, then return true, if failing,
	 * return false
	 */
	private boolean testVol(Mixer mixer) { // source
		Line.Info[] lineInfos = mixer.getTargetLineInfo(); // target, not
															// source
		Line line = null;
		boolean opened = true;
		try {
			if (lineInfos.length > 0) {
				line = mixer.getLine(lineInfos[0]);
				opened = line.isOpen() || line instanceof Clip;
				if (!opened) {
					line.open();
				}
				/*
				 * try to get control
				 */
				@SuppressWarnings("unused")
				FloatControl volCtrl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
				return true;
			} else {
				return false;
			}

		} catch (LineUnavailableException e) {
			return false;
		} catch (IllegalArgumentException iaEx) {
			return false;
		} finally {
			if (line != null && !opened) {
				line.close();
			}

		}

	}
}
