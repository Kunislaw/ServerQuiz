package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;


public class Controller {
    @FXML
    private TextArea messages;
    private BlockingQueue<Answer> blockingQueue = new LinkedBlockingQueue<Answer>();
    private Answer answer;
    private List<Questions> questions = new ArrayList<>();
    private Questions question;
    private boolean correct = true;
    @FXML
    void initialize()
    {
        new Thread(() -> {
            loadQuestions();//wczytywanie pytań
            Iterator<Questions> it = questions.iterator();
            while(true){ //nieskonczona petla
                try {
                    if(correct)//jesli poprawna odpowiedz
                    {
                        if(it.hasNext()){//sprawdzamy czy w mapie mamy jeszcze pytania, jak tak to
                            question = it.next();
                            messages.appendText(question.getQuestion() + "\n");//wyswietlamy pytanie
                        }
                        else{
                            messages.appendText("Koniec \n");
                        }
                    }
                    else messages.appendText(question.getQuestion() + "\n");//jesli odpowiedz bledna, powielamy pytanie
                    answer = blockingQueue.take();
                    if(answer.getAnswer().toLowerCase().equals(question.getCorrectAnswer().toLowerCase())){//sprawdzamy czy podana odpowiedz jest prawidlowa
                        correct = true;//jak tak to ustawiamy flage
                        blockingQueue.clear();//czyszczczenie kolejki w przypadku prawidlowej odpowiedzi
                    }
                    else correct = false;
                }
                catch (Exception e){
                    messages.appendText(e + "\n");
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
    private void loadQuestions(){
        try (Stream<String> linesStream = Files.lines(new File("questions.txt").toPath())) {//tworzymy strumien aby odczytac plik
            linesStream
                    .map(line -> line.split(","))//mapujemy kazda linie na tablica dwuelementowa
                    .forEach(line -> {
                        questions.add(new Questions(line[0],line[1]));//umieszczamy kazde pytanie w mapie
            });
        }
        catch (Exception e){

        }

    }
}
