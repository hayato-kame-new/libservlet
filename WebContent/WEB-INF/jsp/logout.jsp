<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
  String userDeleteMsg =  (String)request.getAttribute("userDeleteMsg");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ログアウト</title>
<style>
 .msg {
   color: red;
 }
</style>
</head>
<body>
 <% if (userDeleteMsg != null) { %>
   <p class="msg"><%=userDeleteMsg %></p>
   <% } %>


<h4>スケジュール帳からログアウトしました。</h4>
<p>スケジュール帳をご利用頂くにはまずログインして頂く必要があります。ユーザー名とパスワードを入力してログインして下さい。</p>
<a href="./">トップへ</a>

</body>
</html>