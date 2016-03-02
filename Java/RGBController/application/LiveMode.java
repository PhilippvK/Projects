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

public class LiveMode extends BorderPane {
	String input = "";
	OutputStream out;
    private Label label3 = new Label("Colors:");
    private CheckBox checkbox = new CheckBox("Fade");
    private Slider slider = new Slider(1, 9, 1);
    private VBox vbox = new VBox(2);
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

    public LiveMode(OutputStream o)
    {
    	this.out = o;
        this.setCenter(vbox);
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
        vbox.getChildren().add(checkbox);
        vbox.getChildren().add(slider);
        vbox.getChildren().add(label3);
        button1.setMinWidth(250);
        button1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('n', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        vbox.getChildren().add(button1);
       
        button2.setMinWidth(124);
        button2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('r', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        button3.setMinWidth(124);
        button3.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('o', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        hbox1.getChildren().addAll(button2,button3);
        button4.setMinWidth(124);
        button4.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('g', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        button5.setMinWidth(124);
        button5.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('t', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        hbox2.getChildren().addAll(button4,button5);
        button6.setMinWidth(124);
        button6.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('b', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        button7.setMinWidth(124);
        button7.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('l', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        hbox3.getChildren().addAll(button6,button7);
        vbox.getChildren().addAll(hbox1,hbox2,hbox3);
        button8.setMinWidth(250);
        button8.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					output('w', !checkbox.isSelected() ? 0 : (int)slider.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
