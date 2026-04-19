package exception;

import java.math.BigDecimal;

public class BankruptcyException extends RuntimeException {

    private final BigDecimal finalBudget;

    public BankruptcyException(BigDecimal finalBudget) {
        super("BANKRUPTCY — budget fell to $" + finalBudget.toPlainString());
        this.finalBudget = finalBudget;
    }

    public BigDecimal getFinalBudget() { return finalBudget; }
}
