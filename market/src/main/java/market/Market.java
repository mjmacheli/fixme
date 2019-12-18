package market;

import java.util.ArrayList;

public class Market {
    ArrayList<Index> indices;

    {
        indices = new ArrayList<>();
        indices.add(new Index(42, "SILVER"));
        indices.add(new Index(42, "GOLD"));
        indices.add(new Index(42, "PLAT"));
    }

    String getMarket() {
        String market = "";

        for (Index in : indices) {
            market += in.index + " " + in.amount + "\n";
        }
        return market;
    }

    String buy(String item, int amount) {
        for (Index i : indices) {
            if (i.index.equalsIgnoreCase(item)) {
                if (i.amount >= amount) {
                    i.amount -= amount;
                    return "Success ";
                }
            }
            // System.out.println("it "+ item + " "+ i.index );
        }
        return "Not Success";
    }

    String sell(String item, int amount) {
        for (Index i : indices) {
            /** Market cannot have more than 42 of stock */
            if (i.index.equalsIgnoreCase(item) && amount + i.amount <= 42) {
                i.amount += amount;
                return "Success ";
            }
        }
        return "Not Success";
    }
}