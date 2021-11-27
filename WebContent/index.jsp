<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%

// UserServletから、セッションがなかった時にこのindex.jspに戻ってくるときのエラーメッセージをリクエストスコープから取得する
  String userRegistFailureMsg =  (String) request.getAttribute("userRegistFailureMsg");

//エラーなら 再入力を行うようにします フォームに入力してあったものを表示する
// LoginCheckServletでエラー発生の時 このindex.jspへ戻ってくる エラーメッセージをリクエストスコープから取得する
String action = (String)request.getAttribute("action");
String loginFailureMsg = "";
String mail = "";
List<String> errMsgList = null;
if(action != null && action.equals("re_enter")) {
 // 失敗した時のメッセージ UserServletから、フォワードしてくる時にリクエストスコープに保存したので、取得する
   loginFailureMsg = (String) request.getAttribute("loginFailureMsg");
 // 失敗した時にフォームに入力してあったのを表示
 // String flat_password = (String)request.getAttribute("flat_password");  // これは表示しないでおく要らないセキュリティのため
 mail = (String)request.getAttribute("mail");
 errMsgList = (List<String>)request.getAttribute("errMsgList");  // バリデーションに引っかかったエラーのメッセージが入ってる

}

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>スケジュール管理</title>
<style>
.err {
  color: red;
}
</style>
</head>
<body>

  <h2>スケジュール帳へようこそ</h2>
  <hr>
<!-- このログイン画面は、ログイン処理で失敗した場合の遷移先にもなっている また、新規登録処理失敗でも遷移先となっている
  失敗するとメッセージが取得でき そのメッセージを表示しています -->
  <% if (loginFailureMsg != null) { %>
    <p><%=loginFailureMsg %></p>
  <%} %>
   <% if (userRegistFailureMsg != null) { %>
    <p><%=userRegistFailureMsg %></p>
  <%} %>

  <div >
  <h4>ログインフォーム</h4>
  <%
    if(errMsgList != null) {
    for(String errMsg : errMsgList) {
  %>
    [&nbsp;<span class="err"><%=errMsg %>&ensp;</span>&nbsp;]
  <%
    }
    }
  %>
    <form action="/libservlet/LoginCheckServlet" method="post">
    <!-- LoginCheckServletで、セッションを作ってセッションスコープにUserBeanインスタンスを保存して、ログインをしていることにしてます -->
      <table>
         <tr>
          <th >メールアドレス</th>
          <td><input type="email" name="mail" value="<%=mail %>" size="32"></td>
        </tr>
        <tr>
          <th >パスワード</th>
          <td><input type="password" name="flat_password" value="" size="32"></td>
        </tr>
        <tr>
          <td colspan="2">
            <input type="submit" value="ログイン">
            <input type="reset" value="リセット">
          </td>
        </tr>
      </table>
    </form>
  </div>

  <div>
  <!-- フォームの  method属性のデフォルト値は GET です  method="get" は書かなくても良い
  formタグで送る理由は、サーブレットに行ってから、jspファイルにアクセスしたいから。サーブレット経由にしないと、フィルターで戻される
  フィルターで全てのサーブレットとjspに、最初にアクセスすると、index.jspへ転送するようにしてるのでaリンクでアクセスできない
  aリンクでは送れないので、(HTTPメソッドのGETメソッドでuser_registration.jspにリンクでできない aリンクは HTTPメソッドのGET) フィルターで、index.jspに転送処理されてしまう
  -->
    <p>まだ、ユーザー登録されていない方は登録してください</p>
    <form action="/libservlet/UserFormServlet" >
      <input type="hidden" name="action" value="add" />
    <!-- UserFormServletでセッションを作って、セッションスコープに新規のUserBeanを保存することによって、ログインの状態と同じことを意味しています -->
       <input type="submit" value="新規登録画面へ">
    </form>
  </div>
</body>
</html>