package com.datacollector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class TokenStatusServlet
 */
@WebServlet("/UserEventStatus")
public class UserEventStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserEventStatusServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		Gson gson = new GsonBuilder().create();
		
		String username = request.getParameter("username");
		String event = request.getParameter("event");
		String verify = request.getParameter("verifier");
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			TestingConnectionSource myConnectionSource = new TestingConnectionSource();
			
			Connection dbConn = myConnectionSource.getDatabaseConnection();
			
			String eventQuery = "SELECT * FROM `openDataCollectionServer`.`Event` INNER JOIN `openDataCollectionServer`.`EventContact` ON `openDataCollectionServer`.`Event`.`event` = `openDataCollectionServer`.`EventContact`.`event` WHERE `openDataCollectionServer`.`Event`.`event` = ?";
			
			String desc = "";
			String start = "";
			String end = "";
			String password = "";
			ArrayList contactName = new ArrayList();
			ArrayList contacts = new ArrayList();
			try
			{
				PreparedStatement queryStmt = dbConn.prepareStatement(eventQuery);
				queryStmt.setString(1, event);
				ResultSet myResults = queryStmt.executeQuery();
				if(!myResults.next())
				{
					return;
				}
				desc = myResults.getString("description");
				start = myResults.getString("start");
				end = myResults.getString("end");
				password = myResults.getString("password");
				contactName.add(myResults.getString("name"));
				contacts.add(myResults.getString("contact"));
				while(myResults.next())
				{
					contactName.add(myResults.getString("name"));
					contacts.add(myResults.getString("contact"));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(!verify.equals(password))
			{
				System.out.println("Challenge unacceptable");
				return;
			}
			
			String query = "SELECT * FROM `openDataCollectionServer`.`UserList` WHERE `event` = ? AND `username` = ?";
			
			PreparedStatement toInsert = dbConn.prepareStatement(query);
			toInsert.setString(1, event);
			toInsert.setString(2, username);
			ResultSet myResults = toInsert.executeQuery();
			if(!myResults.next())
			{
				System.out.println("no such user");
				HashMap outputMap = new HashMap();
				outputMap.put("result", "nokay");
				String output = gson.toJson(outputMap);
				response.getWriter().append(output);
				return;
			}
			HashMap outputMap = new HashMap();
			outputMap.put("result", "ok");
			outputMap.put("username", username);
			outputMap.put("event", event);
			String output = gson.toJson(outputMap);
			response.getWriter().append(output);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
