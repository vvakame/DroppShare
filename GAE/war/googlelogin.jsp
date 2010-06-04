<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>

<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<html>
<body>
<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user != null) {
%>
username:
<%=user.getNickname()%>
<a href="<%=userService.createLogoutURL(request.getRequestURI())%>">サインアウト</a>
<%
	} else {
%>
<a href="<%=userService.createLoginURL(request.getRequestURI())%>">サインイン</a>
<%
	}
%>
</body>
</html>