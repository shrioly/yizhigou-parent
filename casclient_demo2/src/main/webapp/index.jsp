<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>9002易直购</title>
</head>
<body>
<h1>欢迎来到9002易直购</h1><br>
<%=request.getRemoteUser()%><%--获取远程登录名--%><br>
<a href="http://localhost:9100/cas/logout?service=http://localhost:9100/cas/login">退出登录</a>
</body>
</html>