package test.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import push.Demo;
import push.JPush;
import test.model.*;
import test.service.*;
import test.util.EncodingUtil;
import test.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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
	private LocationService locationService = new LocationService();
	private PlanService planService = new PlanService();

	//存储每个用户上传的最新位置信息
	Map<String, Map<String, Object>> map_last_location = new HashMap<>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
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

	private JSONObject handleRequest(HttpServletRequest request)
			throws JSONException, UnsupportedEncodingException {

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
				Version class_get_version = null;
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
				User class_get_user = null;
				map_params.clear();
				json_return.put("type", "getUserInfo");
				if (str_operation.equals("get")) {
					json_return.put("operation", "get");
					String str_get_id = request.getParameter("id");
					//没有id，非法请求
					if (StringUtil.isEmpty(str_get_id)) {
						json_return.put("result", "0");
					} else {
						String str_get_name = EncodingUtil.strISO2UTF(request.getParameter("name"));
						String str_get_head_img = request.getParameter("head_img");
						String str_get_device_token = request.getParameter("device_token");

						class_get_user = userService.getUser(str_get_id);
						//不存在该用户，看是否可以注册
						if (class_get_user == null ) {
							//没有device_token一定不能注册，非法请求
							if (StringUtil.isEmpty(str_get_device_token)) {
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
					if (StringUtil.isEmpty(str_update_id)) {
						json_return.put("result", "0");
					} else {
						String str_update_name = EncodingUtil.strISO2UTF(request.getParameter("name"));
						String str_update_head_img = request.getParameter("head_img");

						class_get_user = userService.getUser(str_update_id);
						//不存在该用户，不能更新用户信息
						if (class_get_user == null) {
							json_return.put("result", "-1");
							//用户存在但是没有更新信息，不更新
						} else if (StringUtil.isEmpty(str_update_name) && StringUtil.isEmpty(str_update_head_img)) {
							json_return.put("result", "-2");
							//可以更新
						} else {
							map_params.put("id", str_update_id);
							map_params.put("name", str_update_name);
							map_params.put("head_img", str_update_head_img);
							if (StringUtil.isEmpty(str_update_name)) {
								map_params.remove("name");
							}
							if (StringUtil.isEmpty(str_update_head_img)) {
								map_params.remove("head_img");
							}
							boolean register_result = userService.updateUser("id", str_update_id, map_params);
							//更新成功
							if (register_result) {
								json_return.put("result", "1");
								//数据库操作失败
							} else {
								json_return.put("result", "-1");
							}
							json_return.put("updateInfo", map_params);
							json_return.put("result", "1");
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
					if (StringUtil.isEmpty(str_add_bindid)) {
						json_return.put("result", "0");
					} else {
						class_get_user = userService.getUser(str_add_bindid);
						//被绑定用户还没有注册，不能绑定
						if (class_get_user == null) {
							json_return.put("result", "-2");
						} else {
							String str_add_device_token = class_get_user.getDevice_token();
							String str_add_id = request.getParameter("id");
							//参数不足，非法请求
							if (StringUtil.isEmpty(str_add_id)) {
								json_return.put("result", "0");
								//可以绑定
							} else {
								map_params.put("id", str_add_id);
								map_params.put("bindid", str_add_bindid);

								json_return.put("id", str_add_id);
								json_return.put("bindid", str_add_bindid);
								json_return.put("device_token", str_add_device_token);

								boolean bind_result = bindingService.createBinding(map_params);
								//绑定成功
								if (bind_result) {
									//推送到被绑定人设备上
									JSONObject json_push = new JSONObject();
									json_push.put("device_token", str_add_device_token);
									json_push.put("id", str_add_id);
									json_push.put("name", userService.getUser(str_add_id).getName());
									JPush.launch(json_push);

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
						JSONObject json_temp = null;
						JSONArray json_list = new JSONArray();
						for (Binding binding : list_get) {
							json_temp = new JSONObject();
							User class_bind_user = userService.getUser(binding.getBindid());
							json_temp.put("id", class_bind_user.getId());
							json_temp.put("name", class_bind_user.getName());
							json_temp.put("head_img", class_bind_user.getHead_img());
							json_list.put(json_temp);
						}
						json_return.put("bindList", json_list);
						json_return.put("result", "1");
					}
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 确认绑定
			 */
			case "bindConfirm":
				json_return.put("type", "bindConfirm");
				if (str_operation.equals("confirm")) {
					json_return.put("operation", "confirm");
					//被绑定人的id
					String str_confirm_id = request.getParameter("id");
					//绑定人的id
					String str_confirm_bindid = request.getParameter("bindid");
					//确认绑定结果
					String str_confirm_agree = request.getParameter("agree");

					class_get_user = userService.getUser(str_confirm_id);
					String str_confirm_name = class_get_user.getName();

					class_get_user = userService.getUser(str_confirm_bindid);
					String str_confirm_device_token = class_get_user.getDevice_token();

					JSONObject json_push = new JSONObject();
					json_push.put("id", str_confirm_id);
					json_push.put("name", str_confirm_name);
					json_push.put("device_token", str_confirm_device_token);
					if (str_confirm_agree.equals("1")) {
						json_return.put("result", "1");

						//返回被绑定人的位置
						boolean found_loc = false;
						for (Map.Entry entry : map_last_location.entrySet()) {
							//找到该位置，返回位置信息
							if (str_confirm_id.equals(entry.getKey().toString())) {
								json_push.put("lacation", entry.getValue());
								found_loc = true;

								break;
							}
						}
						//没有存储位置
						if (!found_loc) {
							json_push.put("result", "-2");
						}

					} else {
						json_return.put("result", "2");
					}
					JPush.launch(json_push);
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 发送当前位置
			 */
			case "sendLocation":
				json_return.put("type", "sendLocation");
				if (str_operation.equals("add")) {
					json_return.put("operaion", "add");
					String str_add_id = request.getParameter("id");
					String str_add_space = EncodingUtil.strISO2UTF(request.getParameter("space"));
					String str_add_lat = request.getParameter("lat");
					String str_add_lot = request.getParameter("lot");
					String str_add_time = request.getParameter("time");
					if (str_add_id == null || str_add_space == null || str_add_lat == null
							|| str_add_lot == null || str_add_time == null) {
						json_return.put("result", "0");
					} else {
						Map<String, Object> map_temp = new HashMap<>();
						map_temp.put("space", str_add_space);
						map_temp.put("lat", str_add_lat);
						map_temp.put("lot", str_add_lot);
						map_temp.put("time", str_add_time);
						map_last_location.put(str_add_id, map_temp);

						map_params.put("id", str_add_id);
						map_params.put("space", str_add_space);
						map_params.put("lat", str_add_lat);
						map_params.put("lot", str_add_lot);
						map_params.put("time", str_add_time);
						boolean result = locationService.createLocation(map_params);
						if (result) {
							json_return.put("result", "1");
						} else {
							json_return.put("result", "-1");
						}
					}
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 获取用户当前位置
			 */
			case "getCurrentLocation":
				json_return.put("type", "getCurrentLocation");
				if (str_operation.equals("get")) {
					json_return.put("operation", "get");
					String str_get_id = request.getParameter("id");
					if (str_get_id == null) {
						json_return.put("result", "0");
					} else {
						boolean found_loc = false;
						for (Map.Entry entry : map_last_location.entrySet()) {
							//找到该位置，返回位置信息
							if (str_get_id.equals(entry.getKey().toString())) {
								json_return.put("currentLocation", entry.getValue());
								json_return.put("result", "1");
								found_loc = true;
								break;
							}
						}
						//没有存储位置
						if (!found_loc) {
							json_return.put("result", "-2");
						}
					}
					System.out.print(json_return);
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 制定计划
			 */
			case "makePlan":
				map_params.clear();
				json_return.put("type", "makeplan");
				if (str_operation.equals("add")) {
					String str_add_id = request.getParameter("id");
					String str_add_bindid = request.getParameter("bindid");
					String str_add_space_start = EncodingUtil.strISO2UTF(request.getParameter("space_start"));
					String str_add_space_arrival = EncodingUtil.strISO2UTF(request.getParameter("space_arrival"));
					String str_add_lat_start = request.getParameter("lat_start");
					String str_add_lat_arrival = request.getParameter("lat_arrival");
					String str_add_lot_start = request.getParameter("lot_start");
					String str_add_lot_arrival = request.getParameter("lot_arrival");
					String str_add_time_start = request.getParameter("time_start");
					String str_add_time_arrival = request.getParameter("time_arrival");
					String str_add_remark = EncodingUtil.strISO2UTF(request.getParameter("remark"));
					String str_add_grade = request.getParameter("grade");

					if (str_add_id == null || str_add_bindid == null || str_add_space_start == null ||
							str_add_space_arrival == null || str_add_lat_start == null || str_add_lat_arrival == null ||
							str_add_lot_start == null || str_add_lot_arrival == null || str_add_time_start == null ||
							str_add_time_arrival == null) {
						json_return.put("result", "0");
					} else {
						User class_get_binduser = userService.getUser(str_add_bindid);
						//被指定计划的人未注册
						if (class_get_binduser == null) {
							json_return.put("result", "-2");
						} else {
							map_params.put("id", str_add_id);
							map_params.put("bindid", str_add_bindid);
							map_params.put("space_start", str_add_space_start);
							map_params.put("space_arrival", str_add_space_arrival);
							map_params.put("lat_start", str_add_lat_start);
							map_params.put("lat_arrival", str_add_lat_arrival);
							map_params.put("lot_start", str_add_lot_start);
							map_params.put("lot_arrival", str_add_lot_arrival);
							map_params.put("time_start", str_add_time_start);
							map_params.put("time_arrival", str_add_time_arrival);
							map_params.put("remark", str_add_remark);
							map_params.put("grade", str_add_grade);
							boolean result = planService.createPlan(map_params);
							if (result) {
								json_return.put("planInfo", map_params);
								json_return.put("result", "1");
							} else {
								json_return.put("result", 0);
							}
						}
					}
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 获取计划
			 * 一个人最多只能成为一个制定者，但可以成为多个被制定者
			 */
			case "getPlan":
				map_params.clear();
				json_return.put("type", "getPlan");
				if (str_operation.equals("get")) {
					json_return.put("operation", "get");
					String str_get_id = request.getParameter("id");
					Plan class_get_plan = planService.getPlanById(str_get_id);
					if (class_get_plan == null) {
						json_return.put("result", "-2");
					} else {
						String str_finish_time = class_get_plan.getTime_arrival();
						Date current_date = new Date();
						//计划有效，返回
						if (str_finish_time.compareTo(String.valueOf(current_date.getTime())) > 0) {
							JSONObject json_temp = new JSONObject();
							json_temp.put("id", class_get_plan.getId());
							json_temp.put("bindid", class_get_plan.getBindid());
							json_temp.put("space_start", class_get_plan.getSpace_start());
							json_temp.put("space_arrival", class_get_plan.getSpace_arrival());
							json_temp.put("lat_start", class_get_plan.getLat_start());
							json_temp.put("lat_arrival", class_get_plan.getLat_arrival());
							json_temp.put("lot_start", class_get_plan.getLot_start());
							json_temp.put("lot_arrival", class_get_plan.getLot_arrival());
							json_temp.put("time_start", class_get_plan.getTime_start());
							json_temp.put("time_arrival", class_get_plan.getTime_arrival());
							json_temp.put("remark", class_get_plan.getRemark());
							json_temp.put("grade", class_get_plan.getGrade());
							json_return.put("planInfo", json_temp);
							json_return.put("result", "1");
							//计划失效，从数据库中删除
						} else {
							planService.deletePlanById(str_get_id);
							json_return.put("result", "-3");
						}
					}
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 取消计划（delete）
			 */
			case "cancelPlan":
				map_params.clear();
				json_return.put("type", "cancelPlan");
				if (str_operation.equals("delete")) {
					json_return.put("operation", "delete");
					String str_delete_id = request.getParameter("id");
					Plan class_delete_plan = planService.getPlanById(str_delete_id);
					if (class_delete_plan == null) {
						json_return.put("result", "1");
					} else {
						boolean result = planService.deletePlanById(str_delete_id);
						if (result) {
							json_return.put("result", "1");
						} else {
							json_return.put("result", "-1");
						}
					}
				} else {
					json_return.put("result", "0");
				}
				break;

			/**
			 * 获取历史位置
			 */
			case "getHistoryLocation":
				json_return.put("type", "getHistoryLocation");
				if (str_operation.equals("get")) {
					json_return.put("operation", "get");
					String str_get_id = request.getParameter("id");
					if (str_get_id == null) {
						json_return.put("result", "0");
					} else {
						List<Location> list_location = locationService.getLocationListById(str_get_id);
						//没有历史位置
						if (list_location == null) {
							json_return.put("result", "-2");
						} else {
							JSONObject json_temp = null;
							JSONArray json_list = new JSONArray();
							for (Location loc : list_location) {
								json_temp = new JSONObject();
								json_temp.put("space", loc.getSpace());
								json_temp.put("lat", loc.getLat());
								json_temp.put("lot", loc.getLot());
								json_temp.put("time", loc.getTime());
								json_list.put(json_temp);
							}
							json_return.put("locationList", json_list);
							json_return.put("result", "1");
						}
					}
				} else {
					json_return.put("result", "0");
				}
				break;
		}
		return json_return;
	}

	private void handleResponse(HttpServletResponse response) {
	}
}
