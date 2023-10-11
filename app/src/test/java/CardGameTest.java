import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CardGameTest {
    class RoundData {
        int roundNumber;
        String playerSelections;
        int[] playerScores = new int[4];

        @Override
        public String toString() {
            return "Round " + roundNumber + ". Player Selections: "  + playerSelections + ". Scores: " +
                    playerScores[0] + "," + playerScores[1] + "," + playerScores[2] + "," + playerScores[3];
        }
    }

    class CardPair {
        int rankValue;
        String suitString;

        public CardPair(int rankValue, String suitString) {
            this.rankValue = rankValue;
            this.suitString = suitString;
        }
    }


    private RoundData convertFromLogLine(String logLine) {
        if (!logLine.startsWith("Round")) {
            return null;
        }

        String[]scoreSplit = logLine.split("Score:");
        String roundMovementPart = scoreSplit[0];
        String scoreDataPart = scoreSplit[1];

        String[]roundSplit = roundMovementPart.split(":");
        String roundDataPart = roundSplit[0];
        String movementPart = roundSplit[1];
        movementPart = movementPart.substring(0, movementPart.length() - 1);
        int roundNumber = Integer.parseInt(roundDataPart.replaceAll("Round", ""));

        String[] playerScore = scoreDataPart.split(",");
        RoundData roundData = new RoundData();
        roundData.roundNumber = roundNumber;
        roundData.playerSelections = movementPart;
        for (int i = 0; i < roundData.playerScores.length; i++) {
            roundData.playerScores[i] = Integer.parseInt(playerScore[i]);
        }

        return roundData;
    }

    private int[] convertEndGameFromLogLine(String logLine) {
        if (!logLine.startsWith("EndGame:")) {
            return null;
        }

        String [] endGameScoreStrings = logLine.split("EndGame:");
        String endGameScoreString = endGameScoreStrings[1];
        String[] scoreStrings = endGameScoreString.split(",");
        int[] scores = new int[4];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = Integer.parseInt(scoreStrings[i]);
        }

        return scores;
    }


    @Test(timeout = 45000)
    public void testCardGameOriginal() {
        String testOriginal = "properties/test1.properties";
        final Properties properties = PropertiesLoader.loadPropertiesFile(testOriginal);
        String logResult = new CountingUpGame(properties).runApp();
        Scanner scanner = new Scanner(logResult);
        assertTrue(scanner.hasNextLine());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            RoundData roundData = convertFromLogLine(line);
            int[]endGameScores = convertEndGameFromLogLine(line);
            if (roundData == null) {
                continue;
            }

            if (roundData.roundNumber == 1) {
                int [] scores = roundData.playerScores;
                assertArrayEquals(scores, new int[] { 0, 0, 0, 40 });
            }

            if (roundData.roundNumber == 2) {
                int [] scores = roundData.playerScores;
                assertArrayEquals(scores, new int[] { 0, 0, 8, 40 });
            }

            if (endGameScores != null) {
                assertArrayEquals(endGameScores, new int[] { -79, 64, 94, -39 });
            }
        }
    }

    @Test(timeout = 45000)
    public void testCardGameValidCard() {
        String testOriginal = "properties/test2.properties";
        final Properties properties = PropertiesLoader.loadPropertiesFile(testOriginal);
        String logResult = new CountingUpGame(properties).runApp();
        Scanner scanner = new Scanner(logResult);

        assertTrue(scanner.hasNextLine());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            RoundData roundData = convertFromLogLine(line);

            if (roundData == null) {
                continue;
            }

            if (roundData.roundNumber == 2) {
                String expectedInitialSelections = "P0-11C,P1-11S,P2-SKIP,P3-SKIP,P0-12S,P1-12C,P2-SKIP,P3-SKIP,P0-13C,P1-13S";
                assertTrue("The initial movement has to go according to the expected value", roundData.playerSelections.contains(expectedInitialSelections));
            }

            String [] playerSelections = roundData.playerSelections.split(",");
            List<CardPair> cardSelections = new ArrayList<>();
            for (String playerSelection : playerSelections) {
                String [] selections =  playerSelection.split("-");
                String cardString = selections[1];
                if (cardString.equals("SKIP")) {
                    continue;
                }

                String rankString = cardString.substring(0, cardString.length() - 1);
                String suitString = cardString.substring(cardString.length() - 1);
                Integer rankValue = Integer.parseInt(rankString);
                cardSelections.add(new CardPair(rankValue, suitString));
            }

            for (int i = 0; i < cardSelections.size() - 1; i++) {
                CardPair currentPair = cardSelections.get(i);
                CardPair nextPair = cardSelections.get(i + 1);
                boolean isSameRank = currentPair.rankValue == nextPair.rankValue;
                boolean isSameSuit = currentPair.suitString.equals(nextPair.suitString);
                String message = "The 2 consecutive cards need to have the same rank or the same suit: (" +
                        currentPair.rankValue + currentPair.suitString + "-" + nextPair.rankValue + nextPair.suitString + ")";
                assertTrue(message, isSameRank || isSameSuit);
            }
        }
    }

}
