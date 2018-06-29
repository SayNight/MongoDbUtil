package com.saynight.mongodb;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;
 
/**
 * @Title RequestData.java
 * @Description TODO
 * @author saynight
 * @date 2015年12月29日 下午4:06:14
 * @version V1.0  
 */
public class RequestData {
	/**
	 * Mongodb主键  id类型不可改
	 */
	
	private ObjectId id;
	
	private String cardNo;
	
	private String cardPwd;
	
	private Date firstDate;
	//转换为toDocument 在新增和修改时都需要转换  新增和修改的对象都是Document
	
	public Document toDocument(RequestData requestData){
		Document document =  new Document();
		document.put("cardNo", requestData.getCardNo());
		document.put("cardPwd", requestData.getCardPwd());
		document.put("firstDate", requestData.getFirstDate());
		return document;
	}
	//转换为实体bean  查询结果为Document  若想用该bean则需要转换
	public void toRequestData(Document document){
		this.setId(document.getObjectId("_id"));
		this.setCardNo(document.getString("cardNo"));
		this.setCardPwd(document.getString("cardPwd"));
		this.setFirstDate(document.getDate("firstDate"));
	}
 
	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}
 
	/**
	 * @param id the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
	}
 
	/**
	 * @return the cardNo
	 */
	public String getCardNo() {
		return cardNo;
	}
 
	/**
	 * @param cardNo the cardNo to set
	 */
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
 
	/**
	 * @return the cardPwd
	 */
	public String getCardPwd() {
		return cardPwd;
	}
 
	/**
	 * @param cardPwd the cardPwd to set
	 */
	public void setCardPwd(String cardPwd) {
		this.cardPwd = cardPwd;
	}
 
	/**
	 * @return the firstDate
	 */
	public Date getFirstDate() {
		return firstDate;
	}
 
	/**
	 * @param firstDate the firstDate to set
	 */
	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}
	
	
	
}
