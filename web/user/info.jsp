<%--
  Created by IntelliJ IDEA.
  User: Lenovo
  Date: 2022/4/27
  Time: 19:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<div class="col-md-9">
  <div class="data_list">
    <div class="data_list_title"><span class="glyphicon glyphicon-edit"></span>&nbsp;个人中心 </div>
    <div class="container-fluid">
      <div class="row" style="padding-top: 20px;">
        <div class="col-md-8">
          <%--
                表单类型 enctype="multipart/form-data"
                提交方式 method=“POST”
          --%>
          <form class="form-horizontal" method="post" action="user" enctype="multipart/form-data">
            <div class="form-group">
              <%--隐藏域存放用户行为actionName--%>
              <input type="hidden" name="actionName" value="updateUser">
              <label for="nickName" class="col-sm-2 control-label">昵称:</label>
              <div class="col-sm-3">
                <input class="form-control" name="nick" id="nickName" placeholder="昵称" value="${user.nick}">
              </div>
              <label for="img" class="col-sm-2 control-label">头像:</label>
              <div class="col-sm-5">
                <input type="file" id="img" name="img">
              </div>
            </div>
            <div class="form-group">
              <label for="mood" class="col-sm-2 control-label">心情:</label>
              <div class="col-sm-10">
                <textarea class="form-control" name="mood" id="mood" rows="3">${user.mood}</textarea>
              </div>
            </div>
            <div class="form-group">
              <div class="col-sm-offset-2 col-sm-10">
                <%--表单校验--%>
                <button type="submit" id="btn" class="btn btn-success" onclick="return updateUser();">修改</button>&nbsp;&nbsp;
                <span style="color:red;font-size: 12px" id="msg"></span>
              </div>
            </div>
          </form>
        </div>
        <div class="col-md-4"><img style="width:240px;height:180px" src="user?actionName=userHead&imageName=${user.head}"></div>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
  /*
   验证昵称的唯一性
     昵称文本框的失焦事件 blur
            1、获取昵称文本框的值
            2、判断值是否为空
                如果为空，提示用户，禁用按钮，并return
            3、判断昵称是否修改
                从session作用域中获取用户昵称（如果在js中想要使用el表达式获取请求对象，js需要写在jsp页面中，无法写在js文件中）
                如果获取到的用户昵称与session作用域中的一致，return
            4、如果昵称做了修改
                发送ajax请求后台，验证昵称是否可用
                    如果不可用，提示用户，禁用按钮
                    如果可用，清空提示信息，按钮可用
  * */
  $("#nickName").blur(function () {
    //1、获取昵称文本框的值
    var nickName = $("#nickName").val();
    //2、判断值是否为空
    //如果为空，提示用户，禁用按钮，并return
    if(isEmpty(nickName)){
      $("#msg").html("用户昵称不能为空！");
      $("#btn").prop("disabled", true);
      return;
    }
    //3、判断昵称是否修改
    //从session作用域中获取用户昵称（如果在js中想要使用el表达式获取请求对象，js需要写在jsp页面中，无法写在js文件中）
    //如果获取到的用户昵称与session作用域中的一致，return
    var nick = '${user.nick}';
    if(nickName == nick){
      return;
    }
    //4、如果昵称做了修改
    //发送ajax请求后台，验证昵称是否可用
    $.ajax({
      type:"get",
      url:"user",
      data:{
        actionName:"checkNick",
        nick:nickName
      },
      success:function (result) {
        //如果可用，清空提示信息，按钮可用
        if(result == 1){
          //1、清空提示信息
          $("#msg").html("");
          //2、按钮可用
          $("#btn").prop("disabled", false);
          return;
        }else {
          //如果不可用，提示用户，禁用按钮
          //1、显示提示信息
          $("#msg").html("该昵称已存在，请重新输入！");
          //2、按钮不可用
          $("#btn").prop("disabled", true);
          return;
        }



      }
    })

  }).focus(function () {
    //1、清空提示信息
    $("#msg").html("");
    //2、按钮可用
    $("#btn").prop("disabled", false);
    return;
  });

  /*
  * 表单提交校验
  *   满足条件，返回true，表示提交表单
  *   不满足条件，返回false，表示不提交表单
  * */
  function updateUser() {
    //1、获取昵称文本框的值
    var nickName = $("#nickName").val();
    //2、判断值是否为空
    //如果为空，提示用户，禁用按钮，并return
    if(isEmpty(nickName)){
      $("#msg").html("用户昵称不能为空！");
      $("#btn").prop("disabled", true);
      return false;//不提交
    }
    //唯一性
  }
</script>
</html>
