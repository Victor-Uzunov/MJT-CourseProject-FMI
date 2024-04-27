package bg.sofia.uni.fmi.mjt.splitwise.payment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PaymentData implements Serializable {
    private final Map<String, Double> data;

    public PaymentData(Set<String> usernames) {
        this.data = new HashMap<>();
        for (String u : usernames) {
            data.put(u, 0.0);
        }
    }

    public Map<String, Double> getData() {
        return data;
    }

    public void add(String key, double value) {
        data.put(key, value);
    }
}
