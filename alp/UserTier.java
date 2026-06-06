public enum UserTier {
    BRONZE  (    0, 1.0),
    SILVER  (  500, 1.1),
    GOLD    ( 1500, 1.3),
    PLATINUM( 3000, 1.5),
    DIAMOND ( 10000, 2.0);

    public final int    threshold;
    public final double multiplier;

    UserTier(int t, double m) {
        threshold  = t;
        multiplier = m;
    }
}