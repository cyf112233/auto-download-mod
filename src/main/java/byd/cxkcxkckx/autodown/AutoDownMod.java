package byd.cxkcxkckx.autodown;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * 通用初始化器：启动时检查 mods/autodownload.json，并下载缺失的文件到 mods 目录。
 */
public class AutoDownMod implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("autodown");

    // 启动期间是否有需要下载的文件（用于客户端进入游戏后显示提示界面）
    public static volatile boolean downloadsPerformed = false;

    @Override
    public void onInitialize() {
        LOGGER.info("AutoDown: 初始化 - 检查 autodownload.json");
        try {
            checkAndDownload();
        } catch (Throwable t) {
            LOGGER.error("AutoDown: 检查/下载时发生错误", t);
        }
    }

    public static void checkAndDownload() throws Exception {
    Path gameDir = FabricLoader.getInstance().getGameDir();
        Path modsDir = gameDir.resolve("mods");
        Path json = modsDir.resolve("autodownload.json");

        if (!Files.exists(json)) {
            LOGGER.info("AutoDown: 未找到 mods/autodownload.json，跳过。");
            return;
        }

        String raw = new String(Files.readAllBytes(json), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AutoDownloadEntry>>(){}.getType();
        List<AutoDownloadEntry> entries = gson.fromJson(raw, listType);

        boolean hadMissing = false;
        if (entries == null || entries.isEmpty()) {
            LOGGER.info("AutoDown: autodownload.json 为空或格式不正确。");
        } else {
            for (AutoDownloadEntry e : entries) {
                if (e == null || e.getName() == null || e.getUrl() == null) continue;
                Path target = modsDir.resolve(e.getName());
                if (Files.exists(target)) {
                    LOGGER.info("AutoDown: 已存在：{}", e.getName());
                    continue;
                }
                hadMissing = true;
                LOGGER.info("AutoDown: 下载 {} -> {}", e.getUrl(), target);
                try (InputStream in = new URL(e.getUrl()).openStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.info("AutoDown: 下载完成：{}", e.getName());
                } catch (Exception ex) {
                    LOGGER.error("AutoDown: 下载失败：{}", e.getUrl(), ex);
                }
            }
        }

        downloadsPerformed = hadMissing;
        LOGGER.info("AutoDown: 检查完成，downloadsPerformed={}", downloadsPerformed);
    }
}
