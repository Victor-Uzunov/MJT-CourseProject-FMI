package bg.sofia.uni.fmi.mjt.splitwise.payment;

import java.io.Serializable;

public enum PaymentType implements Serializable {
    GROUP_PAYMENT("Group payment"),
    FRIEND_PAYMENT("Friend Payment");

    private final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
