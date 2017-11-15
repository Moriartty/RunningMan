package com.login.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.login.bean.User;
import com.login.service.Service;
import com.login.utils.JsonUtils;

public class LoginServlet extends HttpServlet {
	public LoginServlet() {
		super();
		System.out.println("LoginServlet()");
	}
	
	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("get = ");
		response.setContentType("text/html");
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("name = ");

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		String username = null;
		String password = null;
		User user = new User();
		Service service = new Service();
		
		/**
		 * index 用于标记将要进行什么操作， 0——用户登录 ;1——用户注册;2——查找该用户名是否已经存在； 
		 * 
		 */
		
		JsonObject myResponse=null;
		try {
			String content = request.getParameter("content");
			user = JsonUtils.json2User(content);
			username = user.getUsername();
			password = user.getPassword();
			myResponse =service.canLogin(username, password);
			System.out.println("login--->"+ username + password);
			/*switch (index) {
				case 0://login
					username = user.getUsername();
					password = user.getPassword();
					myResponse =service.canLogin(username, password);
					System.out.println("login--->"+ username + password);
					break;
				case 1://register
					myResponse=service.checkUsername(user.getUsername());
					//如果该用户名未注册
					if(!myResponse.get("returnState").getAsBoolean()){
						myResponse = service.register(user);
						System.out.println("register--->"+ user.getUsername() + user.getPassword());
					}
					break;
				case 2://checkusername
					username = user.getUsername();
					myResponse =service.checkUsername(username);
					System.out.println("checkUser--->"+ username);
					break;
				default:
					break;
			}*/
			response.setContentType("application/json; charset=GBK");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().print(myResponse.toString());
			
			//response.setCharacterEncoding("UTF-8");
			/*OutputStream out = response.getOutputStream();
			out.write(myResponse);
			out.flush();
			out.close();*/
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		System.out.println("init()");
	}
}
