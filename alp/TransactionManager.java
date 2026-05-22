import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class TransactionManager {
    ArrayList<Transaction> transactionList=new ArrayList<>();

    public void addTransaction(Transaction transaction){
        transactionList.add(transaction);
        System.out.println("Transaction berhasil ditambahkan!");
    }

    public void showAllTransactions(){
        if(transactionList.isEmpty()){
            System.out.println("Belum ada transaksi.");
            return;
        }
        System.out.println("===== LIST TRANSAKSI =====");
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
        System.out.println("Total Sampah : "+ calculateTotalWaste()+ " Kg");
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
        System.out.println("Jumlah Transaksi : "+ transactionList.size());
        System.out.println("Total Sampah : "+ calculateTotalWaste()+ " Kg");
        System.out.println("Total Points : "+ calculateTotalPoints());
    }

    public void saveAllTransactionsToFile(){
        try{
            FileWriter writer=new FileWriter("all_transactions.txt");
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
