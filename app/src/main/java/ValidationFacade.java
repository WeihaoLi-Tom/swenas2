import ch.aplu.jcardgame.Card;

public class ValidationFacade {
    static ValidationFacade instance = null;

    public static ValidationFacade getInstance() {
        if (instance == null) {
            instance = new ValidationFacade();
        }
        return instance;
    }

    public boolean isValidCardToPlay(Card cd) {
        return CountingUpGame.Instance().isValidCardToPlay(cd);
    }
}
