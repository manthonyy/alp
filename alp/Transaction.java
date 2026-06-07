public class Transaction {
    String transactionId;
    User user;
    WasteItem wasteItem;
    double weight;
    int pointsEarned;

    public Transaction(String transactionId,User user,WasteItem wasteItem,double weight,int pointsEarned){
        this.transactionId = transactionId;
        this.user = user;
        this.wasteItem = wasteItem;
        this.weight = weight;
        this.pointsEarned = pointsEarned;
    }

    public void displayTransaction(){
        System.out.println("Transaction ID : "+ transactionId);
        System.out.println("Username : " + user.getUsername());
        System.out.println("Waste Type : "+ wasteItem.wasteName);
        System.out.println("Weight : "+ weight + " Kg");
        System.out.println("Points : "+ pointsEarned);
        System.out.println("======================");
    }
}
