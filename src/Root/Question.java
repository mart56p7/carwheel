package Root;

import java.io.Serializable;

/**
 * A Question, is a question and a field where the answer can be set after the question has been answered =)
 * */
public class Question implements Serializable {
    String question;
    String answer;

    public Question(String question){
        this.question = question;
    }

    public String getQuestion(){
        return question;
    }

    public String getAnswer(){
        return answer;
    }

    public void setAnswer(String answer){
        this.answer = answer;
    }
}
