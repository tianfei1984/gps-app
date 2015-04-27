package cn.com.gps169.common.tool;

import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RestfulInvokeService {
	private static final Logger logger = LoggerFactory
			.getLogger(RestfulInvokeService.class);

	private static Client client = Client.create();

	public static JSONObject getData(String restfulURL,Map<String, String> params) {

		Object object = null;

		try {
			object = loadReturnObject(buildGetResponse(restfulURL, params));
			if (!(object instanceof JSONObject)) {
//				throw new BadRequestException("common", "服务器返回数据格式错误,返回格式为:"
//						+ object.getClass().getName());
			}
		} catch (Exception e) {
			logger.warn(String.format("请求资源[%s][%s]出错，原因:%s", restfulURL,
					params, e.getMessage()));
		}

		return (JSONObject) object;
	}

	public static JSONObject getSingleData(String restfulURL, Map<String, String> params) {

		return loadSingleJSONobject(buildGetResponse(restfulURL, params));
	}

	public static JSONArray getArrayDatas(String restfulURL,Map<String, String> params) {

		Object object = null;
		ClientResponse response = null;

		try {
			response = buildGetResponse(restfulURL, params);
			object = loadReturnObject(response);
			if (!(object instanceof JSONArray)) {
//				throw new BadRequestException("common", "服务器返回数据格式错误,返回格式为:"
//						+ object.getClass().getName());
			}
		} catch (Exception e) {
			logger.warn(String.format("请求资源[%s][%s]出错，原因:%s", restfulURL,
					params, e.getMessage()));
		}

		return (JSONArray) object;
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject postData(String restfulURL, Map params)  {
		return postData(restfulURL, params, JSONObject.class);
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject putData(String restfulURL, Map params) {
		return putData(restfulURL, params, JSONObject.class);
	}

	@SuppressWarnings({ "rawtypes" })
	public static <T> T postData(String restfulURL, Map params, Class<T> returnType) {
		return submitDate(restfulURL, params, returnType, SubmitType.POST);
	}

	@SuppressWarnings({ "rawtypes" })
	public static <T> T putData(String restfulURL, Map params, Class<T> returnType) {
		return submitDate(restfulURL, params, returnType, SubmitType.PUT);
	}

	public static void deleteData(String deleteURL) {

		WebResource resource = client.resource(deleteURL);

		ClientResponse response = resource.path("")
				.delete(ClientResponse.class);

		loadReturnObject(response);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T submitDate(String restfulURL, Map params, Class<T> returnType, SubmitType submitType) {
		WebResource resource = client.resource(restfulURL);
		JSONObject object = new JSONObject();

		if (params != null) {
			Set set = params.entrySet();

			for (Object obj : set) {
				Map.Entry entry = (Map.Entry) obj;

				object.put(String.valueOf(entry.getKey()),
						String.valueOf(entry.getValue()));
			}
		}

		ClientResponse response = null;

		if (submitType == SubmitType.PUT) {
			response = resource.type(MediaType.APPLICATION_JSON).put(
					ClientResponse.class, object.toString());
		} else if (submitType == SubmitType.POST) {
			response = resource.type(MediaType.APPLICATION_JSON).post(
					ClientResponse.class, object.toString());
		} else {
			return null;
		}

		T result = null;
		try {
			result = (T) loadReturnObject(response);
		} catch (ClassCastException e) {
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	private static ClientResponse buildGetResponse(String restfulURL,
			Map<String, String> params) {
		MultivaluedMap<String, String> clientParams = new MultivaluedMapImpl();
		WebResource resource = client.resource(restfulURL);

		if (params != null) {
			Set set = params.entrySet();
			for (Object obj : set) {
				Map.Entry entry = (Map.Entry) obj;

				clientParams.add(String.valueOf(entry.getKey()),
						String.valueOf(entry.getValue()));
			}
		}

		return resource.queryParams(clientParams).get(ClientResponse.class);
	}

	@SuppressWarnings("serial")
	private static Object loadReturnObject(ClientResponse response) {

		if (response.getStatus() == Status.OK.getStatusCode()
				|| response.getStatus() == Status.CREATED.getStatusCode()
				|| response.getStatus() == Status.ACCEPTED.getStatusCode()) {
			Object object = null;
			String entity = response.getEntity(String.class);

			if (StringUtils.startsWith(entity, "{")
					&& StringUtils.endsWith(entity, "}")) {
				object = JSONObject.fromObject(entity);
			} else if (StringUtils.startsWith(entity, "[")
					&& StringUtils.endsWith(entity, "]")) {
				object = JSONArray.fromObject(entity);
			} else {
				object = entity;
			}

			return object;
		} else {
			return null;
		}
	}

	@SuppressWarnings("serial")
	private static JSONObject loadSingleJSONobject(ClientResponse response) {

		JSONObject obj = new JSONObject();
		if (response.getStatus() == Status.OK.getStatusCode()) {
			String entity = response.getEntity(String.class);
			obj.put("ret", entity);

			return StringUtils.isNotBlank(entity) ? obj : new JSONObject();
		} else {
			return null;
		}
	}

	private static enum SubmitType {
		POST, PUT
	}
}
