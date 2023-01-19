package hororo.saehyeon.script.main;

import org.bukkit.plugin.java.JavaPlugin;

public final class HororoScript extends JavaPlugin {

    public static HororoScript instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
