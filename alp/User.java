import java.util.ArrayList;
public class User {
    private String userId;
    private String username;
    private String password;
    private UserRole role;
    private int totalPoints;
    ArrayList<Transaction> transactionHistory = new ArrayList<>();
    public User(String userId, String username, String password, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        totalPoints = 0;
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
    public void addPoints(int points) {totalPoints += points;}
    public void reducePoints(int points) {totalPoints -= points;}
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

