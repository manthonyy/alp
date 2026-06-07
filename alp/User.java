import java.util.ArrayList;
public class User {
    private String userId;
    private String username;
    private String password;
    private UserRole role;
    private int totalPoints;
    private int totalPointsEarned;
    private UserTier userTier = UserTier.BRONZE;
    ArrayList<Transaction> transactionHistory = new ArrayList<>();
    
    public User(String userId, String username, String password, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        totalPoints = 0;
    }

    public double getTotalKgDeposited() {
        return transactionHistory.stream().mapToDouble(t -> t.weight).sum();
    }

    public void addPoints(int points) {
        int earned = (int)(points * userTier.multiplier);
        totalPoints     += earned;
        totalPointsEarned += earned;
        updateTier();
    }   

    private void updateTier() {
    UserTier[] levels = UserTier.values();
        for (int i = levels.length - 1; i >= 0; i--) {
            if (totalPointsEarned >= levels[i].threshold) {
                userTier = levels[i];
                return;
            }
        }
    }

    public int getSustainabilityScore() {
    int score = 0;

    score += (int)(getTotalKgDeposited() * 2);

    //bonus 20 kalo e-waste atau organic waste
    for (Transaction t : transactionHistory) {
        if (t.wasteItem instanceof EWaste || t.wasteItem instanceof OrganicWaste) {
            score += 20;
        }
    }

    score = (int)(score * userTier.multiplier);

        return score;
    }

    public String getSustainabilityRank() {
        int score = getSustainabilityScore();
        if (score >= 2000) return "ECO CHAMPION";
        if (score >= 1000) return "GREEN GUARDIAN";
        if (score >= 500)  return "EARTH SAVER";
        if (score >= 200)  return "ECO STARTER";
        return "NEWCOMER";
    }

    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public UserRole getRole() {
        return role;
    }
    public int getTotalPoints(){
        return totalPoints;
    }
    public void reducePoints(int points) {
        totalPoints -= points;
    }
    public int getTotalPointsEarned(){
        return totalPointsEarned;
    }
    public UserTier getUserTier() {
        return userTier;
    }

    public void viewTransactionHistory(){
    if(transactionHistory.isEmpty()){
        System.out.println("No Transaction History");
    }
    else{
        for(Transaction t : transactionHistory){
            t.displayTransaction();
        }
    }
}
}

