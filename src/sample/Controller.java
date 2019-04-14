package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Controller {
    @FXML
    private TextArea messages;
    private BlockingQueue<Answer> blockingQueue = new LinkedBlockingQueue<Answer>();
    private BufferedReader br;
    private Questions question;
    private Answer answer;
    @FXML
    void initialize()
    {
        new Thread(() -> {
            String line;
            boolean correct = true; //zakladamy z gory ze pierwsza odpowiedz jest poprawna aby wyswietlic pierwsze pytanie
            try{
                br = new BufferedReader(new FileReader("questions.txt")); //wczytujemy plik z pytaniami
            }
            catch(Exception e){
                System.out.println(e);
                br = null; //jak sie nie uda ustawiamy br na null
            }

            while(true){ //nieskonczona petla
                try {
                    if (answer != null) { //jezeli przy pierwszym przejsciu petli answer jest null to pomijamy
                        //sprawdzanie odpowiedzi
                        if (answer.getAnswer().equals(question.getCorrectAnswer())) {
                            correct = true;//jezeli odpowiedz na pytanie jest poprawna ustawiamy flage correct
                        }
                        else correct = false;
                    }
                    if(correct){//jezeli odpowiedz byla poprawna
                        if ((line = br.readLine()) != null) { //wczytujemy kolejne pytanie
                            question = new Questions();
                            question.setQuestion(line);
                            line = br.readLine();
                            question.setCorrectAnswer(line);
                            messages.setText(messages.getText() + question.getQuestion() + "\n");
                            //wypisujemy kolejne pytanie
                        } else {//jezeli koniec pliku, wypisz Koniec quizu
                            messages.setText(messages.getText() + "Koniec quizu" + "\n");
                        }
                    }
                    else{//ponawiamy to samo pytanie w przypadku blednej odpowiedzi
                        messages.setText(messages.getText() + question.getQuestion() + "\n");
                    }
                    answer = blockingQueue.take();//pobranie z kolejki blokujacej elementu
                }
                catch(Exception e) {
                    System.out.println(e);
                }
            }

        }).start();//wystartowanie pierwszego watku
        new Thread(() -> {
            String line;
            try {
                ServerSocket server_socket = new ServerSocket(6000);//tworzymy server socket
                while (true) {
                    Socket socket = server_socket.accept(); //nowy socket
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream()); //nowy inputstream
                    Answer answer = (Answer) objectInputStream.readObject();//czytamy obiekt
                    blockingQueue.put(answer); //dodajemy obiekt do kolejki, gdy przyjdzie element do kolejki drugi
                    //watek go pobierze
                    objectInputStream.close();
                    socket.close();//zamykamy polaczenie
                }
            }
            catch(Exception e){
                System.out.println("Blad:\n"+e.getMessage());
            }
        }).start();//wystartowanie drugiego watku
    }
}
