package cn.com.gps169.common.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.Morphia;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

public class GpsTripService {
    private static transient final Logger LOGGER = LoggerFactory.getLogger(GpsTripService.class);

    private Morphia morphia = null;
    private Mongo mongo = null;
    private DB db = null;
    private DBCollection collection = null;
    private static final String gpsTripCollectionName = "jt808_gps_trip";

    private static GpsTripService service = null;

    public static GpsTripService getInstance(String host, String database) {
        if (service == null) {
            synchronized (GpsTripService.class) {
                if (service == null) {
                    GpsTripService temp = new GpsTripService();

                    try {
                        temp.init(host, database);
                        service = temp;
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                        return null;
                    }
                }
            }
        }

        return service;
    }

    public void init(String host, String database) {
        if (StringUtils.isBlank(host) || StringUtils.isBlank(database)) {
            LOGGER.error("MongoDB", "MongoDB服务初始化异常，数据库服务地址和数据库名称不能为空。");
        }

        List<ServerAddress> serverList = new ArrayList<ServerAddress>();
        try {
            morphia = new Morphia();
            morphia.map(GpsTrip.class);

            MongoOptions options = new MongoOptions();
            options.setAutoConnectRetry(true);
            options.setSocketKeepAlive(true);
            options.setConnectionsPerHost(200);

            String[] hostArray = host.split(";");

            for (int i = 0; i < hostArray.length; i++) {
                String[] hostAndPort = hostArray[i].split(":");
                String tempHost = hostAndPort[0];
                int tempPort = 27017;

                if (hostAndPort.length > 1) {
                    tempPort = Integer.parseInt(hostAndPort[1]);
                }

                ServerAddress serverAddress = new ServerAddress(tempHost, tempPort);
                serverList.add(serverAddress);
            }

            mongo = new Mongo(serverList, options);
            db = mongo.getDB(database);
            collection = db.getCollection(gpsTripCollectionName);
        } catch (Exception e) {
            String errorMsg = "初始化MongoDB服务主机出错，配置信息为【" + host + "】" + e;
            LOGGER.error(errorMsg, e);
        }
    }

    public void destory() {
        if (mongo != null) {
            mongo.close();
        }

        morphia = null;
        mongo = null;
        db = null;
        collection = null;
    }

    public void saveGpsTrip(GpsTrip gpsTrip) {
    	 gpsTrip.setId(generateId(gpsTrip.getVid(), gpsTrip.getRecvDay()));

         DBObject tripObject = morphia.toDBObject(gpsTrip);

         try {
             collection.save(tripObject);
         } catch (MongoException e) {
             String errorMsg = String.format("轨迹数据%s保存失败，请检查mongoDB是否正常启动，或轨迹数据格式是否正确。错误信息：%s", tripObject.toString(),
                     e.getMessage());
             LOGGER.error(errorMsg);
         }
    }

    public List<GpsRecord> queryGpsRecords(int vehicleId, int startDay, int endDay) {
    	List<GpsRecord> gpsRecords = new ArrayList<GpsRecord>();
        long startId = generateId(vehicleId, startDay);
        long endId = generateId(vehicleId, endDay);

        DBObject query = new BasicDBObject("_id", new BasicDBObject("$gte", startId).append("$lte", endId));

        DBCursor cursor = null;

        try {
            cursor = collection.find(query);

            while (cursor.hasNext()) {
                GpsTrip gpsTrip = morphia.fromDBObject(GpsTrip.class, cursor.next());
                gpsRecords.addAll(gpsTrip.getGps());
            }
        } catch (MongoException e) {
            String errorMsg = String.format("查询车辆%d在%d至%d期间的轨迹数据失败，请检查mongoDB是否正常启动。错误信息：%s", vehicleId, startDay,
                    endDay, e.getMessage());
            LOGGER.error(errorMsg);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return gpsRecords;
    }

    public GpsTrip queryGpsTrip(int vehicleId, int occurDate) {
        // 将vehicleId和occurDate拼接成long类型的id
        long id = generateId(vehicleId, occurDate);
        DBObject query = new BasicDBObject("_id", id);

        DBCursor cursor = null;
        GpsTrip gpsTrip = null;

        try {
            cursor = collection.find(query);

            if (cursor.hasNext()) {
                gpsTrip = morphia.fromDBObject(GpsTrip.class, cursor.next());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return gpsTrip;
    }
    
    public List<GpsRecord> getAllGps(int vehicleId, int occurDate) {
        GpsTrip gpsTrip = queryGpsTrip(vehicleId, occurDate);
        if(gpsTrip != null){
            return gpsTrip.getGps();
        }
        
    	return null;
    }
    
    public List<Integer> queryVehicleIds(int occurDay) {
        List<Integer> vehicleIds = new ArrayList<Integer>();
        DBObject query = new BasicDBObject("recvDay", occurDay);
        DBObject keys = new BasicDBObject("vid", 1);

        DBCursor cursor = null;

        try {
            cursor = collection.find(query, keys);

            while (cursor.hasNext()) {
                vehicleIds.add(Integer.valueOf(cursor.next().get("vid").toString()));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return vehicleIds;
    }

    /*
     * 更新车辆的GPS TRIP信息。其中occurDate为：YYYYMMDD的int类型。
     */
    public void updateGpsTrip(int vehicleId, int occurDate, GpsRecord gpsRecord, int terminalId) {
        // 将vehicleId和occurDate拼接成long类型的id
        long id = generateId(vehicleId, occurDate);
        DBObject query = new BasicDBObject("_id", id).append("vid", vehicleId).append("recvDay", occurDate)
                .append("tid", terminalId);
        DBObject recordObj = morphia.toDBObject(gpsRecord);
        DBObject update = new BasicDBObject("$push", new BasicDBObject("gps", recordObj));

        collection.update(query, update, true, false);
    }


    /*
     * 根据vehicleId和occurDate值生成_id _id各数据位的组成：vehicleId的末位 + vehicleId + occurDate
     */
    private long generateId(int vehicleId, int occurDate) {
        int bottom = vehicleId % 10;
        String strId = "" + bottom + vehicleId + occurDate;
        long id = Long.parseLong(strId);

        return id;
    }

    /**
     * @param vehicleId
     *            车辆编号
     * @param sortOfRecvDay
     *            按recvDay字段排序，true代表升序，false代表降序
     * @param skip
     *            跳过的数据个数（尽量少跳过过多的数据，不然会产生性能问题）
     * @param limit
     *            限制查询到的数据个数
     * */
    public List<GpsTrip> queryGpsTripList(int vehicleId, boolean sortOfRecvDay, int skip, int limit) {
        List<GpsTrip> tripList = new ArrayList<GpsTrip>();

        DBObject query = new BasicDBObject("vid", vehicleId);
        DBCursor cursor = null;

        try {
            cursor = collection.find(query).sort(new BasicDBObject("recvDay", sortOfRecvDay ? 1 : -1)).skip(skip)
                    .limit(limit);

            while (cursor.hasNext()) {
                GpsTrip gpsTrip = morphia.fromDBObject(GpsTrip.class, cursor.next());
                tripList.add(gpsTrip);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tripList;
    }
}
