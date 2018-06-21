package sk.upjs.vma.kryciemena.gameLogic;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Game implements Serializable {

    public static final int TEAM_0 = 0;
    public static final int TEAM_1 = 1;
    public static final int PASSERBY = 2;
    public static final int KILLER = 3;

    private Card[] cards = new Card[25];
    private int teamTurn;
    private int team0RemainingCards;
    private int team1RemainingCards;

    public Card[] getCards() {
        return cards;
    }

    public int getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(int teamTurn) {
        this.teamTurn = teamTurn;
    }

    public Card getCard(int i) {
        return cards[i];
    }

    private void generateRandomWords(List<String> words) {
        Collections.shuffle(words);
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(words.get(i));
        }
    }

    private void generateRandomCardTypes() {
        boolean[] numbers = new boolean[25];

        // choose the killer
        int index = (int) (Math.random() * 25);
        cards[index].setType(KILLER);
        numbers[index] = true;

        // choose passerby(s)
        int counter = 0;
        while (counter < 7) {
            index = (int) (Math.random() * 25);
            if (numbers[index]) {
                continue;
            }
            cards[index].setType(PASSERBY);
            numbers[index] = true;
            counter++;
        }

        // choose the TEAM_1
        counter = 0;
        while (counter < 8) {
            index = (int) (Math.random() * 25);
            if (numbers[index]) {
                continue;
            }
            cards[index].setType(TEAM_1);
            numbers[index] = true;
            counter++;
        }

        // choose the TEAM_0
        for (int i = 0; i < 25; i++) {
            if (!numbers[i]) {
                cards[i].setType(TEAM_0);
            }
        }

        team0RemainingCards = 9;
        team1RemainingCards = 8;
    }

    public void generateRandomGamePlan(List<String> words) {
        generateRandomWords(words);
        generateRandomCardTypes();
    }

    public void changeTeamTurn() {
        if (teamTurn == 0) {
            teamTurn = 1;
        } else {
            teamTurn = 0;
        }
    }

    public boolean checkTheCard(int index) {
        return !cards[index].isGuessed();
    }

    public boolean checkTheKiller(int index) {
        return cards[index].getType() == KILLER;
    }

    public boolean checkTheWinnerOfGame() {
        if (team0RemainingCards == 0 || team1RemainingCards == 0) {
            return true;
        }
        return false;
    }

    public boolean cardClick(int index) {
        cards[index].setGuessed(true);
        if (cards[index].getType() == TEAM_0) {
            team0RemainingCards--;
        }
        if (cards[index].getType() == TEAM_1) {
            team1RemainingCards--;
        }
        return cards[index].getType() == teamTurn;
    }

}
