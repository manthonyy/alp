import java.util.*;

public class Menu {

    Scanner x = new Scanner(System.in);

    HashMap<String, String> accountData =
            new HashMap<>();

    ArrayList<User> userList =
            new ArrayList<>();

    User currentUser;

    TransactionManager transactionManager = new TransactionManager();

    User admin =
            new User(
                    "ADM001",
                    "admin",
                    "admin",
                    UserRole.ADMIN
            );
    User user1 =
            new User(
                    "USR001",
                    "user",
                    "user",
                    UserRole.USER
            );

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

                if(currentUser.getRole() ==
                        UserRole.ADMIN) {
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

        String userId =
                "USR00" + (userList.size());

        User newUser =
                new User(
                        userId,
                        username,
                        password,
                        UserRole.USER
                );

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
            System.out.println("5. Logout");
            System.out.print("Choose Menu: ");

            int menu = x.nextInt();
            x.nextLine();

            switch(menu) {

                case 1:
                depositWaste();
                break;

            case 2:
                System.out.println("Total Points : "+ currentUser.getTotalPoints());
                break;
            case 3:
                currentUser.viewTransactionHistory();
                break;
            case 4:
                System.out.println("fitur belum tersedia");
                case 5:
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

        System.out.print("Choose Waste : ");

        int choice = x.nextInt();

        System.out.print("Input Weight (kg) : ");

        double weight = x.nextDouble();
        x.nextLine();

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

        int points =waste.calculatePoints();
        currentUser.addPoints(points);
        Transaction t =new Transaction("TRX" +(currentUser.transactionHistory.size() + 1),currentUser,waste,weight,points);
            transactionManager.addTransaction(t);
            currentUser.transactionHistory.add(t);
            System.out.println("Waste Successfully Deposited!");
            System.out.println("Points Earned : "+ points);
            waste.displayWasteInfo();
            }

    public void adminMenu(){

    while(true){

        System.out.println("===== ADMIN MENU =====");
        System.out.println("1. View All Users");
        System.out.println("2. Process Waste");
        System.out.println("3. Generate Report");
        System.out.println("4. Upgrade Facility");
        System.out.println("5. View All Transactions");
        System.out.println("6. Total Waste");
        System.out.println("7. Logout");
        System.out.print("Choose Menu: ");

        int menu = x.nextInt();
        x.nextLine();

        switch(menu){

            case 1:
                viewAllUsers();
                break;

            case 2:
                System.out.println("Process Waste");
                break;

            case 3:
                System.out.println("Generate Report");
                break;

            case 4:
                System.out.println("Upgrade Facility");
                break;

            case 5:
                transactionManager.showAllTransactions();
                break;

            case 6:
                transactionManager.systemMonitoring();
                break;

            case 7:
                return;

            default:
                System.out.println("Invalid Menu");
            }
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
