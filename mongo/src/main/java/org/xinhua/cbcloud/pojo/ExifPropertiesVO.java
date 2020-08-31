package org.xinhua.cbcloud.pojo;

public class ExifPropertiesVO {

	//@ApiModelProperty(value = "图像描述")
	private String imageDescription;

	//@ApiModelProperty(value = "作者")
	private String artist;

	//@ApiModelProperty(value = "生产者")
	private String make;

	//@ApiModelProperty(value = "设备型号")
	private String model;

	//@ApiModelProperty(value = "方向")
	private String orientation;

	//@ApiModelProperty(value = "X方向分辨率")
	private String xResolution;

	//@ApiModelProperty(value = "Y方向分辨率")
	private String yresolution;

	//@ApiModelProperty(value = "分辨率单位")
	private String resolutionUnit;

	//@ApiModelProperty(value = "软件版本")
	private String software;

	//@ApiModelProperty(value = "日期和时间")
	private String dateTime;

	//@ApiModelProperty(value = "色相定位")
	private String ycbCrPositioning;

	//@ApiModelProperty(value = "曝光时间")
	private String exposureTime;

	//@ApiModelProperty(value = "光圈系数")
	private String fnumber;

	//@ApiModelProperty(value = "曝光程序")
	private String exposureProgram;

	//@ApiModelProperty(value = "感光度")
	private String iosspeedRatings;

	//@ApiModelProperty(value = "高度/像素")
	private String height;

	//@ApiModelProperty(value = "宽度/像素")
	private String width;

	//@ApiModelProperty(value = "白平衡")
	private String whiteBalance;
	//@ApiModelProperty(value = "焦距")
	private String focalLength;

	public String getImageDescription() {
		return imageDescription;
	}

	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getxResolution() {
		return xResolution;
	}

	public void setxResolution(String xResolution) {
		this.xResolution = xResolution;
	}

	public String getYresolution() {
		return yresolution;
	}

	public void setYresolution(String yresolution) {
		this.yresolution = yresolution;
	}

	public String getResolutionUnit() {
		return resolutionUnit;
	}

	public void setResolutionUnit(String resolutionUnit) {
		this.resolutionUnit = resolutionUnit;
	}

	public String getSoftware() {
		return software;
	}

	public void setSoftware(String software) {
		this.software = software;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getYcbCrPositioning() {
		return ycbCrPositioning;
	}

	public void setYcbCrPositioning(String ycbCrPositioning) {
		this.ycbCrPositioning = ycbCrPositioning;
	}

	public String getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(String exposureTime) {
		this.exposureTime = exposureTime;
	}

	public String getFnumber() {
		return fnumber;
	}

	public void setFnumber(String fnumber) {
		this.fnumber = fnumber;
	}

	public String getExposureProgram() {
		return exposureProgram;
	}

	public void setExposureProgram(String exposureProgram) {
		this.exposureProgram = exposureProgram;
	}

	public String getIosspeedRatings() {
		return iosspeedRatings;
	}

	public void setIosspeedRatings(String iosspeedRatings) {
		this.iosspeedRatings = iosspeedRatings;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getWhiteBalance() {
		return whiteBalance;
	}

	public void setWhiteBalance(String whiteBalance) {
		this.whiteBalance = whiteBalance;
	}

	public String getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(String focalLength) {
		this.focalLength = focalLength;
	}

	@Override
	public String toString() {
		return "ExifProperties [" + (imageDescription != null ? "imageDescription=" + imageDescription + ", " : "")
				+ (artist != null ? "artist=" + artist + ", " : "") + (make != null ? "make=" + make + ", " : "")
				+ (model != null ? "model=" + model + ", " : "")
				+ (orientation != null ? "orientation=" + orientation + ", " : "")
				+ (xResolution != null ? "xResolution=" + xResolution + ", " : "")
				+ (yresolution != null ? "yresolution=" + yresolution + ", " : "")
				+ (resolutionUnit != null ? "resolutionUnit=" + resolutionUnit + ", " : "")
				+ (software != null ? "software=" + software + ", " : "")
				+ (dateTime != null ? "dateTime=" + dateTime + ", " : "")
				+ (ycbCrPositioning != null ? "ycbCrPositioning=" + ycbCrPositioning + ", " : "")
				+ (exposureTime != null ? "exposureTime=" + exposureTime + ", " : "")
				+ (fnumber != null ? "fnumber=" + fnumber + ", " : "")
				+ (exposureProgram != null ? "exposureProgram=" + exposureProgram + ", " : "")
				+ (iosspeedRatings != null ? "iosspeedRatings=" + iosspeedRatings : "") + "]";
	}

}
