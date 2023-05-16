package club.ifcserver.laggremover.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/Updater.class */
public class Updater {
    private static final String TITLE_VALUE = "name";
    private static final String LINK_VALUE = "downloadUrl";
    private static final String TYPE_VALUE = "releaseType";
    private static final String VERSION_VALUE = "gameVersion";
    private static final String QUERY = "/servermods/files?projectIds=";
    private static final String HOST = "https://api.curseforge.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0";
    private static final String DELIMETER = "^v|[\\s_-]v";
    private static final String[] NO_UPDATE_TAG = {"-DEV", "-PRE", "-SNAPSHOT"};
    private static final int BYTE_SIZE = 1024;
    private static final String API_KEY_CONFIG_KEY = "api-key";
    private static final String DISABLE_CONFIG_KEY = "disable";
    private static final String API_KEY_DEFAULT = "PUT_API_KEY_HERE";
    private static final boolean DISABLE_DEFAULT = false;
    private final Plugin plugin;
    private final UpdateType type;
    private final File file;
    private final File updateFolder;
    private final UpdateCallback callback;
    private int id;
    private String apiKey;
    private String versionName;
    private String versionLink;
    private String versionType;
    private String versionGameVersion;
    private String latestVersion;
    private URL url;
    private Thread thread;
    private UpdateResult result;

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/Updater$ReleaseType.class */
    public enum ReleaseType {
        ALPHA,
        BETA,
        RELEASE
    }

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/Updater$UpdateCallback.class */
    public interface UpdateCallback {
        void onFinish(Updater updater);
    }

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/Updater$UpdateResult.class */
    public enum UpdateResult {
        SUCCESS,
        NO_UPDATE,
        DISABLED,
        FAIL_DOWNLOAD,
        FAIL_DBO,
        FAIL_NOVERSION,
        FAIL_BADID,
        FAIL_APIKEY,
        UPDATE_AVAILABLE
    }

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/Updater$UpdateType.class */
    public enum UpdateType {
        DEFAULT,
        NO_VERSION_CHECK,
        NO_DOWNLOAD
    }

    public Updater(Plugin plugin, int id, File file, UpdateType type, boolean announce) {
        this(plugin, id, file, type, null, announce);
    }

    public Updater(Plugin plugin, int id, File file, UpdateType type, UpdateCallback callback) {
        this(plugin, id, file, type, callback, false);
    }

    public Updater(Plugin plugin, int id, File file, UpdateType type, UpdateCallback callback, boolean announce) {
        String message;
        this.id = -1;
        this.apiKey = null;
        this.result = UpdateResult.SUCCESS;
        this.plugin = plugin;
        this.type = type;
        this.file = file;
        this.id = id;
        this.updateFolder = new File(this.plugin.getDataFolder(), "_update");
        this.callback = callback;
        File pluginFile = this.plugin.getDataFolder().getParentFile();
        File updaterFile = new File(pluginFile, "Updater");
        File updaterConfigFile = new File(updaterFile, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )\nIf you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below.\nSome updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
        config.addDefault(API_KEY_CONFIG_KEY, API_KEY_DEFAULT);
        config.addDefault(DISABLE_CONFIG_KEY, false);
        if (!updaterFile.exists()) {
            fileIOOrError(updaterFile, updaterFile.mkdir(), true);
        }
        boolean createFile = !updaterConfigFile.exists();
        try {
            if (createFile) {
                fileIOOrError(updaterConfigFile, updaterConfigFile.createNewFile(), true);
                config.options().copyDefaults(true);
                config.save(updaterConfigFile);
            } else {
                config.load(updaterConfigFile);
            }
        } catch (Exception e) {
            if (createFile) {
                message = "The updater could not create configuration at " + updaterFile.getAbsolutePath();
            } else {
                message = "The updater could not load configuration at " + updaterFile.getAbsolutePath();
            }
            this.plugin.getLogger().log(Level.SEVERE, message, (Throwable) e);
        }
        if (config.getBoolean(DISABLE_CONFIG_KEY)) {
            this.result = UpdateResult.DISABLED;
            return;
        }
        String key = config.getString(API_KEY_CONFIG_KEY);
        this.apiKey = (API_KEY_DEFAULT.equalsIgnoreCase(key) || "".equals(key)) ? DISABLE_DEFAULT : key;
        try {
            this.url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + this.id);
        } catch (MalformedURLException e2) {
            this.plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + this.id + " is invalid.", (Throwable) e2);
            this.result = UpdateResult.FAIL_BADID;
        }
        if (this.result != UpdateResult.FAIL_BADID) {
            this.thread = new Thread(new UpdateRunnable());
            this.thread.start();
            return;
        }
        runUpdater();
    }

    public UpdateResult getResult() {
        waitForThread();
        return this.result;
    }

    public ReleaseType getLatestType() {
        waitForThread();
        if (this.versionType != null) {
            ReleaseType[] values = ReleaseType.values();
            int length = values.length;
            for (int i = DISABLE_DEFAULT; i < length; i++) {
                ReleaseType type = values[i];
                if (this.versionType.equalsIgnoreCase(type.name())) {
                    return type;
                }
            }
            return null;
        }
        return null;
    }

    public String getLatestGameVersion() {
        waitForThread();
        return this.versionGameVersion;
    }

    public String getLatestName() {
        waitForThread();
        return this.versionName;
    }

    public String getLatestFileLink() {
        waitForThread();
        return this.versionLink;
    }

    private void waitForThread() {
        if (this.thread != null && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, (String) null, (Throwable) e);
            }
        }
    }

    private void saveFile(String file) {
        File folder = this.updateFolder;
        deleteOldFiles();
        if (!folder.exists()) {
            fileIOOrError(folder, folder.mkdir(), true);
        }
        downloadFile();
        File dFile = new File(folder.getAbsolutePath(), file);
        if (dFile.getName().endsWith(".zip")) {
            unzip(dFile.getAbsolutePath());
        }
        this.plugin.getLogger().info("Update downloaded! Your version: " + this.plugin.getDescription().getVersion() + " Latest version: " + this.latestVersion);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lr.update")) {
                p.sendMessage(LaggRemover.prefix + "§cUpdate downloaded! Your version: §e" + this.plugin.getDescription().getVersion() + "§c Latest version: §e" + this.latestVersion);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x0136 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00f5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void downloadFile() {
        /*
            Method dump skipped, instructions count: 341
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: drew6017.lr.main.Updater.downloadFile():void");
    }

    private void deleteOldFiles() {
        File[] list = listFilesOrError(this.updateFolder);
        int length = list.length;
        for (int i = DISABLE_DEFAULT; i < length; i++) {
            File xFile = list[i];
            if (xFile.getName().endsWith(".zip")) {
                fileIOOrError(xFile, xFile.mkdir(), true);
            }
        }
    }

    private void unzip(String file) {
        File fSourceZip = new File(file);
        try {
            try {
                String zipPath = file.substring(DISABLE_DEFAULT, file.length() - 4);
                ZipFile zipFile = new ZipFile(fSourceZip);
                Enumeration<? extends ZipEntry> e = zipFile.entries();
                while (e.hasMoreElements()) {
                    ZipEntry entry = e.nextElement();
                    File destinationFilePath = new File(zipPath, entry.getName());
                    fileIOOrError(destinationFilePath.getParentFile(), destinationFilePath.getParentFile().mkdirs(), true);
                    if (!entry.isDirectory()) {
                        BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                        byte[] buffer = new byte[BYTE_SIZE];
                        FileOutputStream fos = new FileOutputStream(destinationFilePath);
                        BufferedOutputStream bos = new BufferedOutputStream(fos, BYTE_SIZE);
                        while (true) {
                            int b = bis.read(buffer, DISABLE_DEFAULT, BYTE_SIZE);
                            if (b == -1) {
                                break;
                            }
                            bos.write(buffer, DISABLE_DEFAULT, b);
                        }
                        bos.flush();
                        bos.close();
                        bis.close();
                        String name = destinationFilePath.getName();
                        if (name.endsWith(".jar") && pluginExists(name)) {
                            File output = new File(this.updateFolder, name);
                            fileIOOrError(output, destinationFilePath.renameTo(output), true);
                        }
                    }
                }
                zipFile.close();
                moveNewZipFiles(zipPath);
                fileIOOrError(fSourceZip, fSourceZip.delete(), false);
            } catch (IOException e2) {
                this.plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", (Throwable) e2);
                this.result = UpdateResult.FAIL_DOWNLOAD;
                fileIOOrError(fSourceZip, fSourceZip.delete(), false);
            }
        } catch (Throwable th) {
            fileIOOrError(fSourceZip, fSourceZip.delete(), false);
            throw th;
        }
    }

    private void moveNewZipFiles(String zipPath) {
        File[] list = listFilesOrError(new File(zipPath));
        int length = list.length;
        for (int i = DISABLE_DEFAULT; i < length; i++) {
            File dFile = list[i];
            if (dFile.isDirectory() && pluginExists(dFile.getName())) {
                File oFile = new File(this.plugin.getDataFolder().getParent(), dFile.getName());
                File[] dList = listFilesOrError(dFile);
                File[] oList = listFilesOrError(oFile);
                int length2 = dList.length;
                for (int i2 = DISABLE_DEFAULT; i2 < length2; i2++) {
                    File cFile = dList[i2];
                    boolean found = DISABLE_DEFAULT;
                    int length3 = oList.length;
                    int i3 = DISABLE_DEFAULT;
                    while (true) {
                        if (i3 >= length3) {
                            break;
                        }
                        File xFile = oList[i3];
                        if (!xFile.getName().equals(cFile.getName())) {
                            i3++;
                        } else {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        File output = new File(oFile, cFile.getName());
                        fileIOOrError(output, cFile.renameTo(output), true);
                    } else {
                        fileIOOrError(cFile, cFile.delete(), false);
                    }
                }
            }
            fileIOOrError(dFile, dFile.delete(), false);
        }
        File zip = new File(zipPath);
        fileIOOrError(zip, zip.delete(), false);
    }

    private boolean pluginExists(String name) {
        File[] plugins = listFilesOrError(new File("plugins"));
        int length = plugins.length;
        for (int i = DISABLE_DEFAULT; i < length; i++) {
            File file = plugins[i];
            if (file.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean versionCheck() {
        String title = this.versionName;
        if (this.type != UpdateType.NO_VERSION_CHECK) {
            String localVersion = this.plugin.getDescription().getVersion();
            if (title.split("-").length == 2) {
                String remoteVersion = title.split("-")[1];
                this.latestVersion = remoteVersion;
                if (hasTag(localVersion) || !shouldUpdate(localVersion, remoteVersion)) {
                    this.result = UpdateResult.NO_UPDATE;
                    return false;
                }
                return true;
            }
            String authorInfo = this.plugin.getDescription().getAuthors().isEmpty() ? "" : " (" + ((String) this.plugin.getDescription().getAuthors().get(DISABLE_DEFAULT)) + ")";
            this.plugin.getLogger().warning("The author of this plugin" + authorInfo + " has misconfigured their Auto Update system");
            this.plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
            this.plugin.getLogger().warning("Please notify the author of this error.");
            this.result = UpdateResult.FAIL_NOVERSION;
            return false;
        }
        return true;
    }

    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return !localVersion.equalsIgnoreCase(remoteVersion);
    }

    private boolean hasTag(String version) {
        String[] strArr = NO_UPDATE_TAG;
        int length = strArr.length;
        for (int i = DISABLE_DEFAULT; i < length; i++) {
            String string = strArr[i];
            if (version.contains(string)) {
                return true;
            }
        }
        return false;
    }

    private boolean read() {
        try {
            URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);
            if (this.apiKey != null) {
                conn.addRequestProperty("X-API-Key", this.apiKey);
            }
            conn.addRequestProperty("User-Agent", USER_AGENT);
            conn.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            JSONArray array = (JSONArray) JSONValue.parse(response);
            if (array.isEmpty()) {
                this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }
            JSONObject latestUpdate = (JSONObject) array.get(array.size() - 1);
            this.versionName = (String) latestUpdate.get(TITLE_VALUE);
            this.versionLink = (String) latestUpdate.get(LINK_VALUE);
            this.versionType = (String) latestUpdate.get(TYPE_VALUE);
            this.versionGameVersion = (String) latestUpdate.get(VERSION_VALUE);
            return true;
        } catch (IOException | NullPointerException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                this.plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
                this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
                this.result = UpdateResult.FAIL_APIKEY;
            } else {
                this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
                this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
                this.result = UpdateResult.FAIL_DBO;
            }
            this.plugin.getLogger().log(Level.SEVERE, (String) null, (Throwable) e);
            return false;
        }
    }

    private void fileIOOrError(File file, boolean result, boolean create) {
        if (!result) {
            this.plugin.getLogger().severe("The updater could not " + (create ? "create" : "delete") + " file at: " + file.getAbsolutePath());
        }
    }

    private File[] listFilesOrError(File folder) {
        File[] contents = folder.listFiles();
        if (contents == null) {
            this.plugin.getLogger().severe("The updater could not access files at: " + this.updateFolder.getAbsolutePath());
            return new File[DISABLE_DEFAULT];
        }
        return contents;
    }

    /* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/Updater$UpdateRunnable.class */
    private class UpdateRunnable implements Runnable {
        private UpdateRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Updater.this.runUpdater();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v4, types: [drew6017.lr.main.Updater$1] */
    public void runUpdater() {
        if (this.url != null && read() && versionCheck()) {
            if (this.versionLink != null && this.type != UpdateType.NO_DOWNLOAD) {
                String name = this.file.getName();
                if (this.versionLink.endsWith(".zip")) {
                    name = this.versionLink.substring(this.versionLink.lastIndexOf("/") + 1);
                }
                saveFile(name);
            } else {
                this.result = UpdateResult.UPDATE_AVAILABLE;
            }
        }
        if (this.callback != null) {
            new BukkitRunnable() { // from class: drew6017.lr.main.Updater.1
                public void run() {
                    Updater.this.runCallback();
                }
            }.runTask(this.plugin);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runCallback() {
        this.callback.onFinish(this);
    }
}
