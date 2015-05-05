package com.ljmob.corner.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.ljmob.corner.R;

/**
 * Http请求工具类
 * 
 * @author YANGBANG
 * 
 */
public class HttpRequestTools {
	private static final int CONNECTION_TIMEOUT = 10 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	public static int statusCode;// 状态码

	/**
	 * get请求
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            请求url地址
	 * @param token
	 *            令牌----可为null
	 * @return 服务器响应
	 */
	public static String doGet(Context context, String url, String token) {
		String result = "";
		HttpGet get = new HttpGet(url);
		get.setHeader("Auth-Token", token);
		try {
			HttpResponse httpResponse = getHttpClient().execute(get);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			Log.i("statusCode", statusCode + "");
			Log.i("result", result);
			if (statusCode == 200 || statusCode == 201) {
				return result;
			} else {
				DetailHintUtil.showToast(context, result, statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			ToastUtil.show(context, R.string.toast_error_net);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * post请求
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            请求url地址
	 * @param params
	 *            参数map集合----可为null
	 * @param files
	 *            文件map集合-----可为null
	 * @param token
	 *            令牌----可为null
	 * @return 服务器响应
	 */
	public static String doPost(Context context, String url,
			Map<String, Object> params, Map<String, File> files, String token) {
		String result = "";
		HttpPost post = new HttpPost(url);
		post.setHeader("Auth-Token", token);
		// post.setHeader("Accept", "application/json");
		// post.setHeader("Content-Type", "application/json;charset=UTF-8");
		MultipartEntity entity = new MultipartEntity();
		// 添加参数
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				try {
					entity.addPart(key, new StringBody(params.get(key)
							.toString(), Charset.forName(HTTP.UTF_8)));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		// 添加文件
		int asd = 0;
		if (files != null && !files.isEmpty()) {
			for (String key : files.keySet()) {
				if (files.get(key).exists()) {
					asd++;
					Log.i("YANGBANG1234", "正在上传第" + asd + "张图片");
					// FileBody fileBody = new FileBody(files.get(key),
					// "application/octet-stream");
					entity.addPart(key, new FileBody(files.get(key)));
					// array.put(new FileBody(files.get(key)));
					// "application/octet-stream"));
					// FormBodyPart bodyPart = new FormBodyPart(files.get(key)
					// .getName(), fileBody);
					// entity.addPart(bodyPart);
				}
			}
		}
		Log.i("YANGBANG1111", "setEntity");
		post.setEntity(entity);
		try {
			HttpResponse httpResponse = getHttpClient().execute(post);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			Log.i("statusCode", statusCode + "");
			Log.i("result", result);
			if (statusCode == 200 || statusCode == 201) {
				return result;
			} else {
				Log.i("No201 Or 200", "No201 Or 200");
				DetailHintUtil.showToast(context, result, statusCode);
			}
		} catch (ClientProtocolException e) {
			Log.i("YANGBANG", "连接超时！");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("YANGBANG", "连接超时！！！！");
			ToastUtil.show(context, R.string.toast_error_net);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * put请求
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            请求url地址
	 * @param params
	 *            参数map集合----可为null
	 * @param files
	 *            文件map集合-----可为null
	 * @param token
	 *            令牌----可为null
	 * @return 服务器响应
	 */
	public static String doPut(Context context, String url,
			Map<String, Object> params, Map<String, File> files, String token) {
		String result = "";
		HttpPut put = new HttpPut(url);
		put.setHeader("Auth-Token", token);
		MultipartEntity entity = new MultipartEntity();
		// 添加参数
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				try {
					entity.addPart(key, new StringBody(params.get(key)
							.toString(), Charset.forName(HTTP.UTF_8)));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		// 添加文件
		if (files != null && !files.isEmpty()) {
			for (String key : files.keySet()) {
				entity.addPart(key, new FileBody(files.get(key)));
			}
		}
		put.setEntity(entity);
		try {
			HttpResponse httpResponse = getHttpClient().execute(put);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			Log.i("statusCode", statusCode + "");
			Log.i("result", result);
			if (statusCode == 200 || statusCode == 201) {
				return result;
			} else {
				DetailHintUtil.showToast(context, result, statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			ToastUtil.show(context, R.string.toast_error_net);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            请求url地址
	 * @param token
	 *            令牌----可为null
	 * @return 服务器响应
	 */
	public static String doDelete(Context context, String url, String token) {
		String result = "";
		HttpDelete delete = new HttpDelete(url);
		delete.setHeader("Auth-Token", token);
		try {
			HttpResponse httpResponse = getHttpClient().execute(delete);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			Log.i("statusCode", statusCode + "");
			Log.i("result", result);
			if (statusCode == 200 || statusCode == 201) {
				return result;
			} else {
				DetailHintUtil.showToast(context, result, statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			ToastUtil.show(context, R.string.toast_error_net);
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 取得HttpCilent实例
	 * 
	 * @return HttpClient实例
	 */
	private static HttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		return httpClient;
	}

}
