package market;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class Index {
    @NonNull public int amount;
    @NonNull public String index;
}