public class PlayerStrategyFactory {
//    private IPlayerStrategy myStrategy = null;

    static PlayerStrategyFactory instance = null;

    public static PlayerStrategyFactory getInstance() {
        if (instance == null) {
            instance = new PlayerStrategyFactory();
        }
        return instance;
    }

    public IPlayerStrategy getMyStrategy(Player p) {
        if (p.getPlayerType() == Player.PlayerType.RANDOM)
            return new RandomPlayerStrategy();
        else if (p.getPlayerType() == Player.PlayerType.BASIC)
            return new BasicPlayerStrategy();
        else if (p.getPlayerType() == Player.PlayerType.CLEVER)
            return new CleverPlayerStrategy();
        return null;
    }
}
