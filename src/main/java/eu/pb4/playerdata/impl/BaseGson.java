package eu.pb4.playerdata.impl;


import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Type;

public class BaseGson {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
            .registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocation.Serializer())

            .registerTypeHierarchyAdapter(Item.class, new RegistrySerializer<>(Registry.ITEM))
            .registerTypeHierarchyAdapter(Block.class, new RegistrySerializer<>(Registry.BLOCK))
            .registerTypeHierarchyAdapter(Enchantment.class, new RegistrySerializer<>(Registry.ENCHANTMENT))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistrySerializer<>(Registry.SOUND_EVENT))
            .registerTypeHierarchyAdapter(MobEffect.class, new RegistrySerializer<>(Registry.MOB_EFFECT))
            .registerTypeHierarchyAdapter(EntityType.class, new RegistrySerializer<>(Registry.ENTITY_TYPE))
            .registerTypeHierarchyAdapter(BlockEntityType.class, new RegistrySerializer<>(Registry.BLOCK_ENTITY_TYPE))

            .registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())

            .registerTypeHierarchyAdapter(ItemStack.class, new CodecSerializer<>(ItemStack.CODEC))
            .registerTypeHierarchyAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
            .registerTypeHierarchyAdapter(Vec3.class, new CodecSerializer<>(Vec3.CODEC))
            .setLenient().create();


    private record RegistrySerializer<T>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.registry.get(ResourceLocation.tryParse(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("" + this.registry.getId(src));
        }
    }

    private record CodecSerializer<T>(Codec<T> codec) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return this.codec.decode(JsonOps.INSTANCE, json).getOrThrow(false, (x) -> {}).getFirst();
            } catch (Throwable e) {
                return null;
            }
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                return src != null ? this.codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false, (x) -> {}) : JsonNull.INSTANCE;
            } catch (Throwable e) {
                return JsonNull.INSTANCE;
            }
        }
    }
}
