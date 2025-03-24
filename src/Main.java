import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Exception for dealing with negative deposits
class NegativeDepositException extends Exception {
    public NegativeDepositException(String message) {
        super(message);
    }
}

//Exception for handling overdraft
class OverdrawException extends Exception{
    public OverdrawException(String message) {
        super(message);
    }
}

//Exception for handling invalid operations on accounts
class InvalidAccountOperationException extends Exception {
    public InvalidAccountOperationException(String message) {
        super(message);
    }
}

// ============================
// Observer Pattern - Define Observer Interface
// ============================
interface Observer {
    void update(String message);
}
// Concrete observer for logging transactions
class TransactionLogger implements Observer {
    public void update (String message) {
        System.out.println(message);
    }
}

// ============================
// BankAccount (Subject in Observer Pattern)
// ============================
class BankAccount {
    protected String accountNumber;
    protected double balance;
    protected boolean isActive;
    private List<Observer> observers = new ArrayList<>();

    public BankAccount(String accNum, double initialBalance) {
        this.accountNumber = accNum;
        this.balance = initialBalance;
        this.isActive = true;
    }

    // Attach observer to the account
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Notify all observers when transactions occur
    private void notifyObservers(String message) {
        // TODO: Notify all observers when a transaction occurs
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    // Method to deposit money into account with exception handling
    public void deposit(double amount) throws NegativeDepositException, InvalidAccountOperationException {
        if (!isActive) {
            throw new InvalidAccountOperationException("Cannot deposit, account is closed");
        }
        if (amount < 0) {
            throw new NegativeDepositException("Negative deposit amount");
        }
        this.balance += amount;
        notifyObservers("Deposited: $" + amount);
    }

    // Method to withdraw money from account with exception handling
    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        if (!isActive) {
            throw new InvalidAccountOperationException("Cannot withdraw, account is closed");
        }
        if (amount > this.balance) {
            throw new OverdrawException("Insufficient funds");
        }
        this.balance -= amount;
        notifyObservers("Withdrawn: $" + amount);
    }

    // Method to get current balance
    public double getBalance() {
        return balance;
    }

    // Method to close account with exception handling
    public void closeAccount() throws InvalidAccountOperationException {
        if (!isActive) {
            throw new InvalidAccountOperationException("Account is already closed");
        }
        this.isActive = false;
        notifyObservers("Account closed");
    }
}

// ============================
// Decorator Pattern - Define SecureBankAccount Class
// ============================
abstract class BankAccountDecorator extends BankAccount {
    protected BankAccount decoratedAccount;

    public BankAccountDecorator(BankAccount account) {
        super(account.accountNumber, account.getBalance());
        this.decoratedAccount = account;
    }

    // Initialize methods again
    @Override
    public void deposit(double amount) throws NegativeDepositException, InvalidAccountOperationException {
        decoratedAccount.deposit(amount);
    }

    @Override
    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        decoratedAccount.withdraw(amount);
    }

    @Override
    public double getBalance() {
        return decoratedAccount.getBalance();
    }

    @Override
    public void closeAccount() throws InvalidAccountOperationException {
        decoratedAccount.closeAccount();
    }
}

// Concrete decorator to enforce a withdrawal limit
class SecureBankAccount extends BankAccountDecorator {
    public SecureBankAccount(BankAccount account) {
        super(account);
    }

    @Override
    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        if (amount > 500) {
            throw new OverdrawException("Exceeded the limit");
        }
        decoratedAccount.withdraw(amount);
    }
}
// ============================
// Main Program
// ============================

class BankAccountTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Create a new account with balance and account #
            System.out.print("Enter initial balance: ");
            double initialBalance = scanner.nextDouble();
            System.out.print("Enter account number: ");
            String accNum = scanner.next();
            BankAccount account = new BankAccount(accNum, initialBalance);

            System.out.println("Bank Account Created for Account Number: " + accNum);

            // Attach transaction logger
            TransactionLogger logger = new TransactionLogger();
            account.addObserver(logger);

            // Wrap account with withdraw security feature
            SecureBankAccount secureBankAccount = new SecureBankAccount(account);

            // Deposit to secured account
            System.out.print("Enter an amount to deposit: ");
            double deposit = scanner.nextDouble();
            secureBankAccount.deposit(deposit);

            // Withdraw from secured account
            System.out.print("Enter an amount to withdraw: ");
            double withdraw = scanner.nextDouble();
            secureBankAccount.withdraw(withdraw);

            // Output the balance
            System.out.println("Account Balance: " + account.getBalance());

            // Close account
            secureBankAccount.closeAccount();

            // Try depositing to closed account to demonstrate the exception
            System.out.println("Depositing $500");
            secureBankAccount.deposit(500);

            // Exception handling for the different possible cases
        } catch (InvalidAccountOperationException e) {
            System.out.println("Invalid operation: " + e.getMessage());
        } catch (NegativeDepositException | OverdrawException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
