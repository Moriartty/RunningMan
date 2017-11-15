package com.login.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.login.bean.Activity;
import com.login.service.ActivityService;
import com.login.utils.JsonUtils;

public class CreateActivityServlet extends HttpServlet {
	/**
	 * Constructor of the object.
	 */
	public CreateActivityServlet() {
		super();
		System.out.println("CreateActivityServlet()");
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
		System.out.println("post = ");

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		ActivityService service = new ActivityService();
		
		
		String myResponse=null;
		try {
			String content=request.getParameter("content");
			Activity ac;
			ac=JsonUtils.json2Activity(content);
			myResponse =service.insertActivity(ac);
			System.out.println("createActivity--->"+myResponse);
			/*switch (index) {
				case 0:
					String content=request.getParameter("content");
					Activity ac;
					ac=JsonUtils.json2Activity(content);
					myResponse =service.insertActivity(ac);
					System.out.println("addActivity--->"+myResponse);
					break;
				case 1:
					String userUid=request.getParameter("useruid");
					String acUid=request.getParameter("acuid");
					myResponse =service.deleteActivity(userUid,acUid);
					System.out.println("deleteActivity--->"+ myResponse);
					break;
				case 2:
					String uuid=request.getParameter("useruid");
					myResponse =service.queryMyActivities(uuid);
					System.out.println("queryActivities--->"+ myResponse);
					break;
				default:
					break;
			}*/
			
			response.setContentType("application/json; charset=GBK");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(myResponse);
			
			//response.setCharacterEncoding("UTF-8");
			/*OutputStream out = response.getOutputStream();
			out.write(myResponse);
			out.flush();
			out.close();*/
		} catch(Exception e){
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
