<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<!-- _login_page_ --><!--登录超时标记 勿删-->
<html>

	<head>
	<meta name="decorator" content="ani"/>
		<title>${fns:getConfig('productName')} 登录</title>
		<script>
			if (window.top !== window.self) {
				window.top.location = window.location;
			}
		</script>
		<script type="text/javascript">
				$(document).ready(function() {
					$("#loginForm").validate({
						rules: {
							validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
						},
						messages: {
							username: {required: "请填写用户名."},password: {required: "请填写密码."},
							validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
						},
						errorLabelContainer: "#messageBox",
						errorPlacement: function(error, element) {
							error.appendTo($("#loginError").parent());
						} 
					});
				});
				// 如果在框架或在对话框中，则弹出提示并跳转到首页
				if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0){
					alert('未登录或登录超时。请重新登录，谢谢！');
					top.location = "${ctx}";
				}
		</script>
	
	</head>

	
	<body>
		

		<div class="login-page">
		<div class="row">
			<div class="col-md-4 col-lg-4 col-md-offset-4 col-lg-offset-4">
				<img  class="img-circle" src="${ctxStatic}/common/images/flat-avatar.png" class="user-avatar" />
				<h1>Jeeplus</h1>
				<sys:message content="${message}" showType="1"/>
				<form id="loginForm" role="form" action="${ctx}/login" method="post">
					<div class="form-content">
						<div class="form-group">
							<input type="text" id="username" name="username" class="form-control input-underline input-lg required"  value="admin" placeholder="用户名">
						</div>

						<div class="form-group">
							<input type="password" id="password" name="password" value="admin" class="form-control input-underline input-lg required" placeholder="密码">
						</div>
						<c:if test="${isValidateCodeLogin}">
						<div class="form-group  text-muted">
								<label class="inline"><font color="white">验证码:</font></label>
							<sys:validateCode name="validateCode" inputCssStyle="margin-bottom:5px;" buttonCssStyle="color:white"/>
						</div>
						</c:if>
							<ul class="pull-right btn btn-info btn-circle" style="background-color:white;height:45px;width:46px">	
								<li class="dropdown color-picker" >
									<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
										<span><i class="fa fa-circle"></i></span>
									</a>
									<ul class="dropdown-menu pull-right animated fadeIn" role="menu">
										<li class="padder-h-xs">
											<table class="table color-swatches-table text-center no-m-b">
												<tr>
													<td class="text-center colorr">
														<a href="#" data-theme="blue" class="theme-picker">
															<i class="fa fa-circle blue-base"></i>
														</a>
													</td>
													<td class="text-center colorr">
														<a href="#" data-theme="green" class="theme-picker">
															<i class="fa fa-circle green-base"></i>
														</a>
													</td>
													<td class="text-center colorr">
														<a href="#" data-theme="red" class="theme-picker">
															<i class="fa fa-circle red-base"></i>
														</a>
													</td>
												</tr>
												<tr>
													<td class="text-center colorr">
														<a href="#" data-theme="purple" class="theme-picker">
															<i class="fa fa-circle purple-base"></i>
														</a>
													</td>
													<td class="text-center color">
														<a href="#" data-theme="midnight-blue" class="theme-picker">
															<i class="fa fa-circle midnight-blue-base"></i>
														</a>
													</td>
													<td class="text-center colorr">
														<a href="#" data-theme="lynch" class="theme-picker">
															<i class="fa fa-circle lynch-base"></i>
														</a>
													</td>
												</tr>
											</table>
										</li>
									</ul>
								</li>
						</ul>
						<label class="inline">
								<input  type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''} class="ace" />
								<span class="lbl"> 记住我</span>
						</label>
					</div>
					<input type="submit" class="btn btn-white btn-outline btn-lg btn-rounded progress-login"  value="登录">
					&nbsp;
					<a href="${ctx}/sys/register" class="btn btn-white btn-outline btn-lg btn-rounded progress-login">注册</a>
				</form>
			</div>			
		</div>
	</div>
	
	<script>

		
$(function(){
		$('.theme-picker').click(function() {
			changeTheme($(this).attr('data-theme'));
		}); 	
	
});

function changeTheme(theme) {
	$('<link>')
	.appendTo('head')
	.attr({type : 'text/css', rel : 'stylesheet'})
	.attr('href', '${ctxStatic}/common/css/app-'+theme+'.css');
	//$.get('api/change-theme?theme='+theme);
	 $.get('${pageContext.request.contextPath}/theme/'+theme+'?url='+window.top.location.href,function(result){  });
}
</script>
<style>
li.color-picker i {
    font-size: 24px;
    line-height: 30px;
}
.red-base {
    color: #D24D57;
}
.blue-base {
    color: #3CA2E0;
}
.green-base {
    color: #27ae60;
}
.purple-base {
    color: #957BBD;
}
.midnight-blue-base {
    color: #2c3e50;
}
.lynch-base {
    color: #6C7A89;
}
</style>
</body>
</html>