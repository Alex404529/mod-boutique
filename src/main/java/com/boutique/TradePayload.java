package com.boutique;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Message client -> serveur : acheter ou vendre `qty` exemplaires de `itemId`. */
public record TradePayload(String itemId, int qty, boolean buy) implements CustomPacketPayload {
    public static final Type<TradePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(BoutiqueMod.MOD_ID, "trade"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TradePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TradePayload::itemId,
            ByteBufCodecs.VAR_INT, TradePayload::qty,
            ByteBufCodecs.BOOL, TradePayload::buy,
            TradePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
