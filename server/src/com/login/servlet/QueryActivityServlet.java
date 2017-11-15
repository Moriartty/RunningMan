package com.login.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.login.bean.Activity;
import com.login.service.ActivityService;
import com.login.utils.JsonUtils;

public class QueryActivityServlet extends HttpServlet{
	
	public QueryActivityServlet() {
		super();
		System.out.println("QueryActivityServlet()");
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("get = ");
		response.setContentType("text/html");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("post = ");

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		ActivityService service = new ActivityService();
		
		String myResponse=null;
		try {
			String uuid=request.getParameter("useruid");
			myResponse =service.queryMyActivities(uuid);
			System.out.println("queryActivities--->"+ myResponse);
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
