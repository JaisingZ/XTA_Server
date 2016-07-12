package test.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import push.Demo;
import test.model.Binding;
import test.model.User;
import test.model.Version;
import test.service.BindingService;
import test.service.UserService;
import test.service.VersionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 需要全局记录绑定发送接收情况，实现两次握手
 *
 * 用线程池的话，要放在每个异步监听器里
 */


@WebServlet("/entrance")
public class HelloServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private VersionService versionService = new VersionService();
	private UserService userService = new UserService();
	private BindingService bindingService = new BindingService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONObject json = new JSONObject();

		try {
			json = handleRequest(request);
			PrintWriter writer = response.getWriter();
			writer.print(json);
			writer.close();

			handleResponse(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private JSONObject handleRequest(HttpServletRequest request) throws JSONException {

		//初始化友盟推送服务端
		String appkey = "5761689167e58e831f002f4d";
		String masterkey = "xp9sdk7evxrsnesxyo4onjovesy7tljl";
		//String appkey = "575b8327e0f55aee8a00175f";
		//String masterkey = "u2egxr6gbzfux3lp5zgptj2ftz8fxmu3";

		Demo demo = new Demo(appkey, masterkey);

		//返回的json对象
		JSONObject json_return = new JSONObject();
		//数据库操作参数列表
		Map<String, Object> map_params = new HashMap<>();
		String str_type = request.getParameter("type");
		String str_operation = request.getParameter("operation");

		switch (str_type) {

			/**
			 * 版本信息
			 */
			case "version":
				Version class_get_version;
				map_params.clear();
				json_return.put("type", "version");
				switch (str_operation) {
					//获取版本号
					case "get":
						json_return.put("operation", "get");
						class_get_version = versionService.getVersion();
						if (class_get_version == null) {
							json_return.put("version", "0000");
						} else {
							json_return.put("version", class_get_version.getV());
						}
						break;
					//更新版本号
					case "update":
						json_return.put("operation", "update");
						String str_update_version = request.getParameter("version");
						class_get_version = versionService.getVersion();
						if (class_get_version == null) {
							versionService.createVersion(str_update_version);
						} else {
							versionService.updateVersion(class_get_version.getV(), str_update_version);
						}
						json_return.put("version", -2);
						break;
					default:
						json_return.put("version", -1);
						break;
				}
				break;


			/**
			 * 获取用户信息
			 */
			case "getUserInfo":
				User class_get_user;
				map_params.clear();
				json_return.put("type", "getUserInfo");
				if (str_operation.equals("get")) {
					json_return.put("operation", "get");
					String str_get_id = request.getParameter("id");
					//没有id，非法请求
					if (str_get_id == null) {
						json_return.put("result", "0");
					} else {
						String str_get_name = request.getParameter("name");
						String str_get_head_img = request.getParameter("head_img");
						String str_get_device_token = request.getParameter("device_token");

						class_get_user = userService.getUser(str_get_id);
						//不存在该用户，看是否可以注册
						if (class_get_user == null) {
							//没有device_token一定不能注册，非法请求
							if (str_get_device_token == null) {
								json_return.put("result", "0");
								//有device_token可以注册
							} else {
								map_params.put("id", str_get_id);
								map_params.put("name", str_get_name);
								map_params.put("head_img", str_get_head_img);
								map_params.put("device_token", str_get_device_token);
								boolean register_result = userService.createUser(map_params);
								//注册成功
								if (register_result) {
									json_return.put("result", "2");
									//数据库操作失败
								} else {
									json_return.put("result", "-1");
								}
							}
							//用户存在，可以返回用户信息
						} else {
							json_return.put("id", class_get_user.getId());
							json_return.put("name", class_get_user.getName());
							json_return.put("head_img", class_get_user.getHead_img());
							json_return.put("device_token", class_get_user.getDevice_token());
							json_return.put("result", "1");
							demo.launch(json_return);
							//是不是还要确认已经返回用户信息？
						}
					}
					//参数错误，非法请求
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 更新用户信息
			 */
			case "updateUserInfo":
				map_params.clear();
				json_return.put("type", "updateUserInfo");
				if (str_operation.equals("update")) {
					json_return.put("operation", "update");
					String str_update_id = request.getParameter("id");
					//没有id，非法请求
					if (str_update_id == null) {
						json_return.put("result", "0");
					} else {
						String str_update_name = request.getParameter("name");
						String str_update_head_img = request.getParameter("head_img");

						class_get_user = userService.getUser(str_update_id);
						//不存在该用户，不能更新用户信息
						if (class_get_user == null) {
							json_return.put("result", "-1");
							//用户存在但是没有更新信息，不更新
						} else if (str_update_name == null && str_update_head_img == null) {
							json_return.put("result", "-2");
							//可以更新
						} else {
							map_params.put("id", str_update_id);
							map_params.put("name", str_update_name);
							map_params.put("head_img", str_update_head_img);
							boolean register_result = userService.updateUser(str_update_id, map_params);
							//更新成功
							if (register_result) {
								json_return.put("result", "1");
								//数据库操作失败
							} else {
								json_return.put("result", "-1");
							}

							json_return.put("id", str_update_id);
							json_return.put("name", class_get_user.getName());
							json_return.put("head_img", class_get_user.getHead_img());
							json_return.put("result", "1");
							demo.launch(json_return);
							//是不是还要确认已经返回用户信息？
						}
					}
					//参数错误，非法请求
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 绑定（发送绑定请求）
			 */
			case "bind":
				map_params.clear();
				json_return.put("type", "bind");
				if (str_operation.equals("add")) {
					json_return.put("operation", "add");
					String str_add_bindid = request.getParameter("bindid");
					//参数不足，非法请求
					if (str_add_bindid == null) {
						json_return.put("result", "0");
					} else {
						class_get_user = userService.getUser(str_add_bindid);
						//被绑定用户还没有注册，不能绑定
						if (class_get_user == null) {
							json_return.put("result", "-2");
						} else {
							String str_add_device_token = class_get_user.getDevice_token();
							String str_add_id = request.getParameter("id");
							//被绑定用户没有注册，不能绑定
							if (str_add_id == null) {
								json_return.put("result", "0");

								//可以绑定
							} else {
								map_params.put("id", str_add_id);
								map_params.put("bindid", str_add_bindid);

								json_return.put("id", str_add_id);
								json_return.put("bindid", str_add_bindid);
								json_return.put("device_token", str_add_device_token);
								demo.launch(json_return);

								boolean bind_result = bindingService.createBinding(map_params);
								//绑定成功
								if (bind_result) {
									json_return.put("result", "1");
									//数据库操作失败，绑定失败
								} else {
									json_return.put("result", "-1");
								}
							}
						}
					}
				} else {
					json_return.put("result", "0");
				}
				break;


			/**
			 * 获取绑定列表
			 */
			case "getBindList":
				map_params.clear();
				json_return.put("type", "getBindList");
				if (str_operation.equals("get")) {
					json_return.put("operation", "get");
					String str_get_id = request.getParameter("id");
					List<Binding> list_get = bindingService.getBinding(str_get_id);
					//绑定列表为空
					if (list_get == null) {
						json_return.put("result", "-2");
						//绑定列表不为空，可以返回绑定列表
					} else {
						/*JSONArray json_list = new JSONArray();
						json_list.put(list_get);
						json_return.put("bindList", json_list);
						System.out.println(json_return);
						json_return.put("bindList", list_get);
						System.out.print(json_return);
						json_return.put("result", "1");*/


						JSONObject json_temp = new JSONObject();
						JSONArray json_list = new JSONArray();
						for (Binding binding : list_get) {
							User class_bind_user = userService.getUser(binding.getBindid());
							json_temp.put("id", class_bind_user.getId());
							json_temp.put("name", class_bind_user.getName());
							json_temp.put("head_img", class_bind_user.getHead_img());
							json_list.put(json_temp);
						}
						json_return.put("bindList", json_list);
						json_return.put("result", "1");
						System.out.print(json_return);

						//demo.launch(json_return);

					}
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 返回绑定
			 */
			case "bind_return":
				json_return.put("type", "bind_return");
				if (str_operation.equals("return")) {
					json_return.put("operation", "return");
					String str_return_id = request.getParameter("id");
					String str_return_bindid = request.getParameter("bindid");
					String str_return_content = request.getParameter("content");
					String str_return_device_token = userService.getUser(str_return_bindid).getDevice_token();

					json_return.put("id", str_return_id);
					json_return.put("content", str_return_content);
					json_return.put("device_token", str_return_device_token);
					demo.launch(json_return);

					json_return.put("result", "-2");
				} else {
					json_return.put("result", "-1");
				}
				break;
		}

		return json_return;

	}

	private void handleResponse(HttpServletResponse response) {
	}
}