package api;

import com.sun.net.httpserver.HttpServer;
import util.Databasehelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ApiServer {

    public static void main(String[] args) throws IOException {
        Databasehelper.create_table();
        Databasehelper.create_activity_log_table();

        HttpServer server = HttpServer.create(new InetSocketAddress(7000), 0);

        server.createContext("/api/login", new AuthController.LoginHandler());
        server.createContext("/api/register", new AuthController.RegisterHandler());
        server.createContext("/api/summary", new DashboardController.SummaryHandler());
        server.createContext("/api/transactions", new TransactionController.Handler());
        server.createContext("/api/budgets", new BudgetController.Handler());
        server.createContext("/api/goals", new GoalController.Handler());
        server.createContext("/api/report", new ReportController.Handler());

        server.createContext("/api/admin/users", new AdminController.UsersHandler());
        server.createContext("/api/admin/delete-user", new AdminController.DeleteUserHandler());
        server.createContext("/api/admin/activity", new AdminController.ActivityHandler());
        server.createContext("/api/admin/financial-report", new AdminController.FinancialReportHandler());
        server.createContext("/api/admin/change-password", new AdminController.ChangePasswordHandler());

        server.createContext("/", new StaticFileHandler());

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Server running at http://localhost:7000");
    }
}