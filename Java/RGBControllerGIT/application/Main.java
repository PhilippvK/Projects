package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	String input = "";

	public boolean select() {
		List<String> choices = new ArrayList<>();
		
		Enumeration<CommPortIdentifier> c = CommPortIdentifier.getPortIdentifiers();
		
		while (c.hasMoreElements()) {
			CommPortIdentifier p = (CommPortIdentifier) c.nextElement();
			choices.add(p.getName());
			
		}

		ChoiceDialog<String> dialog = new ChoiceDialog<>("None", choices);
		dialog.setTitle("Choose Device");
		dialog.setHeaderText("Please select a Serial Port");
		dialog.setContentText("9600bps");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    System.out.println("Your choice: " + result.get());
		    if(!result.get().equals("None")){
		    	connect(result.get());
			    return true;
		    } else {
		    	return false;
			}
		    
		}
		return false;
		

		
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			select();
			primaryStage.setTitle("RGB Controller");
			Scene scene = new Scene(new Panel(), 250, 380);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub

					try {
						out.flush();
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					serialPort.close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public class Panel extends BorderPane {
		

		public Panel() {
			
			TabPane tabPane = new TabPane();
		    BorderPane borderPane = new BorderPane();
		      Tab tablive = new Tab("Live");
		      Tab tabprog = new Tab("Program");
		      
		      tablive.setContent(new LiveMode(out));
		      tablive.setClosable(false);
		      tabPane.getTabs().add(tablive);
		      
		      tabprog.setContent(new ProgramMode(out));
		      tabprog.setClosable(false);
		      tabPane.getTabs().add(tabprog);
		    // bind to take available space
		    borderPane.prefHeightProperty().bind(heightProperty());
		    borderPane.prefWidthProperty().bind(widthProperty());

		    borderPane.setCenter(tabPane);
		    setTop(borderPane);
		    
		    
			
			
			
			
			

			
			
			
			
		
		}
	}

	OutputStream out;
	SerialPort serialPort;

	public void connect(String portName) {
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				int timeout = 2000;
				CommPort commPort = portIdentifier.open(this.getClass().getName(), timeout);

				if (commPort instanceof SerialPort) {
					serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);

					// InputStream in = serialPort.getInputStream();
					out = serialPort.getOutputStream();

					// t1 = new Thread(new SerialReader(in));
					// t1.start();

				} else {
					System.out.println("Error: Only serial ports are handled by this example.");
				}
			}
		} catch (NoSuchPortException | UnsupportedCommOperationException | IOException | PortInUseException e) {
			
	
		
			    Alert alert = new Alert(AlertType.ERROR);
			    alert.setTitle("Error");
			    String s = "Could not open Port! Not connected or already in use?";
			    alert.setContentText(s);
			    alert.showAndWait();
			    
			    System.exit(0);
			}

			
			
			
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
