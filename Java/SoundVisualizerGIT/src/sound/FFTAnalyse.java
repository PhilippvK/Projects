/*
* created by Philipp van Kempen (phvankempen@googlemail.com)
* last change 21.02.2016
* 
* Library used:
* 
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors
* 
*/

package sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.util.PitchConverter;
import be.tarsos.dsp.util.fft.FFT;

/*
 * This Part of the program is taking care of all recording and analysing
 */
public class FFTAnalyse {

	public AudioDispatcher dispatcher;
	private final int SIZE = 32; // the number of fft bins
	private float sampleRate = 22050; // Recording 22kHz for a 11kHz analyse
	private int bufferSize = 256; // recording buffer for minimal latency
	private int overlap = 192; // overlap factor for best results
	public float[] data = new float[SIZE]; // output of the analyse, will be
											// used by the main program

	/*
	 * returns frequency in in a number between 0 and size-1 needs the size and
	 * the frequency as parameters
	 * 
	 */
	private int frequencyToBin(int size, final double frequency) {
		final double minFrequency = 50; // Hz
		final double maxFrequency = 11000 * 2; // Hz
		int bin = 0;
		final boolean logaritmic = false; // activate the logarithmic scale of
											// the frequency axis
		/*
		 */
		if (frequency != 0 && frequency > minFrequency && frequency < maxFrequency) {
			double binEstimate = 0;
			if (logaritmic) { // unfortuntaley don't works at the moment...
				final double minCent = PitchConverter.hertzToAbsoluteCent(minFrequency * 2);
				final double maxCent = PitchConverter.hertzToAbsoluteCent(maxFrequency * 2);
				final double absCent = PitchConverter.hertzToAbsoluteCent(frequency / 2);
				binEstimate = (absCent - minCent) / maxCent * size;
			} else {
				/*
				 * makes a value between 0 and 31
				 */
				binEstimate = (frequency - minFrequency) / maxFrequency * size;
			}
			bin = (int) binEstimate; // catsing to int
		}
		return bin;
	}

	/*
	 * processes the output of the analyse to create an array with everything
	 * the main programm needs
	 */
	public float[] getArray(int size, float[] amplitudes) {
		double maxAmplitude = 0; // for dynamic volume, not used at the moment

		float[] pixeledAmplitudes = new float[size]; // for every bar calculate
														// an amplitude
		// iterate over the lage arrray and map to bars
		maxAmplitude = 100; // static volume
		for (int i = amplitudes.length / 800; i < amplitudes.length; i++) {
			int bar = frequencyToBin(32, i * (44100) / (amplitudes.length * 2));
			pixeledAmplitudes[bar] += amplitudes[i];
			pixeledAmplitudes[bar] = (float) (Math.log1p(pixeledAmplitudes[bar] / maxAmplitude) / Math.log1p(1.0000001)
					* 255); // bring the values to usable sizes

		}
		/*
		 * Against failing detection the the first and the last bar, not very
		 * nice....
		 */
		pixeledAmplitudes[size - 1] = (float) (pixeledAmplitudes[size - 2] * 0.8);
		pixeledAmplitudes[0] = (float) (pixeledAmplitudes[1] * 0.8);

		return pixeledAmplitudes;
	}

	/*
	 * contructor The used mixer is the recording device
	 */
	public FFTAnalyse(Mixer mix) {
		try {
			setNewMixer(mix);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * starts recording and analyzing with a new mixer
	 */
	private void setNewMixer(Mixer mixer) throws LineUnavailableException, UnsupportedAudioFileException {
		if (dispatcher != null) {
			dispatcher.stop(); // stop if already running
			System.out.println("na");
		}
		/*
		 * used recording format
		 */
		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);

		/*
		 * create line
		 */
		final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line;
		line = (TargetDataLine) mixer.getLine(dataLineInfo);
		final int numberOfSamples = bufferSize;

		/*
		 * open and start listening to line
		 */
		line.open(format, numberOfSamples);
		line.start();

		/*
		 * create and start analysis
		 */
		final AudioInputStream stream = new AudioInputStream(line);
		JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
		dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);
		dispatcher.addAudioProcessor(fftProcessor);

		/*
		 * run the dispatcher (on a new thread).
		 */
		new Thread(dispatcher, "Audio dispatching").start();
	}

	/*
	 * fft analysis
	 */
	AudioProcessor fftProcessor = new AudioProcessor() {
		FFT fft = new FFT(bufferSize); // create fft object
		float[] amplitudes = new float[bufferSize / 2]; // array for processing

		@Override
		public void processingFinished() {
			// TODO Auto-generated method stub
		}

		/*
		 * runs periodically
		 */
		@Override
		public boolean process(AudioEvent audioEvent) {
			/*
			 * manage buffers
			 */
			float[] audioFloatBuffer = audioEvent.getFloatBuffer();
			float[] transformbuffer = new float[bufferSize * 2];
			System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length);
			
			/*
			 * do transformation (the most important part)
			 */
			fft.forwardTransform(transformbuffer);
			fft.modulus(transformbuffer, amplitudes);
			
			/*
			 * refresh output array
			 */
			data = getArray(SIZE, amplitudes);
			return true;
		}
	};

}
