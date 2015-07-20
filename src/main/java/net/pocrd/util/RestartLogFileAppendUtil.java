//package net.pocrd.util;
//
//import net.pocrd.define.ConstField;
//import net.pocrd.entity.CommonConfig;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Calendar;
//
///**
// * api publish后,向tomcat重启日志中打印信息
// */
//public class RestartLogFileAppendUtil {
//    private static final Logger logger                  = LoggerFactory.getLogger(RestartLogFileAppendUtil.class);
//    public static final  String TOMCAT_RESTART_LOG_PATH = isWindows() ? "D:\\usr\\local\\tengine\\html\\restartlog\\log.txt" : "/usr/local/tengine/html/restartlog/log.txt";
//    private static       String lock                    = "lock";
//    public static boolean isWindows() {
//        return System.getProperty("os.name").toLowerCase().contains("windows");
//    }
//    /**
//     * append log
//     *
//     * @param content 输出日志
//     */
//    public static void append(String content, Throwable throwable) {
//        if (CompileConfig.isDebug) {
//            synchronized (lock) {
//                File file = new File(TOMCAT_RESTART_LOG_PATH);
//                if (!file.exists()) {
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        logger.error("create restart log file failed.", e);
//                    }
//                }
//                FileOutputStream outputStream = null;
//                try {
//                    outputStream = new FileOutputStream(file, true);
//                    outputStream.write((Calendar.getInstance().getTime().toString() + " " + content + "\r\n").getBytes(ConstField.UTF8));
//                    if (throwable != null) {
//                        outputStream.write(("exception: " + throwable.getClass().getName() + "\r\n").getBytes(ConstField.UTF8));
//                        if (throwable.getMessage() != null) {
//                            outputStream.write(("msg: " + throwable.getMessage() + "\r\n").getBytes(ConstField.UTF8));
//                        }
//                        for (StackTraceElement element : throwable.getStackTrace()) {
//                            outputStream.write(("stacktrace: " + element.toString() + "\r\n").getBytes(ConstField.UTF8));
//                        }
//                    }
//                } catch (FileNotFoundException e) {
//                    logger.error("output restart log failed.", e);
//                } catch (IOException e) {
//                    logger.error("output restart log failed.", e);
//                } finally {
//                    if (outputStream != null) {
//                        try {
//                            outputStream.close();
//                        } catch (IOException e) {
//                            logger.error("close out put stream failed", e);
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
