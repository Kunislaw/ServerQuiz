package sample;

import java.io.Serializable;

public class Answer implements Serializable {
    private String nick;
    private String answer;

    public Answer(String nick, String answer) {
        this.nick = nick;
        this.answer = answer;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "nick='" + nick + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
