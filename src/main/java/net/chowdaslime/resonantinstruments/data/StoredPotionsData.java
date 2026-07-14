package net.chowdaslime.resonantinstruments.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public record StoredPotionsData(List<PotionContents> potions) {

    public static final StoredPotionsData EMPTY = new StoredPotionsData(List.of());

    public static final Codec<StoredPotionsData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PotionContents.CODEC.listOf().fieldOf("potions").forGetter(StoredPotionsData::potions)
            ).apply(instance, StoredPotionsData::new)
    );

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, StoredPotionsData> STREAM_CODEC =
            StreamCodec.composite(
                    PotionContents.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    StoredPotionsData::potions,
                    StoredPotionsData::new
            );
}