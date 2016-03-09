/*
* created by Philipp van Kempen (phvankempen@googlemail.com)
* last change 21.02.2016
* 
*/

package gui.translation;

import javafx.animation.Transition;
import javafx.scene.shape.Box;
import javafx.util.Duration;

/*
 * Animation for the Height of the Bars
 */
public class ResizeHeightTranslation extends Transition {

	protected Box box;
	protected double startHeight;
	protected double newHeight;
	protected double heightDiff;

	/*
	 * constructor
	 */
	public ResizeHeightTranslation(Duration duration, Box box, double newHeight) {
		setCycleDuration(duration); // timing for the animation thread
		this.box = box; // element to be animated
		this.newHeight = newHeight; // new value
		this.startHeight = box.getHeight(); // current value
		this.heightDiff = newHeight - startHeight; // new value - old value
	}
	
	/*
	 * Will be run regularly by JavaFX
	 */
	@Override
	protected void interpolate(double fraction) {
		box.setHeight(startHeight + (heightDiff * fraction)); // do the transition
															  // step by step

	}
}