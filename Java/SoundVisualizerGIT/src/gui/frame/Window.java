/*
* created by Philipp van Kempen (phvankempen@googlemail.com)
* last change 21.02.2016
* 
*/

package gui.frame;

import javax.sound.sampled.Mixer;
import javax.swing.JOptionPane;

import gui.translation.ResizeHeightTranslation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sound.AudioDeviceManager;
import sound.FFTAnalyse;

/* 
 * Main Window
 */
public class Window extends Application {

	boolean first = true; // will be true if the program starts
	
	/*
	 * dimensions of the window
	 */
	private int width = 690;
	private int height = 500;

	/*
	 * Parameters
	 */
	private int balken = 32; // Here, the amount of bars has to be defined

	/*
	 * Controls
	 */
	private Button btnInfo; // Info Button
	private Button btnColor; // Random Color Button
	private Button btnFullscreen; // Fullscreen Button
	private Slider sliderRecVolume;
	private ComboBox<String> myComboBox;

	/*
	 * Stages, Scenes, Groups,...
	 */
	private Stage mainStage;
	BorderPane borderPane;
	HBox buttonGroup;
	Group root;

	/*
	 * Boxes, Lights,...
	 */
	private Box[] boxes; // Array, where the bars are managed

	/*
	 * Material, Colors
	 */
	private PhongMaterial pm; // Material of the Boxes/Bars
	private Color[] colors = { Color.DARKRED, Color.DARKGREEN, Color.DARKCYAN, Color.DARKBLUE, Color.CHARTREUSE,
			Color.CORNFLOWERBLUE, Color.CRIMSON, Color.CHOCOLATE, Color.DEEPPINK, Color.DODGERBLUE, Color.FUCHSIA,
			Color.GREENYELLOW, Color.HOTPINK };
	private int co = 1; // counting through every position of the Array

	/*
	 * Data Processing
	 */
	private float[] data; // current result of fft-analysis

	/*
	 * Threads
	 */
	private Thread updateThread; // updates every bar every x ms
	private FFTAnalyse fftAnalyse; // records audio and writes into a
	private AudioDeviceManager manager;
	// buffer and does analyse with an array
	// as output

	/*
	 * Start of JavaFX-Program
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * Will be called, if JavaFX-Window closes
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#stop()
	 */
	public void stop() throws InterruptedException {
		updateThread.interrupt();

		// stop thread analyse

		System.out.println("Stopped Frame");
	}

	/*
	 * Initializes every JavaFX Control Element
	 */
	private void initControl() {
		/*
		 * Button for the Readme File
		 */
		btnInfo = new Button();
		btnInfo.setText("Info"); // text of the control
		btnInfo.setMinWidth(40); // minimum width of the control
		btnInfo.setTooltip(new Tooltip("Random Color - Shortcut: C")); // Display
																		// Readme
																		// File
		btnInfo.setOnAction(new EventHandler<ActionEvent>() { // PopUp Dialog
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Help"); // title of the popup windows
				alert.setHeaderText("Readme"); // header title
				alert.setContentText(
						"Tasten/Shortcuts:\n\tEnter - Vollbild AN/AUS\n\tC - Farbe wechseln\n\n Allgemeime Infos:\n\t32 Balken\n\t11kHz Frequenzbereich\n\t344Hz pro Balken\n\tMono "); // Readme
																																																																																																																																																															// Text
				alert.showAndWait(); // open popup and wait until closed
			}
		});

		/*
		 * Button for Color changing
		 */
		btnColor = new Button();
		btnColor.setText("Color"); // text of the control
		btnColor.setMinWidth(50); // minimum width of the control
		btnColor.setTooltip(new Tooltip("Random Color - Shortcut: C")); // tooltip
																		// opens
																		// on
																		// mouse
																		// hover
		btnColor.setOnAction(new EventHandler<ActionEvent>() { // next random
																// color on
																// click
			@Override
			public void handle(ActionEvent event) {
				changeColor();
			}
		});

		/*
		 * Button for toggling fullscreen mode
		 */
		btnFullscreen = new Button();
		btnFullscreen.setText("Fullscreen"); // text of the control
		btnFullscreen.setMinWidth(70); // minimum width of the control
		btnFullscreen.setTooltip(new Tooltip("Toggle Fullscreen - Shortcut: Enter/Return")); // tooltip
																								// opens
																								// on
																								// mouse
																								// hover
		btnFullscreen.setOnAction(new EventHandler<ActionEvent>() { // switch
																	// fullscreen
																	// on and
																	// off
			@Override
			public void handle(ActionEvent event) {
				toggleFullscreen();
			}
		});

		/*
		 * horizontal slider for changing the recording volume
		 */
		sliderRecVolume = new Slider();
		sliderRecVolume.setMin(0); // minimum value of the slider
		sliderRecVolume.setMax(100); // maximum value of the slider
		sliderRecVolume.setValue(20); // default value of the slider
		sliderRecVolume.setShowTickLabels(true); // enable labels
		sliderRecVolume.setShowTickMarks(true); // enable bars
		sliderRecVolume.setMajorTickUnit(50); // create big bar in the middle
		sliderRecVolume.setMinorTickCount(2); // create smaller bars
		sliderRecVolume.setBlockIncrement(10); // create blocks
		sliderRecVolume.setPrefWidth(200); // set default width
		sliderRecVolume.setMinWidth(100); // minimum width of the control
		sliderRecVolume.setValueChanging(true); // enable that the value will be
												// updated if slider was moved
		sliderRecVolume
				.setTooltip(new Tooltip("Recording Volume: " + sliderRecVolume.valueProperty().intValue() + "%")); // tooltip
																													// on
																													// mouse
																													// hover
		sliderRecVolume.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) { // will
																													// run,
																													// if
																													// value
																													// changes
				sliderRecVolume.setTooltip(
						new Tooltip("Recording Volume: " + sliderRecVolume.valueProperty().intValue() + "%")); // update
																												// tooltip
																												// percentage
				manager.setAllVol(newValue.floatValue() / 100); // set all
																// available
																// recording
																// devices to
																// new volume
			}
		});

		/*
		 * drop down menu for selecting the soundcard / recording device
		 */
		myComboBox = new ComboBox<String>();
		myComboBox.setMaxWidth(215); // maximal width of the control
		myComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) { // will
																													// run
																													// if
																													// a
																													// new
																													// item
																													// is
																													// selected
				System.out.println("Recording Device: " + newValue);
				for (Mixer m : manager.mixers) { // find the selected device
					if (m.getMixerInfo().getName().equals(myComboBox.getValue())) {
						if(!first){ // killing running audio processor if not the firt time
							fftAnalyse.dispatcher.stop();
						} else {
							first = false;
						}
						fftAnalyse = new FFTAnalyse(m); // restart recording and
														// analyse with new
														// device
						break;
					}
				}
			}
		});
	}

	/*
	 * Initializes the Lights and LightGroups
	 */
	private Group initLight() {
		PointLight pointLight = new PointLight(Color.WHITE); // 2 different
																// light spots
																// for Ambient
																// Lightning
		PointLight pointLight2 = new PointLight(Color.WHITE); // Both with
																// delauft white
																// light color
		/*
		 * setting position and orientation of light spots
		 */
		pointLight.setTranslateX(31);
		pointLight.setTranslateY(40);
		pointLight.setTranslateZ(-400);
		pointLight.setRotate(0);
		pointLight2.setTranslateX(32);
		pointLight2.setTranslateY(-50);
		pointLight2.setTranslateZ(-10);
		pointLight2.setRotate(0);

		/*
		 * Adding Lights to a Group, that will be returned
		 */
		Group lightGroup = new Group();
		lightGroup.getChildren().addAll(pointLight, pointLight2);
		return lightGroup;
	}

	/*
	 * Initializes the Camera
	 */
	private Camera initCam() {
		PerspectiveCamera camera = new PerspectiveCamera(true);

		/*
		 * Camera positioning, orientation and FOV
		 */
		camera.getTransforms().addAll(new Rotate(0, Rotate.Y_AXIS), new Rotate(-20, Rotate.X_AXIS),
				new Translate(31, 10, -90));
		camera.setFieldOfView(30);

		return camera;
	}

	/*
	 * Initialization of the Bar-Array
	 */
	private Group initBoxes() {
		boxes = new Box[balken];
		/*
		 * create Material with colors
		 */
		pm = new PhongMaterial();
		pm.setSpecularColor(Color.ORANGE); // Color of the reflection
		changeColor(); // Set Starting-Color

		/*
		 * definition of every box/bar
		 */
		for (int j = 0; j < balken; j++) {
			boxes[j] = new Box(balken * (width / 13800.0), 10, 1); // Size
			boxes[j].setDrawMode(DrawMode.FILL); // fill instead of drawing only
													// the edges
			boxes[j].setTranslateX(j * (balken / 16));
			boxes[j].setTranslateY(40);
			boxes[j].setCache(true);
			boxes[j].setMaterial(pm);
			boxes[j].setCacheHint(CacheHint.QUALITY);
		}

		/*
		 * adding everything to a Group and return it
		 */
		Group boxGroup = new Group();
		boxGroup.getChildren().addAll(boxes);
		return boxGroup;
	}

	private void initAudioDeviceManager() {
		manager = new AudioDeviceManager();
		for (Mixer m : manager.mixers) {
			myComboBox.getItems().add(m.getMixerInfo().getName());
		}
		myComboBox.getSelectionModel().select(0);
		manager.setAllVol(0.4f);
	}

	private Scene initStage() {
		/*
		 * Getting the Parts from the methods
		 */
		Camera camera = initCam();
		Group light = initLight();
		Group boxGroup = initBoxes();
		Group group = new Group();
		initControl();

		Pane pane = new Pane();
		pane.getChildren().add(group);

		root = new Group();
		root.getChildren().add(camera);
		root.getChildren().add(light);
		root.getChildren().add(boxGroup);

		borderPane = new BorderPane();
		Scene s = new Scene(borderPane, width, height, Color.BLACK);
		s.setOnKeyPressed(new EventHandler<KeyEvent>() { // KeyListener for
															// Scene

			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case ENTER: // Switch Fullscreen if Enter-Key is pressed
					toggleFullscreen();
					break;
				case C: // Change color when Tabulator-Key is pressed
					changeColor();
					break;
				default:
					break;
				}
			}
		});

		SubScene subScene = new SubScene(root, 690, 500, true, SceneAntialiasing.BALANCED);
		subScene.setFill(new Color(0.02, 0.02, 0.02, 1));
		subScene.setEffect(new Glow(500));
		subScene.setCamera(camera);

		subScene.widthProperty().bind(s.widthProperty()); // change size of
															// Subscene if
															// windows was
															// resized
		subScene.heightProperty().bind(s.heightProperty()); // ""

		group.getChildren().add(subScene);

		/*
		 * Arranging Buttons
		 */
		buttonGroup = new HBox(3);
		buttonGroup.setMinHeight(30);
		buttonGroup.setStyle("-fx-background-color: #000000;");
		borderPane.setStyle("background-color:black;");
		buttonGroup.getChildren().addAll(btnInfo, btnColor, btnFullscreen, myComboBox, sliderRecVolume);
		borderPane.setTop(buttonGroup);
		borderPane.setCenter(pane);

		/*
		 * Return Scene
		 */
		return s;
	}



	/*
	 * start threads
	 */
	private void initThreads() {

		updateThread = new Thread(new Runnable() { // thread that updates the
													// boxes
			public void run() {
				while (true) {
					data = fftAnalyse.data; // get new fft data
					refresh(); // setting boxes again
					try {
						Thread.sleep(60); // wait 60 ms
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		});
		updateThread.start();

	}

	@Override
	/*
	 * runs when JavaFX program started
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	public void start(Stage primaryStage) {

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println(t.getName() + ": " + e);
				
				JOptionPane.showMessageDialog(null, "This is an Error caught by JavaFX and will be fixed as soon as possible. Please try again, sorry...", "JavaFX: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				try {
					init();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				mainStage.close();
				System.exit(0);
				
			}
		});
		
		mainStage = primaryStage;
		mainStage.setTitle("Sound Visualizer"); // Set Window Title
		// TODO: FAVICON
		/*
		 * get scene from method and display
		 */
		mainStage.getIcons().add(new Image(Window.class.getResourceAsStream("icon.jpeg")));

		Scene scene = initStage();
		mainStage.setScene(scene);
		mainStage.show();

		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				// stop analyse
				updateThread.interrupt();

				Platform.exit();
				System.exit(0);
			}
		});

		/*
		 * start the program threads
		 */
		initAudioDeviceManager();
		initThreads();
	}

	/*
	 * function that is called periodically
	 */
	private void refresh() {
		/*
		 * Get FFT-Data and "make better numbers"
		 */
		double[] volumes = new double[balken]; //
		for (int i = 0; i < balken; i++) {
			volumes[i] = data[i] / 20;
			// if (volumes[i] < 0.5) { // "kill noise"
			// volumes[i] = 0.0;
			// } else {
			// volumes[i] = (int)volumes[i];
			// }
		}
		/*
		 * For every box, set new Height and start an Animation
		 */
		for (int q = 0; q < balken; q++) {
			ResizeHeightTranslation rh = new ResizeHeightTranslation(Duration.millis(10), boxes[q], volumes[q]); // change
																													// height
			TranslateTransition translateTransition = new TranslateTransition(Duration.millis(10), boxes[q]); // change
																												// position
																												// because
																												// the
																												// box
																												// will
																												// be
																												// moved,
																												// after
																												// the
																												// change
																												// of
																												// the
																												// height
			translateTransition.setToY(20 - volumes[q] / 2.0); // new y
																// position
			translateTransition.setCycleCount(1); // run one time

			ParallelTransition parallelTransition = new ParallelTransition(); // sync
																				// both
																				// animations
			parallelTransition.getChildren().addAll(rh, translateTransition);
			parallelTransition.setCycleCount(1);
			parallelTransition.play(); // play animation

		}

	}

	private void changeColor() {
		System.out.println("Colors Switch");
		int q = (int) (1 + Math.random() * (colors.length - 1));
		co = (int) ((co + q) % colors.length);
		pm.setDiffuseColor(colors[co]);
	}

	private void toggleFullscreen() {
		System.out.println("Fullscreen Switch");
		mainStage.setFullScreen(!mainStage.isFullScreen());
		borderPane.setRight(null);
	}
}
