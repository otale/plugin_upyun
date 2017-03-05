package com.tale.plugins.upyun;

import com.UpYun;
import com.blade.ioc.annotation.Inject;
import com.blade.mvc.annotation.Intercept;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.interceptor.Interceptor;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.view.RestResponse;
import com.tale.dto.LogActions;
import com.tale.dto.Types;
import com.tale.exception.TipException;
import com.tale.init.TaleConst;
import com.tale.model.Attach;
import com.tale.model.Users;
import com.tale.service.AttachService;
import com.tale.service.LogService;
import com.tale.service.SiteService;
import com.tale.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by biezhi on 2017/3/1.
 */
@Intercept(value = "/admin/.*")
public class YouPaiPlugin implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouPaiPlugin.class);

    @Inject
    private AttachService attachService;

    @Inject
    private SiteService siteService;

    @Inject
    private LogService logService;

    static UpYun upyun = null;

    @Override
    public boolean before(Request request, Response response) {

        boolean isActive = TaleConst.OPTIONS.getBoolean("plugin_upyun_active", false);
        if (!isActive) {
            return true;
        } else {
            if (upyun == null) {
                String bucket = TaleConst.OPTIONS.get("plugin_upyun_bucketname");
                String name = TaleConst.OPTIONS.get("plugin_upyun_operatorname");
                String pass = TaleConst.OPTIONS.get("plugin_upyun_operatorpwd");
                YouPaiPlugin.upyun = new UpYun(bucket, name, pass);
            }
        }

        LOGGER.info("执行又拍云插件");

        String uri = request.uri();

        // 拦截上传接口
        if ("/admin/attach/upload".equals(uri)) {
            Users users = TaleUtils.getLoginUser();
            Integer uid = users.getUid();
            Map<String, FileItem> fileItemMap = request.fileItems();
            Collection<FileItem> fileItems = fileItemMap.values();
            try {
                List<Attach> errorFiles = new ArrayList<>();
                List<Attach> urls = new ArrayList<>();
                fileItems.forEach(f -> {
                    String fname = f.fileName();
                    if (f.file().length() / 1024 <= TaleConst.MAX_FILE_SIZE) {
                        String fkey = TaleUtils.getFileKey(fname);
                        String ftype = TaleUtils.isImage(f.file()) ? Types.IMAGE : Types.FILE;
                        try {
                            // 上传到又拍云
                            boolean result = upyun.writeFile(fkey, f.file(), true);
                            if (result) {
                                Attach attach = attachService.save(fname, fkey, ftype, uid);
                                urls.add(attach);
                            } else {
                                LOGGER.warn("上传文件 [{}] 失败", f.fileName());
                            }
                            f.file().delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        errorFiles.add(new Attach(fname));
                    }
                    siteService.cleanCache(Types.C_STATISTICS);
                });
                if(errorFiles.size() > 0){
                    RestResponse restResponse = new RestResponse();
                    restResponse.setSuccess(false);
                    restResponse.setPayload(errorFiles);
                    response.json(restResponse);
                    return false;
                }
                response.json(RestResponse.ok(urls));
            } catch (Exception e) {
                String msg = "文件上传失败";
                if(e instanceof TipException){
                    msg = e.getMessage();
                } else {
                    LOGGER.error(msg, e);
                }
                response.json(RestResponse.fail(msg));
                return false;
            }
            return false;
        }

        // 删除接口
        if ("/admin/attach/delete".equals(uri)) {
            try {
                Users users = TaleUtils.getLoginUser();
                Integer id = request.queryInt("id");
                Attach attach = attachService.byId(id);
                if (null == attach) {
                    response.json(RestResponse.fail("不存在该附件"));
                    return false;
                }
                attachService.delete(id);
                // 删除文件
                boolean result = upyun.deleteFile(attach.getFkey());
                siteService.cleanCache(Types.C_STATISTICS);
                logService.save(LogActions.DEL_ARTICLE, attach.getFkey(), request.address(), users.getUid());
                if (!result) {
                    response.json(RestResponse.fail("又拍云删除失败"));
                    return false;
                }
                response.json(RestResponse.ok());
            } catch (Exception e) {
                String msg = "附件删除失败";
                if (e instanceof TipException) {
                    msg = e.getMessage();
                } else {
                    LOGGER.error(msg, e);
                }
                response.json(RestResponse.fail(msg));
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean after(Request request, Response response) {
        return true;
    }

}
