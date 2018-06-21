package sk.upjs.vma.kryciemena.gameLogic;

import java.io.Serializable;

public class Card implements Serializable {

     private String word;
     private boolean guessed;
     private int type;

    public Card(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isGuessed() {
        return guessed;
    }

    public void setGuessed(boolean guessed) {
        this.guessed = guessed;
    }

    public int getType() {
        return type;
    }

    /**
     *
     * @param type team0 = 0, team1 = 1, passerby = 2, killer = 3
     */
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return word;
    }
}
