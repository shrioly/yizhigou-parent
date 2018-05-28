package com.yizhigou;

import org.csource.fastdfs.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )throws Exception
    {
      //1.加载配置文件
        ClientGlobal.init("C:\\1711A\\yizhigou-parent\\fastDFSDemo\\src\\main\\resources\\fdfs_client.conf");
        //2.创建TrackClient
        TrackerClient trackerClient = new TrackerClient();
        //创建连接
        TrackerServer trackerServer = trackerClient.getConnection();
        //3.创建Storage
        StorageServer storageServer = null;
        //4.获取连接
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        //5.上传图片
        String[] strings = storageClient.upload_file("D:\\image\\5.jpg", "jpg", null);
        for(String s:strings){
            System.out.println(s);
        }
    }
}
