package eu.pb4.playerdata.api.storage;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.impl.PMI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import java.nio.file.Path;
import java.util.UUID;

public record NbtDataStorage(String path) implements PlayerDataStorage<CompoundTag> {

    @Override
    public boolean save(MinecraftServer server, UUID player, CompoundTag settings) {
        if (settings == null) {
            return false;
        }

        try {
            Path path = PlayerDataApi.getPathFor(server, player);
            path.toFile().mkdirs();

            NbtIo.writeCompressed(settings, path.resolve(this.path + ".dat").toFile());
            return true;
        } catch (Exception e) {
            PMI.LOGGER.error(String.format("Couldn't save player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CompoundTag load(MinecraftServer server, UUID player) {
        try {
            Path path = PlayerDataApi.getPathFor(server, player).resolve(this.path + ".dat");
            if (!path.toFile().exists()) {
                return null;
            }

            return NbtIo.readCompressed(path.toFile());
        } catch (Exception e) {
            PMI.LOGGER.error(String.format("Couldn't load player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return null;
        }
    }
}
