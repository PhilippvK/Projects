package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProgramMode extends BorderPane {
	String input = "";
	OutputStream out;
	private Label label2 = new Label("");
	private Label label3 = new Label("Colors:");
	private CheckBox checkbox = new CheckBox("Time");
	private CheckBox checkbox2 = new CheckBox("Endless");
	private Slider slider = new Slider(1, 9, 1);
	private VBox vbox = new VBox(2);
	private HBox hbox0 = new HBox(2);
	private HBox hbox1 = new HBox(2);
	private HBox hbox2 = new HBox(2);
	private HBox hbox3 = new HBox(2);
	private Button button1 = new Button("OFF");
	private Button button2 = new Button("RED");
	private Button button3 = new Button("ORANGE");
	private Button button4 = new Button("GREEN");
	private Button button5 = new Button("CYAN");
	private Button button6 = new Button("BLUE");
	private Button button7 = new Button("LILA");
	private Button button8 = new Button("WHITE");
	private Button button99 = new Button("Play");
	private Button button9 = new Button("Clear");
	private Button button10 = new Button("Del");
	private Button button11 = new Button("Load");
	private Button button12 = new Button("PAUSE");
	private TextArea text = new TextArea();

	public ProgramMode(OutputStream o) {
		this.out = o;

		setCenter(vbox);
		slider.setMinorTickCount(1);
		slider.setMajorTickUnit(2);
		slider.snapToTicksProperty().set(true);
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

			}
		});
		checkbox.setSelected(true);
		checkbox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				slider.setDisable(!checkbox.isSelected());
			}
		});
		checkbox2.setSelected(true);
		vbox.getChildren().add(checkbox2);
		text.setEditable(false);
		text.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				if (text.getText().equals("")) {
					button11.setText("Load");
				} else {
					button11.setText("Save");
				}
			}
		});
		vbox.getChildren().add(text);
		button9.setMinWidth(61);
		button9.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				text.setText("");
			}
		});
		button10.setMinWidth(61);
		button10.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (text.getText().length() > 1) {
					text.setText(text.getText().substring(0, text.getText().length() - 2));
				}
			}
		});
		button11.setMinWidth(61);
		button11.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				if (text.getText().equals("")) {

					ArrayList<String> alist = new ArrayList<String>();

					File folder = new File("save/");
					File[] listOfFiles = folder.listFiles();

					for (File file : listOfFiles) {
						if (file.isFile()) {
							alist.add(file.getName());
						}
					}

					List<String> dialogData;

					dialogData = alist;

					ChoiceDialog<String> dialog = new ChoiceDialog<String>(dialogData.get(0), dialogData);
					dialog.setTitle("Load Program");
					dialog.setHeaderText("Select Name");

					Optional<String> result = dialog.showAndWait();
					String selected = "none";

					if (result.isPresent()) {
						selected = result.get();
					}

					if (!selected.equals("none")) {
						try {
							FileReader reader = new FileReader(new File("./save/" + selected));
							for (int c; (c = reader.read()) != -1;) {

								System.out.print((char) c);
								text.setText(text.getText() + (char) c);
							}
							reader.close();

						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Save");
					dialog.setHeaderText("Program Name?");

					Optional<String> result = dialog.showAndWait();
					String entered = "none";

					if (result.isPresent()) {

						entered = result.get();
					}

					if (!entered.equals("none")) {
						Writer fw = null;

						try {
							fw = new FileWriter("./save/" + entered);
							fw.write(text.getText());
						} catch (IOException e) {
							System.err.println("Konnte Datei nicht erstellen");
						} finally {
							if (fw != null)
								try {
									fw.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
					}

				}

			}
		});
		button99.setMinWidth(61);
		button99.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					input = text.getText();
					if (!checkbox2.isSelected()) {
						input = input + "p0";
					}
					outputString(input);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		hbox0.getChildren().addAll(button99, button9, button10, button11);
		vbox.getChildren().add(hbox0);
		vbox.getChildren().add(label2);
		vbox.getChildren().add(checkbox);
		vbox.getChildren().add(slider);
		vbox.getChildren().add(label3);
		button12.setMinWidth(250);
		button12.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("p");
				text.appendText(String.valueOf((int) slider.getValue()));
			}
		});
		vbox.getChildren().add(button12);
		button1.setMinWidth(250);
		button1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("n");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		vbox.getChildren().add(button1);
		text.setWrapText(true);
		button2.setMinWidth(124);
		button2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("r");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		button3.setMinWidth(124);
		button3.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("o");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		hbox1.getChildren().addAll(button2, button3);
		button4.setMinWidth(124);
		button4.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("g");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		button5.setMinWidth(124);
		button5.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("t");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		hbox2.getChildren().addAll(button4, button5);
		button6.setMinWidth(124);
		button6.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("b");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		button7.setMinWidth(124);
		button7.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("l");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		hbox3.getChildren().addAll(button6, button7);
		vbox.getChildren().addAll(hbox1, hbox2, hbox3);
		button8.setMinWidth(250);
		button8.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				text.appendText("w");
				text.appendText(!checkbox.isSelected() ? "0" : String.valueOf((int) slider.getValue()));
			}
		});
		vbox.getChildren().add(button8);
		this.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				switch (event.getCode()) {
				case N:
					button1.fire();
					break;
				case R:
					button2.fire();
					break;
				case O:
					button3.fire();
					break;
				case G:
					button4.fire();
					break;
				case C:
					button5.fire();
					break;
				case B:
					button6.fire();
					break;
				case L:
					button7.fire();
					break;
				case W:
					button8.fire();
					break;
				case P:
					button12.fire();
					break;
				case ENTER:
					button99.fire();
					break;
				case ESCAPE:
					try {
						output('n', 0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		});
	}

	public void output(char color, int fade) throws IOException, InterruptedException {

		out.write(color);
		Thread.sleep(10);
		out.write(48 + fade);
		Thread.sleep(10);
		out.write(13);
		Thread.sleep(10);

	}

	public void outputString(String in) throws IOException, InterruptedException {
		for (int i = 0; i < (int) (in.length() / 2); i++) {
			out.write(in.charAt(2 * i));
			Thread.sleep(10);
			out.write(in.charAt(2 * i + 1));
			Thread.sleep(10);
		}
		out.write(13);
		Thread.sleep(10);

	}
}
