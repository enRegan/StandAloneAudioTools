package com.regan.saata.util;
/**
 * HistoryUser: regan
 * Date: 2019-06-09
 * Time: 10:16
 * 描述一下这个类吧
 */

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;

/**
 * Created by apple on 16/6/9.
 */
public class LogUtils {
    public static final boolean DEBUG = true;
    //    public static final boolean DEBUG = false;
    public static final boolean WRITE_TO_FILE = true;
    //    public static final boolean WRITE_TO_FILE = false;
//    private static Logger log = Logger.getLogger("");

    private static Handler handler = null;
    public static final int maxBackupIndex = 5;
    private static String logFilePath = "/mnt/sdcard/audiotools/log";

    static {
        HandlerThread thread = new HandlerThread("HandlerThread");
        thread.start();
        handler = new Handler(thread.getLooper());
//        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                .showThreadInfo(false)  // 是否显示线程信息，默认为ture
//                .methodCount(0)         // 显示的方法行数，默认为2
//                .methodOffset(7)        // 隐藏内部方法调用到偏移量，默认为5
//                .logStrategy(new DiskLogStrategy(handler)) // 更改要打印的日志策略。
//                .tag("LogUtils")   // 每个日志的全局标记。默认PRETTY_LOGGER
//                .build();
//        FormatStrategy pformatStrategy = PrettyFormatStrategy.newBuilder()
//                .tag("custom")
//                .build();
//        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        Logger.addLogAdapter(new DiskLogAdapter(CustomStrategy2.newBuilder().tag("customtag").build()));
    }

    public static void setLogFilePath(String logFilePath) {
        LogUtils.logFilePath = logFilePath;
    }

    static class CustomStrategy2 implements FormatStrategy {

        private static final String NEW_LINE = System.getProperty("line.separator");
        //        private static final String NEW_LINE_REPLACEMENT = " <br> ";
        private static final String SEPARATOR = ",";

        private final Date date;
        private final SimpleDateFormat dateFormat;
        private final LogStrategy logStrategy;
        private final String tag;

        private CustomStrategy2(Builder builder) {
            date = builder.date;
            dateFormat = builder.dateFormat;
            logStrategy = builder.logStrategy;
            tag = builder.tag;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        @Override
        public void log(int priority, String onceOnlyTag, String message) {
            String tag = formatTag(onceOnlyTag);

            date.setTime(System.currentTimeMillis());

            StringBuilder builder = new StringBuilder();

            // machine-readable date/time
//            builder.append(Long.toString(date.getTime()));

            // human-readable date/time
//            builder.append(SEPARATOR);
            builder.append(dateFormat.format(date));

            // level
            builder.append(SEPARATOR);
            builder.append(logLevel(priority));

            // tag
//            builder.append(SEPARATOR);
//            builder.append(tag);

            // message
            if (message.contains(NEW_LINE)) {
                // a new line would break the CSV format, so we replace it here
//                message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
            }
            builder.append(SEPARATOR);
            builder.append(message);

            // new line
            builder.append(NEW_LINE);

            logStrategy.log(priority, tag, builder.toString());
        }

        static String logLevel(int value) {
            switch (value) {
                case VERBOSE:
                    return "VERBOSE";
                case Logger.DEBUG:
                    return "DEBUG";
                case INFO:
                    return "INFO";
                case WARN:
                    return "WARN";
                case ERROR:
                    return "ERROR";
                case ASSERT:
                    return "ASSERT";
                default:
                    return "UNKNOWN";
            }
        }

        private String formatTag(String tag) {
//            if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            if (tag != null && !tag.equals("") && !tag.equals(this.tag)) {
                return this.tag + "-" + tag;
            }
            return this.tag;
        }

        public static final class Builder {
            private static final int MAX_BYTES = 5 * 1024 * 1024; // 500K averages to a 4000 lines per file

            Date date;
            SimpleDateFormat dateFormat;
            LogStrategy logStrategy;
            String tag = "PRETTY_LOGGER";

            private Builder() {
            }

            public Builder date(Date val) {
                date = val;
                return this;
            }

            public Builder dateFormat(SimpleDateFormat val) {
                dateFormat = val;
                return this;
            }

            public Builder logStrategy(LogStrategy val) {
                logStrategy = val;
                return this;
            }

            public Builder tag(String tag) {
                this.tag = tag;
                return this;
            }

            public CustomStrategy2 build() {
                if (date == null) {
                    date = new Date();
                }
                if (dateFormat == null) {
                    dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS"/*, Locale.UK*/);
                }
                if (logStrategy == null) {
                    String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String folder = LogUtils.logFilePath;

                    HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
                    ht.start();
                    Handler handler = new CustomLogStrategy.WriteHandler(ht.getLooper(), folder, MAX_BYTES);
                    logStrategy = new CustomLogStrategy(handler);
                }
                return new CustomStrategy2(this);
            }
        }
    }

    static class CustomLogStrategy implements LogStrategy {

        private final Handler handler;

        public CustomLogStrategy(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void log(int level, String tag, String message) {
            // do nothing on the calling thread, simply pass the tag/msg to the background thread
            handler.sendMessage(handler.obtainMessage(level, message));
        }

        static class WriteHandler extends Handler {

            private final String folder;
            private final int maxFileSize;

            WriteHandler(Looper looper, String folder, int maxFileSize) {
                super(looper);
                this.folder = folder;
                this.maxFileSize = maxFileSize;
            }

            @SuppressWarnings("checkstyle:emptyblock")
            @Override
            public void handleMessage(Message msg) {
                String content = (String) msg.obj;

                FileWriter fileWriter = null;
                File logFile = getLogFile2(folder, "audiotools");

                try {
                    fileWriter = new FileWriter(logFile, true);

                    writeLog(fileWriter, content);

                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    if (fileWriter != null) {
                        try {
                            fileWriter.flush();
                            fileWriter.close();
                        } catch (IOException e1) { /* fail silently */ }
                    }
                }
            }

            /**
             * This is always called on a single background thread.
             * Implementing classes must ONLY write to the fileWriter and nothing more.
             * The abstract class takes care of everything else including close the stream and catching IOException
             *
             * @param fileWriter an instance of FileWriter already initialised to the correct file
             */
            private void writeLog(FileWriter fileWriter, String content) throws IOException {
                fileWriter.append(content);
            }

            private File getLogFile(String folderName, String fileName) {

                File folder = new File(folderName);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                int newFileCount = 0;
                File newFile;
                File existingFile = null;

                newFile = new File(folder, String.format("%s.%s", fileName, newFileCount));
                while (newFile.exists()) {
                    existingFile = newFile;
                    newFileCount++;
                    newFile = new File(folder, String.format("%s.%s", fileName, newFileCount));
                }

                if (existingFile != null) {
                    if (existingFile.length() >= maxFileSize) {
                        return newFile;
                    }
                    return existingFile;
                }

                return newFile;
            }

            private String getFileName(String fileName, int suffix) {
                if (suffix == 0) {
                    return fileName;
                }
                return String.format("%s.%s", fileName, suffix);
            }

            private File getLogFile2(String folderName, String fileName) {

                int newFileCount = 0;
                File newFile;
                File existingFile = null;

                File folder = new File(folderName);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                existingFile = new File(folder, getFileName(fileName, 0));
                if (existingFile != null) {
                    if (existingFile.length() >= maxFileSize) {
                        //移位，数字加1
                        renameLog(folderName, fileName);

                        existingFile.delete();
                    }
                }


//                newFile = new File(folder, String.format("%s.%s", fileName, newFileCount));
//                while (newFile.exists()) {
//                    existingFile = newFile;
//                    newFileCount++;
//                    newFile = new File(folder, String.format("%s.%s", fileName, newFileCount));
//                }
//                newFileCount -= 1;
//                if (newFileCount < 0) {
//                    newFileCount = 0;
//                }
//                existingFile = new File(folder, String.format("%s.%s", fileName, newFileCount));

                return existingFile;
            }

            private void renameLog(String folderName, String fileName) {
                File file;
                File target;

//                file = new File(folderName, String.format("%s.%s", fileName, maxBackupIndex - 1));
                file = new File(folderName, getFileName(fileName, maxBackupIndex - 1));
                if (file.exists()) {
                    file.delete();
                }
                file = new File(folderName, getFileName(fileName, maxBackupIndex - 1));
                if (file.exists()) {
                    file.delete();
                }

                for (int i = maxBackupIndex - 1; i >= 0; --i) {
                    file = new File(folderName, getFileName(fileName, i));
                    if (file.exists()) {
                        target = new File(folderName, getFileName(fileName, i + 1));
                        file.renameTo(target);
                    }
                }
            }
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            if (WRITE_TO_FILE) {
//                log.log(Level.ALL, "{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
                Logger.v("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
            }
            Log.v("{" + Thread.currentThread().getId() + "} " + tag, msg);

        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            if (WRITE_TO_FILE) {
//                log.debug("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
                Logger.d("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
            }
            Log.d("{" + Thread.currentThread().getId() + "} " + tag, msg);

        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            if (WRITE_TO_FILE) {
//                log.info("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
                Logger.i("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
            }
            Log.i("{" + Thread.currentThread().getId() + "} " + tag, msg);
        }
    }

    public static void i(String tag, String msg, boolean writeToFile) {
        if (DEBUG) {
            if (writeToFile) {
//                log.info("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
                Logger.i("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
            }
            Log.i("{" + Thread.currentThread().getId() + "} " + tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            if (WRITE_TO_FILE) {
//                log.warn("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
                Logger.w("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
            }
            Log.w("{" + Thread.currentThread().getId() + "} " + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            if (WRITE_TO_FILE) {
//                log.error("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
                Logger.e("{" + Thread.currentThread().getId() + "} " + tag + "::" + msg);
            }
            Log.e("{" + Thread.currentThread().getId() + "} " + tag, msg);
        }
    }
}
