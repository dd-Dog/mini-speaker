package flyscale.monitor.global;

public class Constants {

    public class BatteryInfo {
        public static final String BATTERY_LEVEL = "BatteryLevel";
        public static final String PLUG_TYPE = "PlugType";
        public static final String BATTERY_STATUS = "BatteryStatus";
        public static final String BATTERY_HEALTH = "BatteryHealth";
        public static final String BATTERY_VOLTAGE = "BatteryVoltage";
        public static final String BATTERY_TEMPERATURE = "BatteryTemperature";
    }

    public class ProductInfo {
        public static final int PRODUCT_ID = 10001;
        public static final String PRODUCT_SECRET = "6a5c02c7d9bd42cf8d8317e59d2af997";
    }

    public class LogInfo {
        /**
         * 内部存储SLOG路径
         */
        public static final String INNER_SLOG_PATH = "/data/slog";
        /**
         * 压缩后的SLOG存储目录
         */
        public static final String INNER_ZIPPED_SLOG_DIR = "/data/";

        /**
         * 有外置SD卡的SLOG存储路径
         */
        public static final String EXTERNAL_SLOG_PATH = "/storage/sdcard0/slog";
        /**
         * 有外置SD卡的压缩后的SLOG存储路径
         */
        public static final String EXTERNAL_ZIIPED_SLOG_DIR = "/storage/sdcard0/";

        /**
         * 服务器向终端推送文件时的下载路径
         */
        public static final String DOWNLOAD_LOCAL_DIR = "/mnt/sdcard/flyscale/download/";
    }

    public class Url {
        public static final String BASE_URL_TEST = "http://192.168.1.130:8888/";
        public static final String BASE_URL_NORMAL = "http://124.71.115.4:8888/";
        public static final String LOG_UPLOAD_URL = BASE_URL_NORMAL + "log/upload";
    }

    public class Settings {
        public static final int HEART_MSG_INTERVEL = 10 * 60 * 1000;
        public static final String HEART_MSG_NAME = "mqtt_heart";
        public static final int HEART_VALUE = 1;
    }


    public class LocationInfo{
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String SPEED = "speed";
        public static final String BEARING = "bearing";
        public static final String PROVIDER = "provider";
    }
}
