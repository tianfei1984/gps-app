package cn.com.gps169.common.gis;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.com.gps169.common.tool.RestfulInvokeService;

import com.mysql.jdbc.StringUtils;

public class DeflexionController {
    // serviceId 1, 高德， 2, 图吧
    public List<Coordinate> getDeflexedCoordinate(List<Coordinate> srcCoor, Integer serviceId) {
        if (serviceId == null || serviceId == 1 || serviceId < 1 || serviceId > 2) {
            return autoNaviDeflexion(srcCoor);
        } else {
            return mapbarDeflexion(srcCoor);
        }
    }

    public Coordinate getDeflexedCoordinate(String lon, String lat ,Integer serviceId) {
        if (lat == null || lon == null) {
//            throw new BadRequestException("common", "坐标不能为空");
        }

        Coordinate coor = new Coordinate();
        coor.setLat(lat);
        coor.setLon(lon);
        List<Coordinate> coors = new ArrayList<Coordinate>();
        coors.add(coor);
        List<Coordinate> retCoors = null;
        if (serviceId == null || serviceId == 1 || serviceId < 1 || serviceId > 2) {
            retCoors = autoNaviDeflexion(coors);
        } else {
            retCoors = mapbarDeflexion(coors);
        }

        if (retCoors.size() != 0) {
            return retCoors.get(0);
        } else {
            return null;
        }
    }

    private List<Coordinate> autoNaviDeflexion(List<Coordinate> srcCoor) {
        JSONObject params = new JSONObject();
        params.put("config", AutoNaviConfig.GpsDdflexion);
        params.put("flag", "true");
        params.put("resType", "json");
        params.put("cr", "0");
        params.put("a_k", Configuration.getAutoNaviKey());
        String coors = "";
        for (Coordinate coor : srcCoor) {
            coors += coor.getLon() + "," + coor.getLat() + ";";
        }

        params.put("coors", coors);
        List<Coordinate> retList = new ArrayList<Coordinate>();
        String resultStr = RestfulInvokeService.postData(Configuration.getAutoNaviServiceUri(), params, String.class);
        System.out.println(resultStr);
        JSONObject ret = JSONObject.fromObject(resultStr);
        if (ret.containsKey("list")) {
            JSONArray coorList = ret.getJSONArray("list");

            for (Object object : coorList) {
                JSONObject jsonObject = JSONObject.fromObject(object);
                Coordinate coordinate = new Coordinate();
                coordinate.setLat(jsonObject.getString("y"));
                coordinate.setLon(jsonObject.getString("x"));
                retList.add(coordinate);
            }
        }

        return retList;
    }

    private Coordinate mapbarDeflexion(Coordinate srcCoor) {
        String uri = Configuration.getMapbarDeflexionUri();
        uri += "?latlon=" + srcCoor.getLat() + "," + srcCoor.getLon() + "&customer=2";
        JSONObject object = RestfulInvokeService.getData(uri,null);
        Coordinate coor = new Coordinate();
        if (!StringUtils.isNullOrEmpty(object.getString("pois"))) {
            JSONArray array = object.getJSONArray("pois");
            JSONObject obj = array.getJSONObject(0);
            String latlon = obj.getString("latlon");
            coor.setLat(latlon.substring(0, 7));
            coor.setLon(latlon.substring(7));
        }

        return coor;
    }

    private List<Coordinate> mapbarDeflexion(List<Coordinate> srcCoor) {
        List<Coordinate> coorList = new ArrayList<Coordinate>();
        for (Coordinate coor : srcCoor) {
            Coordinate retCoor = mapbarDeflexion(coor);
            coorList.add(retCoor);
        }

        return coorList;
    }
}
