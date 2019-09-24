package Root;

import java.io.Serializable;

/**
 * A form has a serious of questions and its possible to set the answers to these in the form.
 * */
public class Form implements Serializable {
    Question[] questions;
    String posttarget;

    public Form(Question[] questions, String posttarget){
        this.questions = questions;
        this.posttarget = posttarget;
    }

    public Question[] getQuestions(){
        return questions;
    }

    public String getPostTarget(){
        return posttarget;
    }
}
