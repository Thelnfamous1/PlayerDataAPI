package me.infamous.playerdata;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(PlayerDataApiMod.MODID)
public class PlayerDataApiMod {
    public static final String MODID = "playerdataapi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PlayerDataApiMod() {}
}
