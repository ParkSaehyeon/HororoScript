package hororo.saehyeon.script.main;

import hororo.saehyeon.script.main.script.Script;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class HororoScript extends JavaPlugin implements CommandExecutor {

    public static HororoScript instance;

    @Override
    public void onEnable() {
        instance = this;

        getDataFolder().mkdir();

        Bukkit.getPluginCommand("hororo-script").setExecutor(this);

        Script.loadAll();

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(label.equals("hororo-script")) {
            switch (args[0]) {
                case "load":

                    if(args[1].equals("all")) {
                        Script.loadAll();
                        sender.sendMessage("모든 스크립트를 다시 불러왔습니다.");
                    } else {
                        if(Script.load(args[1])) {
                            sender.sendMessage(args[1]+" 스크립트를 다시 불러왔습니다.");
                        } else {
                            sender.sendMessage(args[1]+" 스크립트를 불러오지 못했습니다. 자세한 내용은 콘솔을 확인해주세요.");
                        }
                    }

                    break;

                case "execute":

                    Script.execute(args[1], Integer.parseInt(args[2]));
                    break;
            }
        }

        return false;
    }

    public static void log(String message) {
        instance.getLogger().log(Level.INFO,message);
    }

    public static void errorLog(String errorMessage) {
        instance.getLogger().warning("§c"+errorMessage);
    }
}
