package com.boutique;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BoutiqueMod implements ModInitializer {
    public static final String MOD_ID = "boutique";
    public static final int START_MONEY = 100;

    /** Argent du joueur : sauvegardé, conservé à la mort, synchronisé avec le client. */
    public static final AttachmentType<Integer> MONEY = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MOD_ID, "money"),
            b -> b.initializer(() -> START_MONEY)
                  .persistent(Codec.INT)
                  .copyOnDeath()
                  .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly()));

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(TradePayload.TYPE, TradePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TradePayload.TYPE, (payload, context) ->
                handleTrade(context.player(), payload));
    }

    private static void handleTrade(ServerPlayer player, TradePayload p) {
        int[] price = ShopItems.PRICES.get(p.itemId());
        int qty = p.qty();
        if (price == null || qty < 1 || qty > 64) return; // sécurité : on ne fait pas confiance au client

        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(p.itemId()));
        int money = player.getAttachedOrElse(MONEY, 0);

        if (p.buy()) {
            int cost = price[0] * qty;
            if (money < cost) {
                player.displayClientMessage(Component.literal("Pas assez d'argent !"), true);
                return;
            }
            player.setAttached(MONEY, money - cost);
            ItemStack stack = new ItemStack(item, qty);
            if (!player.getInventory().add(stack)) player.drop(stack, false);
        } else {
            int have = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (s.is(item)) have += s.getCount();
            }
            int sold = Math.min(have, qty);
            if (sold == 0) {
                player.displayClientMessage(Component.literal("Tu n'as pas cet objet."), true);
                return;
            }
            int left = sold;
            for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (s.is(item)) {
                    int take = Math.min(left, s.getCount());
                    s.shrink(take);
                    left -= take;
                }
            }
            player.setAttached(MONEY, money + price[1] * sold);
        }
    }
}
