package com.googleSearch.test_googleSearch;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

public class FindResult implements Serializable {
	
	private String name = null;
	
	private String title = null;
	private String imageURL = null;
	private String siteURL = null;
	private String thumbnailURL = null;
	private Integer width = null;
	private Integer height = null;
	private String id = null;
	private String siteDomain = null;
	private Integer itg = null;
	private String type = null;
	private Integer imageHeight = null;
	private Integer imageWidth = null;
	private String description = null;
	private String rid = null;
	private Integer rmt = null;
	private Integer sc = null;
	private Integer thumbnailHeight = null;
	private String siteName = null;
	private Integer thumbnailWidth = null;
	private BufferedImage image = null;
	private BufferedImage thumbnailImage = null;

	public FindResult() { super(); }
	
	public FindResult(JsonNode node) {
		id = getStringParam(node,"id");
		siteDomain = getStringParam(node,"isu");
		itg = getIntParam(node,"itg");
		type = getStringParam(node,"ity");
		imageHeight = getIntParam(node,"oh");
		imageURL = getStringParam(node,"ou");
		imageWidth = getIntParam(node,"ow");
		description = getStringParam(node,"pt");
		rid = getStringParam(node,"rid");
		rmt = getIntParam(node,"rmt");
		rid = getStringParam(node,"rid");
		siteURL = getStringParam(node,"ru");
		title = getStringParam(node,"s");
		sc = getIntParam(node,"sc");
		siteName = getStringParam(node,"st");
		thumbnailHeight = getIntParam(node,"th");
		thumbnailURL = getStringParam(node,"tu");
		thumbnailWidth = getIntParam(node,"tw");
	}

	
	public String toString() {
		String result = null;
		result = "title:" + title;
		result += "\ndescription:" + title;
		result += "\nsize(type):[" + imageWidth + "x" + imageHeight + "](" + type + ")";
		result += "\nimageURL:" + imageURL;
		
		return result;
	}
	
	private Integer getIntParam(JsonNode node, String param) {
		Integer result = null;
		JsonNode jSonNode = node.get(param);
		if (jSonNode!=null) {
			result = jSonNode.asInt();
		}
		return result;
	}

	private String getStringParam(JsonNode node, String param) {
		String result = null;
		JsonNode jSonNode = node.get(param);
		if (jSonNode!=null) {
			result = jSonNode.toString();
			if (result.length()>0) {
				if (result.substring(0,1).equals("\"")) {
					result=result.substring(1,result.length());
				}
				if (result.substring(result.length()-1,result.length()).equals("\"")) {
					result=result.substring(0,result.length()-1);
				}
			}
		}
		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSiteDomain() {
		return siteDomain;
	}

	public void setSiteDomain(String siteDomain) {
		this.siteDomain = siteDomain;
	}

	public Integer getItg() {
		return itg;
	}

	public void setItg(Integer itg) {
		this.itg = itg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Integer getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public Integer getRmt() {
		return rmt;
	}

	public void setRmt(Integer rmt) {
		this.rmt = rmt;
	}

	public Integer getSc() {
		return sc;
	}

	public void setSc(Integer sc) {
		this.sc = sc;
	}

	public Integer getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(Integer thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Integer getThumbnailWidth() {
		return thumbnailWidth;
	}

	public void setThumbnailWidth(Integer thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getSiteURL() {
		return siteURL;
	}
	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}
	public String getThumbnailURL() {
		return thumbnailURL;
	}
	public void setThumbnailURL(String thumnailURL) {
		this.thumbnailURL = thumnailURL;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public Image getThumbnailImage() {
		return thumbnailImage;
	}
	public void setThumbnailImage(BufferedImage thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
