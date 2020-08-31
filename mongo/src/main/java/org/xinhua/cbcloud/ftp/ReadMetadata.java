package org.xinhua.cbcloud.ftp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.bson.Document;
import org.xinhua.cbcloud.pojo.ExifPropertiesVO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

public class ReadMetadata {

    public static String exifInfo(String filePath) {
        Document document = new Document();
        if (filePath != null) {
            File jpegFile = new File(filePath);
            if (jpegFile.exists()) {
                try {
                    Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
                    ExifPropertiesVO exifInfo = new ExifPropertiesVO();
                    for (Directory directory : metadata.getDirectories()) {
                        for (Tag tag : directory.getTags()) {
                            switch (tag.getTagName()) {
                                case "Focal Length":
                                    exifInfo.setFocalLength(tag.getDescription());
                                    break;
                                case "White Balance Mode":
                                    exifInfo.setWhiteBalance(tag.getDescription());
                                    break;
                                case "Image Description":
                                case "File Name":
                                    exifInfo.setImageDescription(tag.getDescription());
                                    break;
                                case "Artist":
                                    exifInfo.setArtist(tag.getDescription());
                                    break;
                                case "Make":
                                    exifInfo.setMake(tag.getDescription());
                                    break;
                                case "Model":
                                    exifInfo.setModel(tag.getDescription());
                                    break;
                                case "Orientation":
                                    exifInfo.setOrientation(tag.getDescription());
                                    break;
                                case "X Resolution":
                                    exifInfo.setxResolution(tag.getDescription());
                                    break;
                                case "Y Resolution":
                                    exifInfo.setYresolution(tag.getDescription());
                                    break;
                                case "Resolution Units":
                                case "Resolution Unit":
                                    exifInfo.setResolutionUnit(tag.getDescription());
                                    break;
                                case "Date/Time Original":
                                    exifInfo.setDateTime(tag.getDescription());
                                    break;
                                case "Software":
                                    exifInfo.setSoftware(tag.getDescription());
                                    break;
                                case "YCbCr Positioning":
                                    exifInfo.setYcbCrPositioning(tag.getDescription());
                                    break;
                                case "Exposure Time":
                                    exifInfo.setExposureTime(tag.getDescription());
                                    break;
                                case "F-Number":
                                    exifInfo.setFnumber(tag.getDescription());
                                    break;
                                case "Exposure Program":
                                    exifInfo.setExposureProgram(tag.getDescription());
                                    break;
                                case "ISO Speed Ratings":
                                    exifInfo.setIosspeedRatings(tag.getDescription());
                                    break;
                                case "Exif Image Width":
                                case "Image Width":
                                    exifInfo.setWidth(tag.getDescription().split(" ")[0]);
                                    break;
                                case "Exif Image Height":
                                case "Image Height":
                                    exifInfo.setHeight(tag.getDescription().split(" ")[0]);
                                    break;
                            }
                        }
                    }
                    JSONObject exifJson = JSONObject.parseObject(JSON.toJSONString(exifInfo));
                    if (exifInfo.getWidth() != null && exifInfo.getHeight() != null) {
                        exifJson.put("filePixelWidth", exifInfo.getWidth());
                        exifJson.put("filePixelHeight", exifInfo.getHeight());
                    }
                    exifJson.remove("width");
                    exifJson.remove("height");

                    document.put("exifProperties", exifJson);
                } catch (Exception e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    String exception = baos.toString();
                    System.out.println("提取exif信息异常：{}" + exception);
                }
            } else {
                System.out.println("文件不存在...");
            }
        }

        JSONObject exifProperties = (JSONObject) document.get("exifProperties");
        return exifProperties.getString("dateTime");
    }

    public static void main(String[] args) {
        String dateTime = exifInfo("D:\\tmp\\2f669efefad34d8fba2211c5c62f6eaf.jpg");
        System.out.println("拍摄时间：" + dateTime);
    }
}
