package com.upyun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class CompressHandler extends AsyncProcessHandler {


    /**
     * 初始压缩解压缩接口
     *
     * @param bucketName 空间名称
     * @param userName   操作员名称
     * @param password   密码，不需要MD5加密
     */
    public CompressHandler(String bucketName, String userName, String password) {
        super(bucketName, userName, password);
    }

    /**
     * 发起压缩解压缩处理请求
     *
     * @param params 请求参数
     * @return 请求结果
     * @throws IOException
     */
    public Result process(Map<String, Object> params) throws IOException {

        return super.process(params);

    }


    public class Params {
        /**
         * 请求参数
         * <p>
         * bucket_name	string	是	文件所在空间名称
         * notify_url	string	是	回调通知地址
         * tasks	string	是	处理任务信息，详见下
         * app_name	string	是	任务所使用的云处理程序，压缩打包为 compress，解压为 depress
         */
        public final static String BUCKET_NAME = "bucket_name";
        public final static String NOTIFY_URL = "notify_url";
        public final static String TASKS = "tasks";
        public final static String APP_NAME = "app_name";


        /**
         * 回调通知参数
         * <p>
         * task_id	string	任务对应的 TaskId
         * status_code	integer	处理结果状态码，200 表示成功处理
         * path	string	输出文件保存路径
         * error	string	处理错误信息描述，空字符串表示没有错误
         */
        public final static String TASK_ID = "task_id";
        public final static String STATUS_CODE = "status_code";
        public final static String PATH = "path";
        public final static String ERROR = "error";

        /**
         * 压缩
         * <p>
         * save_as	string	压缩文件保存路径（需要为 zip 压缩文件），如 </result/t.zip>
         * sources	array	需要被压缩打包的文件或目录路径（空间内相对路径）
         * home_dir	string	压缩文件内的目录结构可不包含的父目录。默认包含从根开始的全部目录。可选参数
         */
        public final static String SAVE_AS = "save_as";
        public final static String SOURCES = "sources";
        public final static String HOME_DIR = "home_dir";


        /**
         * 解压缩
         *
         * save_as	string	压缩文件保存路径（需要为目录），如 </result/>
         * sources	string	空间内压缩文件相对路径
         */
//        public final static String SAVE_AS = "save_as";
//        public final static String SOURCES = "sources";

    }
}
