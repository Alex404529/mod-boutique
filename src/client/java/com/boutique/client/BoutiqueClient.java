package com.boutique.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

public class BoutiqueClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ajoute un bouton « Boutique » à côté de l'inventaire du joueur (survie).
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen) {
                int left = (width - 176) / 2;
                int top = (height - 166) / 2;
                Screens.getButtons(screen).add(
                        Button.builder(Component.literal("Boutique"),
                                        b -> client.setScreen(new ShopScreen()))
                                .bounds(left + 180, top, 70, 20)
                                .build());
            }
        });
    }
}
