<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="model.UserBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%
/* LoginCheckServlet で ユーザの名前とパスワードが会ってれば、このwelcome.jsp ページにくる */
  // セッションスコープからログインユーザ情報を取得  フィルターで判断するために必要 セッションスコープから取り出す
 //  ユーザ新規登録した後も、リダイレクトしてくるので セッションから取得します
  UserBean userBean = (UserBean) session.getAttribute("userBean");

String userAddMsg = (String)session.getAttribute("userAddMsg");
String userEditMsg = (String)session.getAttribute("userEditMsg");

%>
<html>
<head>
<meta charset="UTF-8">
<title>スケジュール帳へようこそ</title>
</head>
<body>
<h2>スケジュール帳へようこそ</h2>
<hr>
<% if( userBean != null ) { %>

  <% if (userAddMsg != null) { %>
   <p><%=userAddMsg %></p>
   <% } %>
    <% if (userEditMsg != null) { %>
   <p><%=userEditMsg %></p>
   <% } %>
  <p>ログインに成功しました。</p>
  <p>ようこそ<%= userBean.getName() %>さん</p>

 <!--  自分が登録したスケジュールだけ見れるようにする ?以降はクエリー文字列です aリンクはGETアクセスなのでクエリー文字列で遅れます
    クエリー文字列でユーザーの 主キーidを送ります-->
<%--   <a href="/LocalDateTimeSchedule/MonthDisplayServlet?mon=current&id=<%=userBean.getId() %>" >今月表示</a><br />

 --%>
<!-- ユーザ編集画面へ 編集の時には、もうすでにセッションに自分のUserBeanインスタンスが保存されてるので(ログインの時に保存されてるし、新規登録の時にも保存されてる)
クエリー文字列にユーザの idは必要なし-->

 <a href="/libservlet/UserFormServlet?action=edit" >ユーザー情報編集</a><br />

<a href="/libservlet/UserFormServlet?action=delete" >ユーザー情報削除</a>
<% } %>
</body>
</html>