package hororo.saehyeon.script.main.script;

import hororo.saehyeon.script.main.HororoScript;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Script {
    public static HashMap<String, ArrayList<String>> scripts = new HashMap<>();
    public static HashMap<String, Integer> scriptIndex = new HashMap<>();
    public static ImageUnicode imageUnicode = null;

    /**
     * 이미지가 있는 메세지를 출력 시에 이미지가 안정적으로 출력되기 위해 출력되어야 하는 최소한의 메세지 줄 갯수 입니다.
     */
    public static final int ESSENTIAL_LINES = 8;

    public static void loadAll() {
        File[] files = HororoScript.instance.getDataFolder().listFiles( ( (dir, name) -> name.toLowerCase().endsWith(".hororo") ));

        if(files == null) return;

        for(File file : files) {
            load(file.getName().substring(0,file.getName().lastIndexOf(".")));
        }
    }

    public static boolean load(String scriptName) {

        try {

            File file = new File(HororoScript.instance.getDataFolder(), scriptName+".hororo");

            if(!file.exists()) {
                HororoScript.errorLog(scriptName+"(이)라는 스크립트를 찾을 수 없습니다. (플러그인 폴더에 "+scriptName+".hororo 파일이 없는 것 같습니다.)");
                return false;
            }

            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            BufferedReader buf = new BufferedReader(reader);

            String line = "";
            ArrayList<String> lines = new ArrayList<>();

            while((line = buf.readLine()) != null)
                lines.add(line);

            scripts.put(scriptName, lines);

            HororoScript.log(scriptName+" 스크립트를 로드했습니다.");
            return true;

        } catch (Exception e) {

            HororoScript.errorLog(scriptName+" 스크립트를 로드하던 도중 오류가 발생했습니다. ("+e+")");

        }

        return false;

    }

    public static void execute(String scriptName, int index) {

        if(scripts.containsKey(scriptName)) {

            ArrayList<String> lines = scripts.getOrDefault(scriptName, new ArrayList<>());

            if(lines.size() <= index) {
                HororoScript.log(scriptName+" 스크립트에는 "+index+"번째 줄이 없기 때문에 스크립트 실행을 종료했습니다.");
                return;
            }

            /* 구문 시작 */
            scriptIndex.put(scriptName, index);

            String line = lines.get(index);

            // 주석일시 다음 줄 실행
            if(line.toCharArray()[0] == '#') {
                executeNextLine(scriptName);
                return;
            }

            // 구문 실행
            String command  = line.split(" ")[0].toLowerCase();
            String arg      = line.replaceFirst(line.split(" ")[0]+" ","");
            String[] args   = line.replaceFirst(line.split(" ")[0]+" ","").split(" ");

            switch (command) {
                case "setimage":

                    try {

                        if(args.length >= 1) {
                            imageUnicode = ImageUnicode.valueOf(args[0]);
                        } else {
                            errorLog("setimage 구문은 한 개의 인자가 있어야 합니다.",scriptName,index);
                        }

                    }

                    catch (IllegalArgumentException e) {

                        errorLog(args[0]+"(은)는 올바른 종류의 인자가 아닙니다.",scriptName,index);

                    }

                    break;

                case "send":

                    String message = ChatColor.translateAlternateColorCodes('&', arg);
                    try {

                        imageUnicode = ImageUnicode.valueOf(args[0]);

                    } catch (Exception ignored) {}


                    if(imageUnicode != null) {

                        // 채팅 위로 올리기
                        for(int i = 0; i < 10; i++)
                            Bukkit.broadcastMessage("");

                        Bukkit.broadcastMessage("" + imageUnicode.getImageUnicode());
                        Bukkit.broadcastMessage("");

                        int nowPrintLine = 1;

                        // 줄 바꿈
                        for(String str : message.split("\\\\n")) {
                            Bukkit.broadcastMessage("§f                  "+str);
                            nowPrintLine++;
                        }

                        // 부족한 줄들을 공백 메세지로 채우기
                        if(nowPrintLine < ESSENTIAL_LINES) {
                            for(int i = 0 ; i < ESSENTIAL_LINES-nowPrintLine; i++) {
                                Bukkit.broadcastMessage("");
                            }
                        }

                    } else {

                        Bukkit.broadcastMessage(message);

                    }

                    break;

                case "select":

                    break;

                case "start":
                    execute(arg, 0);
                    break;

                case "wait":

                    try {

                        float delay = Float.parseFloat(args[0]);

                        if(delay > 0) {

                            Bukkit.getScheduler().runTaskLater(HororoScript.instance, () -> executeNextLine(scriptName),20*(long)delay);

                            return;

                        } else {
                            errorLog("기다릴 초는 1 이상의 수여야 합니다.",scriptName,index);
                        }

                    } catch (Exception e) {

                        errorLog("기다릴 초는 1 이상의 수여야 합니다. 인자가 없거나 인자를 실수로 변환할 수 없습니다.",scriptName,index);

                    }

                    break;

                case "end":
                case "stop":
                    scriptIndex.put(scriptName, 0);
                    HororoScript.log(scriptName+" 스크립트를 종료합니다.");
                    return;
            }

            // 다음 구문 실행
            executeNextLine(scriptName);

        } else {

            HororoScript.errorLog(scriptName+" 스크립트는 등록되어 있지 않아, 구문 실행을 하지 않았습니다.");

        }

    }

    public static void executeNextLine(String scriptName) {
        execute( scriptName, getCurrentIndex(scriptName) + 1 );
    }

    public static void errorLog(String errorMessage, String scriptName, int index) {
        HororoScript.errorLog(errorMessage+" ("+scriptName+"의 "+index+"번째 줄)");
    }

    /**
     * 현재 진행 중인 스크립트의 인덱스를 반환합니다.
     */
    public static int getCurrentIndex(String scriptName) {
        if(scripts.containsKey(scriptName)) {

            scriptIndex.putIfAbsent(scriptName,0);

            return scriptIndex.get(scriptName);

        } else {
            HororoScript.errorLog("현재 진행 중인 "+scriptName+" 스크립트의 인덱스를 가져오지 못했습니다. (해당 스크립트는 로드되어 있지 않습니다.)");
            return -1;
        }
    }
}
