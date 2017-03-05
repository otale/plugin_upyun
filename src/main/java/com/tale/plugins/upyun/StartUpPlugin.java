package com.tale.plugins.upyun;

import com.blade.annotation.Order;
import com.blade.config.BConfig;
import com.blade.context.WebContextListener;
import com.tale.dto.PluginMenu;
import com.tale.init.TaleConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * Created by biezhi on 2017/3/1.
 */
@Order(sort = 999)
public class StartUpPlugin implements WebContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpPlugin.class);

    @Override
    public void init(BConfig bConfig, ServletContext sec) {
        LOGGER.info("启动又拍云插件");
        TaleConst.plugin_menus.add(new PluginMenu("又拍云设置", "upyun", null));
    }
}
