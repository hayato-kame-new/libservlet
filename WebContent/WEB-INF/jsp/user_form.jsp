<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.UserBean, java.util.List" %>

<%
String action = (String)request.getAttribute("action");  // "add" "edit" "delete" が入ってる
String re_enter = (String)request.getAttribute("re_enter");

/* String title = action.equals("add") ? "新規登録" : "編集"; */
String title = "";
if(action.equals("add")) {
  title = "新規";
} else if(action.equals("edit")) {
  title = "編集";
} else if(action.equals("delete")) {
  title = "削除";
}
//  request も session も、JSPで使える暗黙オブジェクトです
// 新規ではUserFormサーブレットでは、セッションスコープに、UserBean空のインスタンス(各フィールドは、各データ型の規定値になってる) が保存されてます
// 編集では、セッションスコープに、UserBeanインスタンスがあるので、それを使う
// セッションスコープから、取り出して、フォームに使う セッションスコープにUserBeanインスタンスがあることで、フィルターでindex.jspへ転送されない

// もし、バリデーションエラーや、データベースエラーの時  再入力を行うようにします
String form_msg = "";
String name = "";
Integer str_roll = 0;
int roll = 0;  // 初期値
String mail = "";
int id = 0;
 UserBean userBean = (UserBean)session.getAttribute("userBean");
if(action.equals("add") || action.equals("edit") || action.equals("delete")) {
  name = userBean.getName();
  if(name == null) {
      name = "";
    }
  roll = userBean.getRoll();
  mail = userBean.getMail();
  if(mail == null) {
      mail = "";
    }
  id = userBean.getId();
}

List<String> errMsgList = null;
if(re_enter != null && re_enter.equals("re_enter")) {
  // 失敗した時のメッセージ UserServletから、フォワードしてくる時にリクエストスコープに保存したので、取得する
   form_msg = (String)request.getAttribute("form_msg");
  // 失敗した時にフォームに入力してあったのを表示
   name = (String)request.getAttribute("name");
  // String flat_password = (String)request.getAttribute("flat_password");  // これは表示しないでおく要らない
  str_roll = (Integer)request.getAttribute("roll");
   roll = str_roll.intValue();
  mail = (String)request.getAttribute("mail");
  errMsgList = (List<String>)request.getAttribute("errMsgList");  // バリデーションに引っかかったエラーのメッセージが入ってる
  action = (String)request.getAttribute("action");
  id = (Integer)request.getAttribute("id");
}


if(form_msg.equals("このユーザには登録してあるスケジュールが存在するため、ユーザー情報を削除できませんでした")) {

}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザ<%=title %></title>
<style>
.err {
  color: red;
}
</style>
</head>
<body>

   <div >
  <h4>ユーザ<%=title %>画面</h4>
  <%
    if (form_msg != null) {
  %>
  <p><%= form_msg %></p>
  <%
    }if(form_msg.equals("このユーザには登録してあるスケジュールが存在するため、ユーザー情報を削除できませんでした")) {
  %>
 <!--  <p><a href="">スケジュール全件を確認する</a></p> -->
  <% }
    if(errMsgList != null) {
    for(String errMsg : errMsgList) {
  %>
    [&nbsp;<span class="err"><%=errMsg %>&ensp;</span>&nbsp;]
  <%
    }
    }
  %>
    <form action="/libservlet/UserServlet" method="post">

      <input type="hidden" name="action" value="<%=action %>" />
      <input type="hidden" name="id" value="<%=id %>" />
      <table>
        <tr>
          <th >ユーザ名</th>
          <td ><input type="text" name="name" value="<%=name %>" size="32"  ></td>
        </tr>
        <tr>
          <th >パスワード</th>
          <td>
          <% if(!action.equals("delete")) {%>
             <small>※パスワードは 6文字以上、10文字以下の半角英数字で入力してください</small><br />
              <% } %>
              <input type="password" name="flat_password" value="" size="32">
          </td>
        </tr>
        <tr>
          <th >メールアドレス</th>
          <td>
          <% if(!action.equals("delete")) {%>
              <small>※メールアドレスの形式で入力してください(例: aaa@bbb.com)</small><br />
               <% } %>
              <input type="email" name="mail" value="<%=mail %>" size="32">
          </td>
        </tr>
        <tr>
          <th >権限</th>
          <td >
            <select name="roll">
              <% if (roll == 0) { %>
              <option value="0" selected>一般</option>
              <option value="1">管理者</option>
              <% } else if(roll == 1) { %>
               <option value="0" >一般</option>
               <option value="1" selected>管理者</option>
              <% } else {%>
               <!-- <option value="-1" >選択してください</option> -->
               <!-- disabled属性をつけた optionタグは選択できないようになる -->
               <option disabled value="" >選択してください</option>
               <option value="0" >一般</option>
               <option value="1" >管理者</option>
               <% } %>
            </select>
          </td>
        </tr>
        <tr>
          <td colspan="2">
          <%
          String submitStr = "";
          if(action.equals("add")) {
              submitStr = "新規登録します";
          } else if(action.equals("edit")) {
            submitStr = "編集します";
          } else if(action.equals("delete")) {
            submitStr = "削除します";
          }
          %>
            <input type="submit" value="<%=submitStr %>">
             <% if(!action.equals("delete")) {%>
            <input type="reset" value="リセット">
             <% } %>
          </td>
        </tr>
      </table>
    </form>
  </div>

</body>
</html>