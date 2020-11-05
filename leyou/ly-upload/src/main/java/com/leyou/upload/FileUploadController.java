package com.leyou.upload;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.utils.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
/**
 * 上传必须
 * post提交
 * 表单有文件上传项并且name属性<input type="file" name="file"/>
 * 设置form表单的属性 enctype="multipart/form-data"
 */
@RestController
@RequestMapping("upload")
@CrossOrigin
public class FileUploadController {
    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file){
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId ="LTAI4GE6uD2SRy1fZuLmWXP8";
//        String accessKeySecret = "5ZUppGWdJxJ4ZB3l7XGzuCtnBOoqEx";
//        String yourBucketName ="study-xb";
        String endpoint = ConstantPropertiesUtils.ENDPOINT;
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId =ConstantPropertiesUtils.ACCESSKEYID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESSKEYSECRET;
        String yourBucketName =ConstantPropertiesUtils.YOURBUCKETNAME;

        try {
            //获取上传到MultipartFile file
            String filename = file.getOriginalFilename();
            //获取上传文件的名称，获取上传文件输入流
            InputStream inputStream = file.getInputStream();
            //获取当前日期
            String filePath = new DateTime().toString("yyyy/MM/dd");
            //防止名字重复
            filename = filePath+"/"+ UUID.randomUUID().toString()+filename;
            //添加ContentType类型
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpg");
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(yourBucketName,filename,inputStream,objectMetadata);
           //关闭oss
            ossClient.shutdown();
            //https://study-xb.oss-cn-beijing.aliyuncs.com/study/5.jpg
            String url = "https://" + yourBucketName + "." + endpoint + "/" + filename;

            return ResponseEntity.ok(url);

        } catch (IOException e) {
            throw new LyException(ExceptionEnum.OSS_IMAGE_ADD);
        }

    }
}
