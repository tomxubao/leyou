package com.leyou.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务启动调用读取配置文件的属性
 * 实现InitializingBean接口
 */
@Component//将此类放到spring容器中
//@ConfigurationProperties(prefix="ali.oss.file") //接收application.yml中的system下面的属性
public class ConstantPropertiesUtils implements InitializingBean {

    //根据名称读取文件的值然后赋值给变量
    @Value("${ali.oss.file.endpoint}")
    private String endpoint;
    @Value("${ali.oss.file.accessKeyId}")
    private String accessKeyId;
    @Value("${ali.oss.file.accessKeySecret}")
    private String accessKeySecret;
    @Value("${ali.oss.file.yourBucketName}")
    private String yourBucketName;

    //为了能够调用定义几个常量
    public static String ENDPOINT;
    public static  String ACCESSKEYID;
    public static String ACCESSKEYSECRET;
    public static  String YOURBUCKETNAME;


    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT =endpoint;
        ACCESSKEYID =accessKeyId;
        ACCESSKEYSECRET = accessKeySecret;
        YOURBUCKETNAME = yourBucketName;
    }

}
