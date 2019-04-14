package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        new Thread(() -> {
            try {
                ServerSocket server_socket = new ServerSocket(6000);
                Socket socket;
                InputStream inputStream;
                ObjectInputStream objectInputStream;
                while (true) {
                    socket = server_socket.accept();
                    inputStream = socket.getInputStream();
                    objectInputStream = new ObjectInputStream(inputStream);
                    Answer answer = (Answer) objectInputStream.readObject();
                    System.out.println(answer.toString());

                }
            }
            catch(Exception e){
                System.out.println("Blad:\n"+e.getMessage());
            }
        }).start();

    }


    public static void main(String[] args) {
        launch(args);
    }

}
