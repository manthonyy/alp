import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Menu {

    Scanner x = new Scanner(System.in);

    HashMap<String, String> accountData = new HashMap<>();

    ArrayList<User> userList = new ArrayList<>();
    ArrayList<Voucher> voucherList = new ArrayList<>();

    User currentUser;

    TransactionManager transactionManager = new TransactionManager();
    WasteQueueManager queueManager = new WasteQueueManager();
    ReportGenerator reportGenerator = new ReportGenerator(transactionManager, userList);

    User admin = new User("ADM001","admin","admin",UserRole.ADMIN);
    User user1 = new User("USR001","user","user",UserRole.USER);

    public void menuAwal() {

        userList.add(admin);
        userList.add(user1);

        accountData.put("admin", "admin");
        accountData.put("user", "user");

        while(true) {

            System.out.println("====================================");
            System.out.println(" SMART WASTE MANAGEMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose Menu : ");

            int menu = x.nextInt();
            x.nextLine();

            switch(menu) {

                case 1:
                    login();
                    break;

                case 2:
                    register();
                    break;

                case 3:
                    System.out.println("Thank you!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid Menu");
            }
        }
    }

    public void login() {

        System.out.println("===== LOGIN =====");

        System.out.print("Username : ");
        String username = x.nextLine();

        System.out.print("Password : ");
        String password = x.nextLine();

        if(accountData.containsKey(username)) {
            if(accountData.get(username).equals(password)) {
                for(User u : userList) {
                    if(u.getUsername().equals(username)) {
                        currentUser = u;
                    }
                }

                System.out.println("Login Success!");
                if(currentUser.getRole() ==  UserRole.ADMIN) {
                    System.out.println("You Are Logged in as Admin");
                    adminMenu();
                }
                else {
                    System.out.println("You Are Logged in as User");
                    userMenu();
                }
            }
            else {
                System.out.println("Wrong Password!");
            }
        }
        else {
            System.out.println("Username Not Found!");
        }
    }

    public void register() {
        System.out.println("===== REGISTER =====");
        System.out.print("Username : ");
        String username = x.nextLine();

        if(accountData.containsKey(username)) {
            System.out.println("Username Already Exists!");
            return;
        }

        System.out.print("Password : ");
        String password = x.nextLine();

        String userId = "USR00" + (userList.size());
        User newUser = new User(userId, username, password, UserRole.USER);

        userList.add(newUser);
        accountData.put(username, password);
        System.out.println("Register Success!");
    }

    public void userMenu() {
        while(true) {
            System.out.println("===== USER MENU =====");
            System.out.println("1. Deposit Waste");
            System.out.println("2. View Points");
            System.out.println("3. Transaction History");
            System.out.println("4. Redeem Voucher");
            System.out.println("5. Show Leaderboard");
            System.out.println("6. Show Sustainability Rank");
            System.out.println("7. Logout");
            System.out.print("Choose Menu: ");
            int menu = x.nextInt();
            x.nextLine();

            switch(menu) {
            case 1:
                depositWaste();
                break;
            case 2:
                System.out.println("Tier   : " + currentUser.getUserTier());
                System.out.println("Points : " + currentUser.getTotalPoints());
                System.out.println("Earned : " + currentUser.getTotalPointsEarned());
                System.out.printf ("Bonus  : %.1fx multiplier%n",currentUser.getUserTier().multiplier);
                break;
            case 3:
                currentUser.viewTransactionHistory();
                break;
            case 4:
                redeemVoucher();
                break;
            case 5:
                showLeaderboard();
                break;
            case 6:
                showSustainabilityScore(currentUser);
                break;
            case 7:
                return;
            default:
                System.out.println("Invalid Menu");
            }   
        }
    }
        public void depositWaste(){

        System.out.println("================================");
        System.out.println(" DEPOSIT WASTE");
        System.out.println("================================");
        System.out.println("1. Plastic Waste");
        System.out.println("2. Metal Waste");
        System.out.println("3. Glass Waste");
        System.out.println("4. Organic Waste");
        System.out.println("5. E-Waste");
        System.out.print("Choose Waste Type: ");
        int choice = x.nextInt();

        System.out.print("Input Weight (kg) : ");
        double weight = x.nextDouble();
        x.nextLine();

        if(weight <= 0){
            System.out.println("Weight must be more than 0!");
            return;
        }

        WasteItem waste = null;

        switch(choice){
            case 1:
                waste = new PlasticWaste(weight);
                break;
            case 2:
                waste =new MetalWaste(weight);
                break;
            case 3:
                waste =new GlassWaste(weight);
                break;
            case 4:
                waste =new OrganicWaste(weight);
                break;
            case 5:
                waste =new EWaste(weight);
                break;
            default:
                System.out.println("Invalid Waste Type");
                return;
        }

        int points = waste.calculatePoints();
        currentUser.addPoints(points);
        int earned = (int)(points * currentUser.getUserTier().multiplier);

        Transaction t =new Transaction("TRX" +(currentUser.transactionHistory.size() + 1),currentUser, waste, weight, points);
            transactionManager.addTransaction(t);
            currentUser.transactionHistory.add(t);
            transactionManager.saveAllTransactionsToFile();
            transactionManager.saveUserTransactionHistory(currentUser);
            System.out.println();
            System.out.println("Waste Successfully Deposited!");
            System.out.println("Tier          : " + currentUser.getUserTier());
            System.out.println("Multiplier    : " + currentUser.getUserTier().multiplier + "x");
            System.out.println("Base Points   : " + points);
            System.out.println("Points Earned : " + earned);
            waste.displayWasteInfo();

            System.out.println("\nAdding to Waste Queue.....");
            queueManager.addWasteToQueue(waste);
            }

    public void adminMenu(){
        while(true){
            System.out.println("===== ADMIN MENU =====");
            System.out.println("1. View All Users");
            System.out.println("2. Process Waste");
            System.out.println("3. Generate Report");
            System.out.println("4. Display Facility Info & Upgrade");
            System.out.println("5. View All Transactions");
            System.out.println("6. Total Waste");
            System.out.println("7. Search Transactions");
            System.out.println("8. Show Leaderboard");
            System.out.println("9. Logout");
            System.out.print("Choose Menu: ");

            int menu = x.nextInt();
            x.nextLine();

            switch(menu){
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    queueManager.displayQueue(); 
                    System.out.print("Do you want to process the queue now? (y/n): ");
                    String confirm = x.next();
                    x.nextLine(); 
                    
                    if (confirm.equalsIgnoreCase("y")) {
                        queueManager.processQueue();
                    } else {
                        System.out.println("Process cancelled.");
                    }
                    break;
                case 3:
                    generateReportMenu();
                    break;
                case 4:
                    while (true) {
                    queueManager.displayCenters();
                    System.out.print("Do you want to upgrade a center's capacity? (y/n): "); 
                    String upgradeConfirm = x.next();
                    x.nextLine();

                    if (!upgradeConfirm.equalsIgnoreCase("y")) {
                        break;
                    }

                    System.out.print("Choose Center to Upgrade : ");
                    int choice = x.nextInt();
                    x.nextLine();
                    System.out.print("Input Additional Capacity (Kg): ");
                    double amount = x.nextDouble();
                    x.nextLine();
                    
                    queueManager.upgradeCenter(choice - 1, amount);
                    break;
                    }
                    break;
                case 5:
                    transactionManager.showAllTransactions();
                    break;
                case 6:
                    transactionManager.systemMonitoring();
                    break;
                case 7:
                    searchTransactionMenu();
                    break;

                case 8:
                    showLeaderboard();
                    break; 
                case 9:
                    return;
                default:
                    System.out.println("Invalid Menu");
                }
            }
        }

    public void searchTransactionMenu() {
        System.out.print("Username (press enter to skip): ");
        String username = x.nextLine();
        if (username.isEmpty()) username = null;

        System.out.print("Waste type (press enter to skip): ");
        String wtype = x.nextLine();
        if (wtype.isEmpty()) wtype = null;

        System.out.print("Min weight (0 = no limit): ");
        double minW = x.nextDouble();
        System.out.print("Max weight (0 = no limit): ");
        double maxWInput = x.nextDouble(); 
        x.nextLine();
        double maxW;
            if (maxWInput <= 0) {
                maxW = Double.MAX_VALUE;
            } else {
                maxW = maxWInput;
            }

        List<Transaction> results = transactionManager.searchTransactions(username, wtype, minW, maxW, 0, Integer.MAX_VALUE);

        if (results.isEmpty())
            System.out.println("No results found.");
        else
            for (Transaction t : results) {
                t.displayTransaction();
            }
    }

    public void showSustainabilityScore(User user) {
    int score = user.getSustainabilityScore();
    System.out.println("===== SUSTAINABILITY SCORE =====");
    System.out.println("Username  : " + user.getUsername());
    System.out.println("Score     : " + score);
    System.out.println("Rank      : " + user.getSustainabilityRank());
    System.out.println("--------------------------------");
    System.out.printf ("Kg Deposited    : %.1f kg%n", user.getTotalKgDeposited());
    System.out.println("Transactions    : " + user.transactionHistory.size());
    System.out.println("================================");
    }

    public void showLeaderboard() {
    System.out.println("Sort by: ");
    System.out.println("1. Points");
    System.out.println("2. Total Waste (Kg) Deposited");
    System.out.println("3. Number of Transactions");
    System.out.println("4. Sustainability Score");
    System.out.print("Choose sorting criteria: ");
    int rank = x.nextInt(); 
    x.nextLine();

    ArrayList<User> ranked = new ArrayList<>(userList);

    switch (rank) {
        case 1:
            ranked.sort((a,b) -> b.getTotalPoints() - a.getTotalPoints());
            break;
        case 2:
            ranked.sort((a,b) -> Double.compare(b.getTotalKgDeposited(),a.getTotalKgDeposited()));
            break;
        case 3:
            ranked.sort((a,b) -> b.transactionHistory.size()- a.transactionHistory.size());
            break;
        case 4:
            ranked.sort((a, b) -> b.getSustainabilityScore() - a.getSustainabilityScore());
            break;
    }

    System.out.println("=== LEADERBOARD ===");
    for (int i = 0; i < ranked.size(); i++) {
        User u = ranked.get(i);
        System.out.printf("%2d. %-15s | %5d pts | %.1f kg | %d deposits | Score: %d (%s)%n",i + 1, u.getUsername(), u.getTotalPoints(),u.getTotalKgDeposited(), u.transactionHistory.size(),u.getSustainabilityScore(), u.getSustainabilityRank());
        }
    }

    public void generateReportMenu() {

        while (true) {

            System.out.println("=== GENERATE REPORT ===");
            System.out.println("1. Transaction Report");
            System.out.println("2. Waste Report");
            System.out.println("3. User Report");
            System.out.println("4. Back to Admin Menu");
            System.out.print("Choose Report Type: ");

            int choice = x.nextInt();
            x.nextLine();

            if (choice == 4) break;

            switch (choice) {
                case 1:
                    reportGenerator.generateTransactionReport();
                    askExportPdf("Transaction", choice);
                    break;
                case 2:
                    reportGenerator.generateWasteReport();
                    askExportPdf("Waste", choice);
                    break;
                case 3:
                    reportGenerator.generateUserReport();
                    askExportPdf("User", choice);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void askExportPdf(String reportName, int type) {
        System.out.print("\nExport " + reportName + " Report as PDF? (y/n): ");
        String ans = x.next();
        x.nextLine();
        if (ans.equalsIgnoreCase("y")) {
            switch (type) {
                case 1:    
                    reportGenerator.exportTransactionReportToPdf(); 
                    break;
                case 2: 
                    reportGenerator.exportWasteReportToPdf();      
                    break;
                case 3: 
                    reportGenerator.exportUserReportToPdf();        
                    break;
            }
        }
    }

    public void redeemVoucher() {
        System.out.println("=== REDEEM VOUCHER ===");
        System.out.println("Your Points     : " + currentUser.getTotalPoints());
        System.out.println("Conversion Rate : 1 pt = Rp " + Voucher.RUPIAH_PER_POINT);
        System.out.println("Minimum Redeem  : 100 pts");

        if (currentUser.getTotalPoints() < Voucher.getMinimumPoints()) {
            System.out.println("You don't have enough points to redeem.");
            System.out.println("Minimum required: " + Voucher.getMinimumPoints() + " points.");
            return;
        }

        System.out.print("Enter points to redeem (multiples of 100): ");
        int points = x.nextInt();
        x.nextLine();

        Voucher voucher = Voucher.redeem(currentUser, points);

        if (voucher != null) {
            voucherList.add(voucher);
            System.out.println("\nVoucher successfully created!");
            voucher.displayVoucher();
            System.out.println("Remaining Points: " + currentUser.getTotalPoints());
            System.out.println("\nExporting voucher to PDF...");
            voucher.exportToPdf();
        }
    }

        public void viewAllUsers(){
        System.out.println("===== USER LIST =====");
        if(userList.isEmpty()){
            System.out.println("No User Data");
            return;
        }

        for(User u : userList){
            System.out.println("User ID : " + u.getUserId());
            System.out.println("Username : " + u.getUsername());
            System.out.println("Role : " + u.getRole());
            System.out.println("Total Points : " + u.getTotalPoints());
            System.out.println("=====================");
        }
    }
}
