package com.boutique;

import java.util.LinkedHashMap;
import java.util.Map;

/** Catalogue : id de l'objet -> {prix d'achat, prix de vente}. Modifie ici. */
public final class ShopItems {
    public static final Map<String, int[]> PRICES = new LinkedHashMap<>();
    static {
        PRICES.put("minecraft:cobblestone", new int[]{4, 1});
        PRICES.put("minecraft:oak_log",     new int[]{8, 2});
        PRICES.put("minecraft:wheat",       new int[]{6, 2});
        PRICES.put("minecraft:bread",       new int[]{12, 4});
        PRICES.put("minecraft:iron_ingot",  new int[]{40, 15});
        PRICES.put("minecraft:gold_ingot",  new int[]{80, 30});
        PRICES.put("minecraft:diamond",     new int[]{400, 150});
        PRICES.put("minecraft:emerald",     new int[]{200, 80});
        PRICES.put("minecraft:ender_pearl", new int[]{150, 50});
    }
    private ShopItems() {}
}
