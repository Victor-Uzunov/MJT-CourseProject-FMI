package bg.sofia.uni.fmi.mjt.splitwise.payment;

import java.io.Serializable;

public record Payment(double amount, String reason) implements Serializable {

}
