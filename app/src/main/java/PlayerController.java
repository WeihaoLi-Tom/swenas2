import ch.aplu.jgamegrid.GGKeyListener;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PlayerController implements GGKeyListener {

    public final List<List<String>> playerAutoMovements = new ArrayList<>();
    private final Properties properties;
    //    private CountingUpGame game;
//    public boolean isWaitingForPass = true;
    public boolean passSelected = false;

    public PlayerController(CountingUpGame game, Properties properties) {
        this.properties = properties;
        game.addKeyListener(this);
    }

    public boolean keyPressed(KeyEvent keyEvent) {
        System.out.println("yes i press!");
        System.out.println(CountingUpGame.Instance().isWaitingForPass);
        if (CountingUpGame.Instance().isWaitingForPass && keyEvent.getKeyChar() == '\n') {
            CountingUpGame.Instance().passSelected = true;
            System.out.println("yes i press!");
        }
        return false;
    }

    public boolean keyReleased(KeyEvent keyEvent) {
        return false;
    }

    public List<String> getPlayerMovement(int playerIndex) {
        return playerAutoMovements.get(playerIndex);
    }

    public void setupPlayerAutoMovements() {
        String player0AutoMovement = properties.getProperty("players.0.cardsPlayed");
        String player1AutoMovement = properties.getProperty("players.1.cardsPlayed");
        String player2AutoMovement = properties.getProperty("players.2.cardsPlayed");
        String player3AutoMovement = properties.getProperty("players.3.cardsPlayed");

        String[] playerMovements = new String[]{"", "", "", ""};
        if (player0AutoMovement != null) {
            playerMovements[0] = player0AutoMovement;
        }

        if (player1AutoMovement != null) {
            playerMovements[1] = player1AutoMovement;
        }

        if (player2AutoMovement != null) {
            playerMovements[2] = player2AutoMovement;
        }

        if (player3AutoMovement != null) {
            playerMovements[3] = player3AutoMovement;
        }

        for (int i = 0; i < playerMovements.length; i++) {
            String movementString = playerMovements[i];
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAutoMovements.add(movements);
        }
    }

}
