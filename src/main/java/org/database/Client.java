package org.database;

import java.util.Date;  // Add this import
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Client extends User    //inheritance
{
    private static final Scanner cin = new Scanner(System.in);
    private String role = "Client";

    private FinancialGoals goals;

    Client(){
        String[] account = get_Account();   //uses the get_Account method from User class
        this.email = account[0];    //email initialization
        this.password = account[1]; //password initialization

        if (!ProgramHelper.VerifyUser(this.email,this.password))    //uses VerifyUser method from inherited class(user)
        {
            System.out.println("Invalid Email or Password.");
            throw new IllegalArgumentException("Invalid credentials");  //to stop construction of an object if invalid credentials are given
        }
        else {  //if user verified then approves constructor creation
            System.out.println("User verified successfully.");
        }

        goals = new FinancialGoals(email);
        goals.Goalcompletion();
    }

    public Client(String email, boolean fromGUI) {
        this.email = email;
        goals = new FinancialGoals(email);
    }

    @Override   //overrides get_Account method from user
    public String[] get_Account(){
        System.out.println("Client sign in.");
        return super.get_Account();
    }

    @Override
    void menu(){     //menu interface for client
        int choice1 = -1;
        int choice2 = -1;
        int choice3 = -1;
        int choice4 = -1;

        while (choice1 != 0) {
            choice1 = -1;
            System.out.println("1.Change password\n2.Enter amount\n3.Set budget\n4.View your activities" +
                    "\n5.Financial goals options\n0.Exit");
            choice1 = cin.nextInt();

            switch (choice1) {
                case 1:
                    Change_pass();
                    break;
                case 2:
                    amount();
                    break;
                case 3:
                    budgeting.bugeting(this.email);
                    break;
                case 4:
                    while(choice4 != 0){
                        System.out.println("1.Inserting amount activity\n2.Setting budget activity" +
                                "\n3.Total income\n4.Total expense\n0.Return to client menu");
                        choice4 = cin.nextInt();
                        switch (choice4){
                            case 1:
                                PrintAmountActivity();
                                break;
                            case 2:
                                PrintBudgetActivity();
                                break;
                            case 3:
                                PrintTotalIncome();
                                break;
                            case 4:
                                PrintTotalExpense();
                                break;
                            case 0:
                                System.out.println("Returning...");
                                break;
                            default:
                                System.out.println("Invalid choice.");
                        }
                    }
                case 5:
                    while(choice2 != 0){
                        System.out.println("1.Set Financial goal\n2.Check deadline\n3.Check goals\n0.Return");
                        choice2 = cin.nextInt();

                        switch(choice2){
                            case 1:
                                goals.SetGoal();
                                break;
                            case 2:
                                goals.checkDeadline();
                                break;
                            case 3:
                                while(choice3 != 0){
                                    System.out.println("1.View completed goals\n2.View active goals\n3.View missed goals" +
                                            "\n0.Return to client menu");
                                    choice3 = cin.nextInt();
                                    switch (choice3){
                                        case 1:
                                            goals.viewgoalsbyStatus("completed");
                                            break;
                                        case 2:
                                            goals.viewgoalsbyStatus("active");
                                            break;
                                        case 3:
                                            goals.viewgoalsbyStatus("missed");
                                            break;
                                        case 0:
                                            System.out.println("Returning...");
                                            break;
                                        default:
                                            System.out.println("Invalid choice");
                                    }
                                }
                            case 0:
                                System.out.println("Returning...");
                                break;
                        }
                    }
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
            }
        }
    }



    private void Change_pass(){     //method to change password
        //havent added a option to enter old password before opting for new password yet, will do soon.
        System.out.println("Enter new password:");
        String new_pass = cin.next();

        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = Databasehelper.connect();    //connects to the database
             PreparedStatement pstmt = conn.prepareStatement(sql))      //makes a preparedstatement to execute the update in sql
        {
            pstmt.setString(1,new_pass);
            pstmt.setString(2,this.email);
            int updatedRows = pstmt.executeUpdate();

            if (updatedRows > 0)    //if updatedrows == 0 this means password change failed since after the update.
                                    // there are no rows for the given email
            {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Password change failed.");
            }

        } catch (SQLException e) {      //catching any error occured while executing the update
            System.out.println("Error occured: " + e.getMessage());
        }
    }



    private void amount() {

        System.out.println("Enter the amount you want to add:");   //asks amount
        double amount = cin.nextDouble();

        if (amount <= 0) {      //makes sure amount is positive
            System.out.println("Amount must be positive. Please try again.");
            return;
        }

        //variables declaration and initialization
        String amount_type = "";
        boolean validinput = false;
        boolean updatedBudget = true;


        try(Connection conn = Databasehelper.connect()) {
            while (!validinput) {
                System.out.println("Enter amount type (Income,expense):");
                amount_type = cin.next();
                cin.nextLine(); //absorbs the next empty line

                if (!amount_type.equalsIgnoreCase("Income") && !amount_type.equalsIgnoreCase("expense"))
                {   //makes sure amount type is either income or expense if not then opts the user to enter again.
                    System.out.println("Enter valid amount type.");
                }
                else if (amount_type.equalsIgnoreCase("expense"))
                {   //if amount type is expense
                    System.out.println("Enter the category for your expense:");
                    String category = cin.nextLine();   //prompts for the category from the budget table for expense

                    String sql = "SELECT remaining_budget FROM budget WHERE user_email = ? AND budget_category = ?";

                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, this.email);
                        pstmt.setString(2, category);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) //if rs.next() == true it means there is a matching budget for the given category
                        {
                            double remaining = rs.getDouble("remaining_budget");    //gets remaining budget
                            if (amount > remaining) {   //makes sure amount is less than budget
                                System.out.println("Expense amount exceeds remaining budget. Cannot proceed");
                                updatedBudget = false;
                            }
                            else {  //if amount is less than budget it updates budget by subtracting amount from the remaining_budget
                                String update = "UPDATE budget SET remaining_budget = remaining_budget - ? WHERE user_email = ? AND budget_category = ?";
                                try (PreparedStatement stmtupdate = conn.prepareStatement(update)) {
                                    stmtupdate.setDouble(1, amount);
                                    stmtupdate.setString(2, this.email);
                                    stmtupdate.setString(3, category);
                                    stmtupdate.executeUpdate();
                                }
                            }
                        }
                        else {  //if rs.next() == false it means no budget row found with the given category
                            System.out.println("No matching budget found for the given category.");
                            updatedBudget = false;
                        }
                    }

                    validinput = true;
                } else if (amount_type.equalsIgnoreCase("income")) //if amount type is income
                {
                    validinput = true;
                }
            }

            if (updatedBudget) {    //if updateBudget == true it inserts the amount to the table (amount)
                String sql = "INSERT INTO amount (user_email,type,amount) VALUES (?,?,?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, this.email);
                    stmt.setString(2, amount_type);
                    stmt.setDouble(3, amount);
                    stmt.executeUpdate();

                } catch (SQLException e) {
                    System.out.println("Inserting amount failed: " + e.getMessage());
                }
                System.out.println("Amount entered!");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    public static double getTotalincome(String email){    //method to get total income
        String sql = "SELECT SUM(amount) FROM amount WHERE user_email = ? AND type = 'income'";
        try(Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("Error fetching total income: " + e.getMessage());;
        }
        return 0.0;
    }

    public static double getTotalexpense(String email){   //method to get total expense
        String sql = "SELECT SUM(amount) FROM amount WHERE user_email = ? AND type = 'expense'";
        try(Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("Error fetching total expense: " + e.getMessage());;
        }
        return 0.0;
    }

    private void PrintAmountActivity(){     //prints the amount activity of client
        String sql = "SELECT * FROM amount WHERE user_email = ?";
        try(Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,this.email);
            ResultSet row = pstmt.executeQuery();
            boolean Data = false;

            System.out.println("Your Transactions");
            System.out.println("----------------------------");
            while (row.next()){
                Data = true;
                System.out.println("Amount: " + row.getString("amount") +
                        " | Type: " + row.getString("type") +
                        " | Date: " + row.getString("date"));
            }

            if(!Data){
                System.out.println("No transactions found.");
            }
            System.out.println("----------------------------");

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    private void PrintTotalIncome(){    //prints total income
        double totalincome = getTotalincome(this.email);
        System.out.println("------------------------\nTotal income: " + String.format("%.2f", totalincome) + "PKR\n------------------------");
    }

    private void PrintTotalExpense(){   //method to print total expense
        double totalexpense = getTotalexpense(this.email);
        System.out.println("------------------------\nTotal expense: " + String.format("%.2f", totalexpense) + "PKR\n------------------------");
    }

    public void PrintBudgetActivity(){   //method to print budget activity
        String sql = "SELECT * FROM budget WHERE user_email = ?";
        try(Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,this.email);
            ResultSet row = pstmt.executeQuery();
            boolean Data = false;

            System.out.println("Your Transactions");
            System.out.println("----------------------------");
            while (row.next()){
                Data = true;
                System.out.println("Category: " + row.getString("budget_category") +
                        " | Initial Budget: " + row.getString("amount") +
                        " | Remaining Budget: " + row.getString("remaining_budget"));
            }

            if(!Data){
                System.out.println("No transactions found.");
            }
            System.out.println("----------------------------");

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    //getters
    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    //for GUI
    public void changePasswordGUI() {
        String new_pass = JOptionPane.showInputDialog(null, "Enter new password:");

        if (new_pass != null && !new_pass.isEmpty()) {
            String sql = "UPDATE users SET password = ? WHERE email = ?";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, new_pass);
                pstmt.setString(2, this.email);
                int updatedRows = pstmt.executeUpdate();

                if (updatedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Password changed successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Password change failed.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error occurred: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Password change cancelled or invalid input.");
        }
    }

    // Add these methods to your Client class

    public void amountGUI() {
        // Create the main dialog
        JDialog amountDialog = new JDialog();
        amountDialog.setTitle("Enter Amount");
        amountDialog.setSize(400, 350);
        amountDialog.setLocationRelativeTo(null);
        amountDialog.setLayout(new BorderLayout());
        amountDialog.setModal(true);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new GridLayout(0, 1, 10, 10));

        // Amount field
        JPanel amountPanel = new JPanel(new BorderLayout());
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        amountPanel.add(amountField, BorderLayout.CENTER);
        mainPanel.add(amountPanel);

        // Type selection
        JPanel typePanel = new JPanel(new BorderLayout());
        JLabel typeLabel = new JLabel("Type:");
        ButtonGroup typeGroup = new ButtonGroup();
        JRadioButton incomeRadio = new JRadioButton("Income");
        JRadioButton expenseRadio = new JRadioButton("Expense");
        typeGroup.add(incomeRadio);
        typeGroup.add(expenseRadio);
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(incomeRadio);
        radioPanel.add(expenseRadio);
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(radioPanel, BorderLayout.CENTER);
        mainPanel.add(typePanel);

        // Category field (only visible for expense)
        JPanel categoryPanel = new JPanel(new BorderLayout());
        JLabel categoryLabel = new JLabel("Category (for expense):");
        JTextField categoryField = new JTextField();
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        categoryPanel.add(categoryField, BorderLayout.CENTER);
        categoryPanel.setVisible(false);
        mainPanel.add(categoryPanel);

        // Show/hide category field based on radio selection
        incomeRadio.addActionListener(e -> categoryPanel.setVisible(false));
        expenseRadio.addActionListener(e -> categoryPanel.setVisible(true));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(amountDialog, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String amountType = incomeRadio.isSelected() ? "income" : "expense";
                String category = expenseRadio.isSelected() ? categoryField.getText() : null;

                if (expenseRadio.isSelected() && (category == null || category.isEmpty())) {
                    JOptionPane.showMessageDialog(amountDialog, "Category is required for expenses", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Process the amount (similar to your original amount() method)
                boolean success = processAmount(amount, amountType, category);
                if (success) {
                    JOptionPane.showMessageDialog(amountDialog, "Amount entered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    amountDialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(amountDialog, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel action
        cancelButton.addActionListener(e -> amountDialog.dispose());

        amountDialog.add(mainPanel, BorderLayout.CENTER);
        amountDialog.add(buttonPanel, BorderLayout.SOUTH);
        amountDialog.setVisible(true);
    }

    private boolean processAmount(double amount, String amountType, String category) {
        boolean updatedBudget = true;

        try (Connection conn = Databasehelper.connect()) {
            if (amountType.equalsIgnoreCase("expense")) {
                // Check budget for expense
                String sql = "SELECT remaining_budget FROM budget WHERE user_email = ? AND budget_category = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, this.email);
                    pstmt.setString(2, category);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        double remaining = rs.getDouble("remaining_budget");
                        if (amount > remaining) {
                            JOptionPane.showMessageDialog(null, "Expense amount exceeds remaining budget. Cannot proceed");
                            return false;
                        } else {
                            // Update budget
                            String update = "UPDATE budget SET remaining_budget = remaining_budget - ? WHERE user_email = ? AND budget_category = ?";
                            try (PreparedStatement stmtupdate = conn.prepareStatement(update)) {
                                stmtupdate.setDouble(1, amount);
                                stmtupdate.setString(2, this.email);
                                stmtupdate.setString(3, category);
                                stmtupdate.executeUpdate();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No matching budget found for the given category.");
                        return false;
                    }
                }
            }

            // Insert the amount
            String sql = "INSERT INTO amount (user_email, type, amount) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, this.email);
                stmt.setString(2, amountType);
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void printAmountGUI() {
        JDialog transactionsDialog = new JDialog();
        transactionsDialog.setTitle("Your Transactions");
        transactionsDialog.setSize(600, 400);
        transactionsDialog.setLocationRelativeTo(null);
        transactionsDialog.setLayout(new BorderLayout());

        // Create table model
        String[] columns = {"Amount", "Type", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Fetch data from database
        String sql = "SELECT amount, type, date FROM amount WHERE user_email = ? ORDER BY date DESC";
        try (Connection conn = Databasehelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        String.format("%.2f", rs.getDouble("amount")),
                        rs.getString("type"),
                        rs.getString("date")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(transactionsDialog, "Error fetching transactions: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create table
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add summary information
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2));
        double totalIncome = getTotalincome(this.email);
        double totalExpense = getTotalexpense(this.email);

        JLabel incomeLabel = new JLabel("Total Income: " + String.format("%.2f", totalIncome) + " PKR");
        JLabel expenseLabel = new JLabel("Total Expense: " + String.format("%.2f", totalExpense) + " PKR");

        summaryPanel.add(incomeLabel);
        summaryPanel.add(expenseLabel);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> transactionsDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);

        transactionsDialog.add(scrollPane, BorderLayout.CENTER);
        transactionsDialog.add(summaryPanel, BorderLayout.NORTH);
        transactionsDialog.add(buttonPanel, BorderLayout.SOUTH);
        transactionsDialog.setVisible(true);
    }

    public void setBudgetGUI() {
        JDialog budgetDialog = new JDialog();
        budgetDialog.setTitle("Set Budget");
        budgetDialog.setSize(400, 300);
        budgetDialog.setLocationRelativeTo(null);
        budgetDialog.setLayout(new BorderLayout());
        budgetDialog.setModal(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new GridLayout(0, 1, 10, 10));

        // Category field
        JPanel categoryPanel = new JPanel(new BorderLayout());
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField();
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        categoryPanel.add(categoryField, BorderLayout.CENTER);
        mainPanel.add(categoryPanel);

        // Amount field
        JPanel amountPanel = new JPanel(new BorderLayout());
        JLabel amountLabel = new JLabel("Budget Amount:");
        JTextField amountField = new JTextField();
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        amountPanel.add(amountField, BorderLayout.CENTER);
        mainPanel.add(amountPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // Submit action
        submitButton.addActionListener(e -> {
            try {
                String category = categoryField.getText();
                double amount = Double.parseDouble(amountField.getText());

                if (category.isEmpty()) {
                    JOptionPane.showMessageDialog(budgetDialog, "Category cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(budgetDialog, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert into database
                String sql = "INSERT INTO budget (user_email, budget_category, amount, remaining_budget) VALUES (?, ?, ?, ?)";
                try (Connection conn = Databasehelper.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, this.email);
                    stmt.setString(2, category);
                    stmt.setDouble(3, amount);
                    stmt.setDouble(4, amount);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(budgetDialog, "Budget set successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    budgetDialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(budgetDialog, "Error setting budget: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(budgetDialog, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel action
        cancelButton.addActionListener(e -> budgetDialog.dispose());

        budgetDialog.add(mainPanel, BorderLayout.CENTER);
        budgetDialog.add(buttonPanel, BorderLayout.SOUTH);
        budgetDialog.setVisible(true);
    }
}