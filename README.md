# MongoDbUtil
基于MongoDb的公共组件

# quick start
 (1) add mongodb.properties to Tomcat/commoncfg/dbconf/
<br/> (2) use example
```
  add：
     void insertOne(String collName, Document document)；//新增单条 collName 表名   document需要保存的对象
     void insertMany(String collName, List<Document> documents)；//入库多条  

  add examples：
    public String add(@ModelAttribute RequestData data){
        //入库之前需先转换为document
        Document document = data.toDocument(data);
        MongoDbUtils.insertOne("student", document);//入库单条   
    }

  query：（注意Filters的用法  改删查的查询匹配条件都使用此类）
        Document findById(String collName, String id)；//quyer by id,  collName为表名
        MongoCursor<Document> findByFilter(String collName, Bson filter)；//条件查询  collName为表名  filter为查询条件 
        MongoCursor<Document> findByPage(String collName, Bson filter,Bson sort , int pageNo, int pageSize);//分页查询 filter查询条件 sort排序条件
       //如Bson sort = new BasicDBObject("_id", 1);  order by id,  1：asc, -1:desc  
       // pageNo 需要去的页 不可为0pageSize每页大小
       
  query examples：
        public String queryAll(ModelMap modelMap){
            //Bson filter = Filters.ne("_id", "-12");不等于
            //Bson filter = Filters.eq("_id", "-12");等于
            //String[] items = {"1","3","4"};
            //Bson filter = Filters.in("cardNo", items);查询卡号分别为 1，3 ，4的数据
            //查询当前五分钟之内的数据   firstDate在mongodb中是直接以Date类型存储的   此处注意Filters.and的用法  可以同时包含多个条件
            Date date = new Date();
            date.setMinutes(-5);
            Bson filter = Filters.and(Filters.lte("firstDate", new Date()),Filters.gte("firstDate", date));
            MongoCursor<Document> result = MongoDbUtils.findByFilter("student", filter);//条件查询
            List<RequestData> listData = new ArrayList<RequestData>();
            while (result.hasNext()) {
                Document document=result.next();
                RequestData requestData=new RequestData();
                requestData.toRequestData(document);//将查询结果Document转换为实体bean 
                listData.add(requestData);
            }
            modelMap.put("listData", listData);
            return "result";
        }

    delete：
        int deleteByFilter(String collName, Bson filter)；//按条件删除  返回受影响条数条数  collName表名   filter条件
        int deleteById(String collName, String id)；//通过id删除  返回受影响条数
     
     delete examples：
        @RequestMapping(value = "/del")
        public String del(String id){ 
            int delCount  = MongoDbUtils.deleteById("student", id);
            System.out.println("删除条数：" + delCount);       
            return "redirect:/queryAll";
        } 
       
     update：  
        int updateById(String collName, String id, Document newdoc)；//按id修改  newdoc为修改后的对象  返回受影响条数
        int updateByFilter(String collName,  Bson filter, Document newdoc)//按filter条件修改  newdoc为修改后的对象  返回受影响条数
  
     update examples：
        @RequestMapping(value="/modify")
        public String modify(@ModelAttribute RequestData data,ModelMap model){
            System.out.println("要修改的数据id：" + data.getId());
            data.setFirstDate(new Date());//设置最后修改时间
            Document newDocument = data.toDocument(data);//将bean转换为 Document
            int updateCount = MongoDbUtils.updateById("student", data.getId().toString(), newDocument);//按id修改
            System.out.println("修改条数：" + updateCount);
            return "redirect:/queryAll";
        }
```




