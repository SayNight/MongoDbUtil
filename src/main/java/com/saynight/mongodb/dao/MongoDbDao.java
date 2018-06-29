package com.saynight.mongodb.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
 
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
 
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.saynight.mongodb.enums.MongoOperation;
 
/**
 * @Title MongoDbDao.java
 * @Description MongoDb Dao层服务类
 * @author saynight
 * @date 2015年12月30日 上午10:51:06
 * @version V1.0
 */
public enum MongoDbDao {
	/**
	 * 定义一个枚举的元素，它代表此类的一个实例
	 */
	INSTANCE;
 
	private MongoClient mongoClient;
	/**
	 * 指定默认数据库
	 */
	private static String DEFAULTDBNAME;
	/**
	 * 获取tomcat路径
	 */
	private static String LOADPATH = "catalina.home";
	/**
	 * mongodb配置文件相对路径
	 */
	private static String CONFIG_PATH = "commoncfg/dbconf/mongodb.properties";// tomcat 环境地址 catalina.home 值/Users/night/Documents/apache-tomcat-7.0
	
	static {
 
		System.out.println("MongoDb初始化＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝Begin");
		// 获取系统变量
		Properties sysProperties = System.getProperties();
		// 获取MongoDb配置路径
		String uri = sysProperties.getProperty(LOADPATH) + File.separator + CONFIG_PATH;
		System.out.println(uri);
		// 加载配置路径
		Properties properties = new Properties();
//		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(uri);//这种方式按上述路径无法加载到文件
		InputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(uri);
			System.out.println(inputStream);
			properties.load(inputStream);
			
			for(Iterator<Object> itr = properties.keySet().iterator();itr.hasNext();){  
	            String key = (String) itr.next();  
	            System.out.println(key + "=" + properties.get(key));  
	        }  
			
			/* 将配置文件中的配置进行对应设置 */
 
			DEFAULTDBNAME = properties.getProperty("mongo.databaseName");
 
			Builder builder = new MongoClientOptions.Builder();
			String perHost = properties.getProperty("mongo.connectionsPerHost");
			builder.connectionsPerHost(Integer.parseInt(perHost));
			builder.writeConcern(WriteConcern.SAFE);// WriteConcern.SAFE:抛出网络错误异常、服务器错误异常；并等待服务器完成写操作。
 
			String clientURI = properties.getProperty("mongo.uri");
			if (clientURI == null || "".equals(clientURI)) {
				throw new java.lang.IllegalArgumentException("mongo.uri 不能为空！");
			}
 
			MongoClientURI mongoClientURI = new MongoClientURI(clientURI,builder);
			INSTANCE.mongoClient = new MongoClient(mongoClientURI);
		} catch (Exception e) {
			throw new RuntimeException("load resource fail, uri:" + uri
					+ " errorMsg:" + e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("MongoDb初始化＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝end");
 
	}
 
	/**
	 * 
	 * @Title getCollection
	 * @Description 获取指定数据库的指定表 
	 * @param dbName
	 *            数据库名称
	 * @param collName
	 *            表名称
	 * @return {@link }
	 * @since 2015年12月30日 下午3:13:37
	 */
	public MongoCollection<Document> getCollection(String dbName,String collName) {
		if (null == collName || "".equals(collName)) {
			return null;
		}
		if (null == dbName || "".equals(dbName)) {
			return null;
		}
		MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collName);
		return collection;
	}
	/**
	 * 
	 * @Title getDefaultCollection
	 * @Description 获取默认数据库表  
	 * @param collName	表名称
	 * @return 
	 * {@link }
	 * @since 2015年12月30日 下午4:08:40
	 */
	public MongoCollection<Document> getDefaultCollection(String collName) {
		if (null == collName || "".equals(collName)) {
			return null;
		}
		MongoCollection<Document> collection = mongoClient.getDatabase(DEFAULTDBNAME).getCollection(collName);
		return collection;
	}
	/**
	 * 
	 * @Title findById
	 * @Description 通过id查询  
	 * @param coll	表
	 * @param id	主键  mongodb默认主键名称为‘_id’ 无法修改
	 * @return 
	 * {@link }
	 * @since 2015年12月30日 下午3:25:39
	 */
	public Document findById(MongoCollection<Document> coll, String id) {
		ObjectId _idobj = null;
		try {
			_idobj = new ObjectId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return coll.find(Filters.eq("_id", _idobj)).first();
	}
	/**
	 * 
	 * @Title find
	 * @Description 条件查询  
	 * @param coll	表
	 * @param filter	条件
	 * @return 
	 * {@link }
	 * @since 2015年12月30日 下午4:10:37
	 */
	public MongoCursor<Document> findByFilter(MongoCollection<Document> coll, Bson filter) {
        return coll.find(filter).iterator();
    }
	
	/**
	 * 
	 * @Title findByPage
	 * @Description 分页查询  
	 * @param coll	表
	 * @param filter	查询条件
	 * @param sort		排序条件  使用 1 和 -1 来指定排序的方式，其中 1 为升序排列，而-1是用于降序排列。
	 * @param pageNo	需要跳转页
	 * @param pageSize	每页大小
	 * @return 
	 * {@link }
	 * @since 2015年12月30日 下午5:52:43
	 */
	public MongoCursor<Document> findByPage(MongoCollection<Document> coll, Bson filter,Bson sort , int pageNo, int pageSize) {
		if(sort == null){
			sort = new BasicDBObject("_id", 1);
		}
        return coll.find(filter).sort(sort).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }
	
	/**
	 * 
	 * @Title getCount
	 * @Description 查询表中记录条数  
	 * @param coll	表
	 * @return 
	 * {@link }
	 * @since 2015年12月30日 下午4:44:47
	 */
    public int getCount(MongoCollection<Document> coll) {
        int count = (int) coll.count();
        return count;
    }
	
	/**
	 * 
	 * @Title deleteById
	 * @Description 通过id删除  
	 * @param coll	表
	 * @param id	mongodb 默认主键 ‘_id’
	 * @return 被删除的条数
	 * {@link }
	 * @since 2015年12月30日 下午3:36:14
	 */
	public int deleteById(MongoCollection<Document> coll, String id) {
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = coll.deleteOne(filter);
        return (int) deleteResult.getDeletedCount();
    }
	/**
	 * 
	 * @Title deleteByFilter
	 * @Description 按条件删除  
	 * @param coll	表
	 * @param filter	条件
	 * @return 
	 * {@link }
	 * @since 2015年12月30日 下午4:16:00
	 */
	public int deleteByFilter(MongoCollection<Document> coll, Bson filter){
		DeleteResult deleteResult = coll.deleteMany(filter);
		return (int) deleteResult.getDeletedCount();
	}
	/**
	 * 
	 * @Title updateById
	 * @Description 通过Id修改  
	 * @param coll	表名称
	 * @param id	mongodb默认主键 ‘_id’
	 * @param newdoc	被修改后的对象
	 * @return 返回被修改的条数
	 * {@link }
	 * @since 2015年12月30日 下午3:44:05
	 */
	public int updateById(MongoCollection<Document> coll, String id, Document newdoc) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _idobj);
        UpdateResult updateResult =coll.updateOne(filter, new Document(MongoOperation.SET.getOperStr(), newdoc));
        return (int)updateResult.getModifiedCount();
    }
	/**
	 * 
	 * @Title updateByFilter
	 * @Description 按条件修改  
	 * @param coll	表
	 * @param filter	条件
	 * @param newdoc	被修改后的对象
	 * @return 被修改的条数
	 * {@link }
	 * @since 2015年12月30日 下午4:35:28
	 */
	public int updateByFilter(MongoCollection<Document> coll,  Bson filter, Document newdoc){
		UpdateResult updateResult =coll.updateMany(filter, new Document(MongoOperation.SET.getOperStr(), newdoc));
        return (int)updateResult.getModifiedCount();
	}
	
	/**
	 * 
	 * @Title insertOne
	 * @Description 新增一个  
	 * @param coll	表
	 * @param document 需新增对象  若没有设置'_id' 则mongodb自动生成
	 * {@link }
	 * @since 2015年12月30日 下午4:41:54
	 */
	public void insertOne(MongoCollection<Document> coll, Document document){
		coll.insertOne(document);
	}
	
	/**
	 * 
	 * @Title insertMany
	 * @Description 新增多个  若没有设置'_id' 则mongodb自动生成
	 * @param coll	表
	 * @param documents	需新增对象 
	 * {@link }
	 * @since 2015年12月30日 下午4:43:35
	 */
	public void insertMany(MongoCollection<Document> coll, List<Document> documents){
		coll.insertMany(documents);
	}
	
	
}