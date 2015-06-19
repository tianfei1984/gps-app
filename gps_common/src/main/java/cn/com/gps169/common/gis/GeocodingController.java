package cn.com.gps169.common.gis;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.common.tool.RestfulInvokeService;

import com.mysql.jdbc.StringUtils;

public class GeocodingController {
    // serviceId 1, 高德， 2, 图吧
    // poiPattern 0为返回地标性POI，1为返回全部POI
	
    private static final Logger LOGGER = LoggerFactory.getLogger(GeocodingController.class);
    
    public JSONObject getGeocodingInfo(String lat, String lon, Integer range, Integer poiPattern,
            Integer serviceId) {
    	
    	LOGGER.info("开始逆地理解析");
        if (serviceId == null || serviceId == 1 || serviceId < 1 || serviceId > 2) {
            if (range == null) {
                range = Integer.parseInt(Configuration.getGeocodingRange());
            }

            if (poiPattern == null || poiPattern > 1) {
                poiPattern = Integer.parseInt(Configuration.getGeocodingPoiPattern());
            }
            
            JSONObject object = null;
            try {
            	object = regeoCoding(lat, lon, range, poiPattern);
			} catch (Exception e) {
				LOGGER.info("使用高德你地理解析失败，原因:{}",e.getMessage());
				object = autoNaviGeocoding(lat, lon, range, poiPattern);
			}
            return object;
        } else {
            return mapbarGeocoding(lat, lon);
        }
    }

    // poiPattern 0为返回地标性POI，1为返回全部POI
    public String simpleGeocoding(String lat, String lon) {
        JSONObject params = new JSONObject();
        params.put("config", AutoNaviConfig.SimpleGeocoding);
        params.put("x1", lon);
        params.put("y1", lat);
        params.put("enc", "utf-8");
        params.put("a_k", Configuration.getAutoNaviKey());
        String resultStr = RestfulInvokeService.postData(Configuration.getAutoNaviServiceUri(), params,String.class);
        return resultStr;
    }
    
    public String simpleRegeocoding(String lat, String lon) {
    	LOGGER.info("使用高德服务解析");
        String url = Configuration.getRegeoUri();
        String key = Configuration.getRegeoKey();
        String uri = url + "?key="+key+"&output=json&location="+lon+","+lat+"&extensions=base";
        JSONObject object = RestfulInvokeService.getData(uri,null);
        object = object.getJSONObject("regeocode");
        return object.optString("formatted_address");
    }

    private String getSpatialXml(String lat, String lon, Integer searchRange, Integer poiPattern) {
        Element root = new Element("spatial_request");
        root.setAttribute("method", "searchPoint");
        Document doc = new Document(root);
        Element xCoor = new Element("x");
        xCoor.setText(lon);
        root.addContent(xCoor);
        Element yCoor = new Element("y");
        yCoor.setText(lat);
        root.addContent(yCoor);
        Element poiNumber = new Element("poiNumber");
        poiNumber.setText("10");
        root.addContent(poiNumber);
        Element range = new Element("range");
        range.setText(searchRange.toString());
        root.addContent(range);
        Element pattern = new Element("pattern");
        pattern.setText(poiPattern.toString());
        root.addContent(pattern);
        Element roadLevel = new Element("roadLevel");
        roadLevel.setText("0");
        root.addContent(roadLevel);
        XMLOutputter output = new XMLOutputter();// (Format.getRawFormat().setEncoding("GB2312"));
        String xmlStr = output.outputString(doc);
        return xmlStr;
    }

    private JSONObject autoNaviGeocoding(String lat, String lon, Integer range, Integer poiPattern) {
    	LOGGER.info("使用车网服务解析");
        JSONObject params = new JSONObject();
        params.put("config", AutoNaviConfig.Geocoding);
        params.put("enc", "utf-8");
        params.put("resType", "json");
        params.put("spatialXml", getSpatialXml(lat, lon, range, poiPattern));
        params.put("a_k", Configuration.getAutoNaviKey());
        String resultStr = RestfulInvokeService.postData(Configuration.getAutoNaviServiceUri(), params,String.class);
        return parseGeocodingResult(resultStr);
    }
    
    private JSONObject regeoCoding(String lat, String lon, Integer range, Integer poiPattern) {
    	LOGGER.info("使用高德服务解析");
        String url = Configuration.getRegeoUri();
        String key = Configuration.getRegeoKey();
        if(range == 0){
        	range = Integer.parseInt(Configuration.getRegeoRadiu());
        }
        String uri = url + "?key="+key+"&output=json&location="+lon+","+lat+"&extensions=all&radius="+range;
        JSONObject object = RestfulInvokeService.getData(uri,null);
        return parseRegeocodingResult(object, lat, lon);
    }
    
    private JSONObject parseRegeocodingResult(JSONObject object,String lati,String lont) {
        JSONObject ret = new JSONObject();
        object = object.getJSONObject("regeocode");
        if (!StringUtils.isNullOrEmpty(object.getString("pois"))) {
            JSONArray poiList = object.getJSONArray("pois");
            JSONArray retPoiList = new JSONArray();
            
            int k = 0;
            for (Object poi : poiList) {
                JSONObject jsonObject = JSONObject.fromObject(poi);
                JSONObject retPoi = new JSONObject();
                String name = jsonObject.getString("name");
                String distance = jsonObject.getString("distance");
                String direction = jsonObject.getString("direction");
                String type = jsonObject.getString("type");
                String address = jsonObject.getString("address");
                String location = jsonObject.getString("location");
                
                String lat = "";
                String lon = "";
                if(location.contains(",")){
                	String[] locs = location.split(",");
                	lon = locs[0];
                	lat = locs[1];
                }
               
                retPoi.put("name", name);
                retPoi.put("distance", distance);
                retPoi.put("direction", direction);
                retPoi.put("type", type);
                retPoi.put("address", address);
                retPoi.put("lat", lat);
                retPoi.put("lon", lon);
                retPoiList.add(retPoi);
                k ++;
                if(k == 10){
                	break;
                }
            }

            ret.put("poiList", retPoiList);
        }

        if (!StringUtils.isNullOrEmpty(object.getString("roads"))) {
            JSONArray roadList = object.getJSONArray("roads");
            JSONArray retRoadList = new JSONArray();
            if(roadList.size() > 0){
            	  for (Object road : roadList) {
                      JSONObject jsonObject = JSONObject.fromObject(road);
                      
                      String location = jsonObject.getString("location");
                      
                      String lat = "";
                      String lon = "";
                      if(location.contains(",")){
                      	String[] locs = location.split(",");
                      	lon = locs[0];
                      	lat = locs[1];
                      }
                      jsonObject.remove("id");
                      jsonObject.put("lat", lat);
                      jsonObject.put("lon", lon);
                      retRoadList.add(jsonObject);
                  }
            }
          
            ret.put("roadList", retRoadList);
        }

        if (!StringUtils.isNullOrEmpty(object.getString("roadinters"))) {
            JSONArray crossPoiList = object.getJSONArray("roadinters");
            JSONArray retCrossPoiList = new JSONArray();
            if(crossPoiList.size() > 0){
            	  for (Object crossPoi : crossPoiList) {
                      JSONObject orgCrossPoi = JSONObject.fromObject(crossPoi);
                      
                      String location = orgCrossPoi.getString("location");
                      
                      String lat = "";
                      String lon = "";
                      if(location.contains(",")){
                      	String[] locs = location.split(",");
                      	lon = locs[0];
                      	lat = locs[1];
                      }
                     
                      String name = orgCrossPoi.optString("first_name") + "-" + orgCrossPoi.optString("second_name");
                      orgCrossPoi.remove("first_id");
                      orgCrossPoi.remove("first_name");
                      orgCrossPoi.remove("second_id");
                      orgCrossPoi.remove("second_name");
                      orgCrossPoi.put("lat", lat);
                      orgCrossPoi.put("lon", lon);
                      orgCrossPoi.put("name", name);
                      retCrossPoiList.add(orgCrossPoi);
                  }
            }

            ret.put("crossPoiList", retCrossPoiList);
        }
        
        JSONObject address = object.getJSONObject("addressComponent");
        
        JSONObject provinceObject = new JSONObject();
        provinceObject.put("name", address.optString("province"));
        ret.put("Province", provinceObject);
        JSONObject cityObject = new JSONObject();
        cityObject.put("citycode", address.optString("citycode"));
        String cityname = address.optString("city");
        if(cityname.equals("[]")){
        	cityname = "";
        }
        cityObject.put("name", cityname);
        ret.put("City", cityObject);

        JSONObject districtObject = new JSONObject();
        districtObject.put("name", address.optString("district"));
        districtObject.put("lat", lati);
        districtObject.put("lon", lont);
        ret.put("District", districtObject);
        return ret;
    }
    
    private JSONObject parseGeocodingResult(String resultStr) {
        JSONObject ret = new JSONObject();
        JSONObject object = JSONObject.fromObject(resultStr);
        object = object.getJSONObject("SpatialBean");
        if (!StringUtils.isNullOrEmpty(object.getString("poiList"))) {
            JSONArray poiList = object.getJSONArray("poiList");
            JSONArray retPoiList = new JSONArray();
            for (Object poi : poiList) {
                JSONObject jsonObject = JSONObject.fromObject(poi);
                JSONObject retPoi = new JSONObject();
                String name = jsonObject.getString("name");
                String distance = jsonObject.getString("distance");
                String direction = chToEhDirection(jsonObject.getString("direction"));
                String type = jsonObject.getString("type");
                String address = jsonObject.getString("address");
                String lat = jsonObject.getString("y");
                String lon = jsonObject.getString("x");
                retPoi.put("name", name);
                retPoi.put("distance", distance);
                retPoi.put("direction", direction);
                retPoi.put("type", type);
                retPoi.put("address", address);
                retPoi.put("lat", lat);
                retPoi.put("lon", lon);
                retPoiList.add(retPoi);
            }

            ret.put("poiList", retPoiList);
        }

        if (!StringUtils.isNullOrEmpty(object.getString("roadList"))) {
            JSONArray roadList = object.getJSONArray("roadList");
            JSONArray retRoadList = new JSONArray();
            for (Object road : roadList) {
                JSONObject jsonObject = JSONObject.fromObject(road);
                String lat = jsonObject.getString("y");
                String lon = jsonObject.getString("x");
                jsonObject.remove("id");
                jsonObject.remove("ename");
                jsonObject.remove("level");
                jsonObject.remove("x");
                jsonObject.remove("y");
                jsonObject.put("lat", lat);
                jsonObject.put("direction", chToEhDirection(jsonObject.optString("direction")));
                jsonObject.put("lon", lon);
                retRoadList.add(jsonObject);
            }

            ret.put("roadList", retRoadList);
        }

        if (!StringUtils.isNullOrEmpty(object.getString("crossPoiList"))) {
            JSONArray crossPoiList = object.getJSONArray("crossPoiList");
            JSONArray retCrossPoiList = new JSONArray();
            for (Object crossPoi : crossPoiList) {
                JSONObject orgCrossPoi = JSONObject.fromObject(crossPoi);
                String lat = orgCrossPoi.getString("y");
                String lon = orgCrossPoi.getString("x");
                orgCrossPoi.remove("roadList");
                orgCrossPoi.remove("x");
                orgCrossPoi.remove("y");
                orgCrossPoi.put("lat", lat);
                orgCrossPoi.put("lon", lon);
                orgCrossPoi.put("direction", chToEhDirection(orgCrossPoi.optString("direction")));
                retCrossPoiList.add(orgCrossPoi);
            }

            ret.put("crossPoiList", retCrossPoiList);
        }

        JSONObject provinceObject = object.getJSONObject("Province");
        provinceObject.remove("ename");
        provinceObject.remove("code");
        ret.put("Province", provinceObject);
        JSONObject cityObject = object.getJSONObject("City");
        cityObject.remove("ename");
        cityObject.remove("code");
        ret.put("City", cityObject);

        JSONObject districtObject = object.getJSONObject("District");
        String lat = districtObject.optString("y");
        String lon = districtObject.optString("x");
        districtObject.put("lat", lat);
        districtObject.put("lon", lon);
        ret.put("District", districtObject);
        return ret;
    }

    private String enToChDirection(String enDirection) {
        if (enDirection.equalsIgnoreCase("north")) {
            return "北";
        } else if (enDirection.equalsIgnoreCase("south")) {
            return "南";
        } else if (enDirection.equalsIgnoreCase("west")) {
            return "西";
        } else if (enDirection.equalsIgnoreCase("east")) {
            return "东";
        } else if (enDirection.equalsIgnoreCase("NorthEast")) {
            return "东北";
        } else if (enDirection.equalsIgnoreCase("NorthWest")) {
            return "西北";
        } else if (enDirection.equalsIgnoreCase("SouthWest")) {
            return "西南";
        } else if (enDirection.equalsIgnoreCase("SouthEast")) {
            return "东南";
        } else {
            return enDirection;
        }
    }
    
    
    private String chToEhDirection(String cnDirection) {
        if (cnDirection.equalsIgnoreCase("北")) {
            return "North";
        } else if (cnDirection.equalsIgnoreCase("南")) {
            return "South";
        } else if (cnDirection.equalsIgnoreCase("西")) {
            return "West";
        } else if (cnDirection.equalsIgnoreCase("东")) {
            return "East";
        } else if (cnDirection.equalsIgnoreCase("东北")) {
            return "NorthEast";
        } else if (cnDirection.equalsIgnoreCase("西北")) {
            return "NorthWest";
        } else if (cnDirection.equalsIgnoreCase("西南")) {
            return "SouthWest";
        } else if (cnDirection.equalsIgnoreCase("东南")) {
            return "SouthEast";
        } else {
            return "";
        }
    }

    public String mapbarGeocoding(String latlon) {
        String uri = Configuration.getMapbarGeocodingUri();
        uri += "?customer=1&detail=1&zoom=13&latlon=" + latlon;
        JSONObject object = RestfulInvokeService.getData(uri,null);
        String location = "";
        if (object.getJSONObject("province").containsKey("#text")) {
            if (!object.getJSONObject("province").getString("#text").equalsIgnoreCase("直辖市")) {
                location += object.getJSONObject("province").getString("#text");
            }
        }

        if (object.getJSONObject("city").containsKey("#text")) {
            if (object.getJSONObject("city") != null
                    && object.getJSONObject("city").getString("#text") != null
                    && object.getJSONObject("city").getString("#text").length() > 0) {
                location += object.getJSONObject("city").getString("#text");
            }
        }

        if (object.getJSONObject("dist").containsKey("#text")) {
            if (object.getJSONObject("dist") != null
                    && object.getJSONObject("dist").getString("#text") != null
                    && object.getJSONObject("dist").getString("#text").length() > 0) {
                location += object.getJSONObject("dist").getString("#text");
            }
        }

        if (object.getJSONObject("area").containsKey("#text")) {
            if (object.getJSONObject("area") != null
                    && object.getJSONObject("area").getString("#text") != null
                    && object.getJSONObject("area").getString("#text").length() > 0) {
                location += object.getJSONObject("area").getString("#text");
            }
        }

        if (object.getString("poi") != null && object.getString("poi").length() > 0) {
            location += object.getString("poi");
        }

        if (object.getString("direction") != null && object.getString("direction").length() > 0) {
            location += object.getString("direction");
        }

        if (object.getString("distance") != null && object.getString("distance").length() > 0) {
            location += object.getString("distance");
        }

        return location;
    }

    private JSONObject mapbarGeocoding(String lat, String lon) {
        // TODO
        return null;
    }
}
