package ca.uqac.florentinth.speakerauthentication.Models;

/**
 * Created by FlorentinTh on 11/12/2015.
 */
public class Story {

    private int id;
    private String text;

    public Story(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
