package com.boutique.client;

import com.boutique.BoutiqueMod;
import com.boutique.ShopItems;
import com.boutique.TradePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ShopScreen extends Screen {
    private Button moneyLabel;

    public ShopScreen() {
        super(Component.literal("Boutique"));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 150;
        int y = 30;

        moneyLabel = Button.builder(moneyText(), b -> {}).bounds(x, 6, 300, 20).build();
        moneyLabel.active = false;
        addRenderableWidget(moneyLabel);

        for (Map.Entry<String, int[]> e : ShopItems.PRICES.entrySet()) {
            String id = e.getKey();
            int[] price = e.getValue();
            String name = BuiltInRegistries.ITEM.getValue(Identifier.parse(id)).getName().getString();

            // Nom + prix (achat / vente)
            Button info = Button.builder(
                    Component.literal(name + "  (achat " + price[0] + " / vente " + price[1] + ")"),
                    b -> {}).bounds(x, y, 170, 20).build();
            info.active = false;
            addRenderableWidget(info);

            addRenderableWidget(trade("+1", id, 1, true, x + 172, y));
            addRenderableWidget(trade("+10", id, 10, true, x + 200, y, 30));
            addRenderableWidget(trade("-1", id, 1, false, x + 232, y));
            addRenderableWidget(trade("-10", id, 10, false, x + 260, y, 30));
            y += 22;
        }
    }

    private Button trade(String label, String id, int qty, boolean buy, int x, int y) {
        return trade(label, id, qty, buy, x, y, 26);
    }

    private Button trade(String label, String id, int qty, boolean buy, int x, int y, int w) {
        return Button.builder(Component.literal(label),
                        b -> ClientPlayNetworking.send(new TradePayload(id, qty, buy)))
                .bounds(x, y, w, 20).build();
    }

    private Component moneyText() {
        var player = Minecraft.getInstance().player;
        int money = player == null ? 0 : player.getAttachedOrElse(BoutiqueMod.MONEY, 0);
        return Component.literal("Argent : " + money + " $   (+ acheter, - vendre)");
    }

    @Override
    public void tick() {
        super.tick();
        if (moneyLabel != null) moneyLabel.setMessage(moneyText());
    }
}
