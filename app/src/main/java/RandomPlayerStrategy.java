import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Random;

public class RandomPlayerStrategy implements IPlayerStrategy {

    @Override
    public Card PickCardToPlay(Player p) {
        ArrayList<Card> tempList = new ArrayList<>(p.hand.getCardList());

        while (!tempList.isEmpty()) {
            Random random = new Random();
            int x = random.nextInt(tempList.size());
            Card selectedCard = tempList.get(x);

            if (ValidationFacade.getInstance().isValidCardToPlay(selectedCard)) {
                return selectedCard;
            } else {
                tempList.remove(x);
            }
        }

        return null;
    }
}
