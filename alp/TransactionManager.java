import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TransactionManager {
    ArrayList<Transaction> transactionList=new ArrayList<>();

    public void addTransaction(Transaction transaction){
        transactionList.add(transaction);
        System.out.println("Transaction has been successfully added!");
    }

    public void showAllTransactions(){
        if(transactionList.isEmpty()){
            System.out.println("No transaction data available.");
            return;
        }
        System.out.println("===== TRANSACTION LIST =====");
        for(Transaction transaction: transactionList){
            transaction.displayTransaction();
        }
    }

    public double calculateTotalWaste(){
        double totalWaste= 0;
        for(Transaction transaction: transactionList){
            totalWaste += transaction.weight;
        }
        return totalWaste;
    }

    public void showTotalWaste(){
        System.out.println("Total Waste : "+ calculateTotalWaste()+ " Kg");
    }

    public int calculateTotalPoints(){
        int totalPoints = 0;
        for(Transaction transaction : transactionList){
            totalPoints += transaction.pointsEarned;
        }
        return totalPoints;
    }

    public void systemMonitoring(){
        System.out.println("===== SYSTEM MONITORING =====");
        System.out.println("Total Transactions : "+ transactionList.size());
        System.out.println("Total Waste  : "+ calculateTotalWaste()+ " Kg");
        System.out.println("Total Points : "+ calculateTotalPoints());
    }

    public List<Transaction> searchTransactions(String username, String wasteType, double minWeight, double maxWeight, int minPoints, int maxPoints) {

    return transactionList.stream()
        .filter(t -> username == null || t.user.getUsername().equalsIgnoreCase(username))
        .filter(t -> wasteType == null || t.wasteItem.wasteName.equalsIgnoreCase(wasteType))
        .filter(t -> t.weight >= minWeight && t.weight <= maxWeight)
        .filter(t -> t.pointsEarned >= minPoints && t.pointsEarned <= maxPoints)
        .collect(Collectors.toList());
}

    public void saveAllTransactionsToFile(){
        try{
            FileWriter writer = new FileWriter("all_transactions.txt");
            for(Transaction transaction : transactionList){
                writer.write("Transaction ID : "+ transaction.transactionId + "\n");
                writer.write("Username : "+ transaction.user.getUsername() + "\n");
                writer.write("Waste Type : "+ transaction.wasteItem.wasteName + "\n");
                writer.write("Weight : "+ transaction.weight + " KG\n");
                writer.write("Points : "+ transaction.pointsEarned + "\n");
                writer.write("========================\n");
            }
            writer.close();
            System.out.println("All transaction history saved!");
        }
        catch(IOException e){
            System.out.println("Error saving transaction history!");
        }
    }

    public void saveUserTransactionHistory(User user){
        try{
            FileWriter writer =new FileWriter("transaction_"+ user.getUsername()+ ".txt");
            for(Transaction transaction :user.transactionHistory){
                writer.write("Transaction ID : "+ transaction.transactionId + "\n");
                writer.write("Username : "+ transaction.user.getUsername() + "\n");
                writer.write("Waste Type : "+ transaction.wasteItem.wasteName + "\n");
                writer.write("Weight : "+ transaction.weight + " KG\n");
                writer.write("Points : "+ transaction.pointsEarned + "\n");
                writer.write("========================\n");
            }
            writer.close();
            System.out.println("User transaction history saved!");
        }
        catch(IOException e){
            System.out.println("Error saving user transaction!");
        }
    }
}
