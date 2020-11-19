package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject obj = new JSONObject();
		if (session != null) {
			DBConnection connection = DBConnectionFactory.getConnection();
			String userId = session.getAttribute("user_id").toString();
			try {
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			} catch (Exception e) {
				e.printStackTrace();
			}
			connection.close();
		} else {
			try {
				obj.put("status", "Invalid Session");
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setStatus(403);
		}
		RpcHelper.writeJsonObject(response, obj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
	
			String userId = input.getString("user_id");
	
			String password = input.getString("password");
			
			DBConnection connection = DBConnectionFactory.getConnection();
			JSONObject obj = new JSONObject();
			if (connection.verifyLogin(userId, password)) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				session.setMaxInactiveInterval(600);
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			} else {
				obj.put("status", "User Doesn't Exist");
				response.setStatus(401);
			}
			connection.close();
			RpcHelper.writeJsonObject(response, obj);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
