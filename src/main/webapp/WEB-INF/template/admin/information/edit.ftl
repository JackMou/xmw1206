<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="format-detection" content="telephone=no">
	<meta name="author" content="SHOP++ Team">
	<meta name="copyright" content="SHOP++">
	<title>${message("admin.information.edit")} - 北京芯梦国际科技有限公司</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/common/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/common/css/iconfont.css" rel="stylesheet">
	<link href="${base}/resources/common/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/common/css/awesome-bootstrap-checkbox.css" rel="stylesheet">
	<link href="${base}/resources/common/css/bootstrap-select.css" rel="stylesheet">
	<link href="${base}/resources/common/css/base.css" rel="stylesheet">
	<link href="${base}/resources/admin/css/base.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/common/js/html5shiv.js"></script>
		<script src="${base}/resources/common/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/common/js/jquery.js"></script>
	<script src="${base}/resources/common/js/bootstrap.js"></script>
	<script src="${base}/resources/common/js/bootstrap-growl.js"></script>
	<script src="${base}/resources/common/js/bootstrap-select.js"></script>
	<script src="${base}/resources/common/js/jquery.nicescroll.js"></script>
	<script src="${base}/resources/common/js/jquery.validate.js"></script>
	<script src="${base}/resources/common/js/jquery.validate.additional.js"></script>
	<script src="${base}/resources/common/js/jquery.form.js"></script>
	<script src="${base}/resources/common/js/jquery.cookie.js"></script>
	<script src="${base}/resources/common/js/lodash.js"></script>
	<script src="${base}/resources/common/js/URI.js"></script>
	<script src="${base}/resources/common/js/base.js"></script>
	<script src="${base}/resources/admin/js/base.js"></script>
	<script type="text/javascript">
	window.UEDITOR_HOME_URL = "${setting.siteUrl}/resources/plugin/ueditor/";
	window.SERVER_URL = "${base}";
	</script>
	<script type="text/javascript" charset="utf-8" src="${base}/resources/plugin/ueditor/ueditor.config.js"></script>
	<script type="text/javascript" charset="utf-8" src="${base}/resources/plugin/ueditor/ueditor.all.js"></script>
	<script type="text/javascript">
		UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
		UE.Editor.prototype.getActionUrl = function(action) {
			if (action == 'uploadimage') {
				return SERVER_URL + '/common/ueditor/upload_image';
			} else if (action == 'uploadfile') {
				return SERVER_URL + '/common/ueditor/upload_file';
			} else if (action == 'uploadvideo') {
				return SERVER_URL + '/common/ueditor/upload_video';
			} else{
				return this._bkGetActionUrl.call(this, action);
			}
		};
	</script>
	[#noautoesc]
		[#escape x as x?js_string]
			<script>
			$().ready(function() {
			
				var $articleForm = $("#articleForm");
				
				// 表单验证
				$articleForm.validate({
					rules: {
						title: "required",
					},
					submitHandler: function(form) {
						if($("#ueditorHtml").length == 1){
							var content = UE.getEditor('ueditorDiv').getContent();
							$("#ueditorHtml").val(content);
						}
						$(form).ajaxSubmit({
							successRedirectUrl: "${base}/admin/information/list"
						});
					}
				});
				if (UE != null) {
					var ue = UE.getEditor('ueditorDiv');
		            ue.ready(function(){
		            	var csrfToken = $.cookie("csrfToken");
		            	ue.execCommand('serverparam','csrfToken',csrfToken);
		            	ue.setContent($("#ueditorHtml").val());
		            	ue.setHeight('300px');
		            });
				}
			});
			</script>
		[/#escape]
	[/#noautoesc]
</head>
<body class="admin">
	[#include "/admin/include/main_header.ftl" /]
	[#include "/admin/include/main_sidebar.ftl" /]
	<main>
		<div class="container-fluid">
			<ol class="breadcrumb">
				<li>
					<a href="${base}/admin/index">
						<i class="iconfont icon-homefill"></i>
						${message("common.breadcrumb.index")}
					</a>
				</li>
				<li class="active">${message("admin.information.edit")}</li>
			</ol>
			<form id="articleForm" class="ajax-form form-horizontal" action="${base}/admin/information/update" method="post">
				<input  type="hidden" name="id" value="${information.id}"/>
				<div class="panel panel-default">
					<div class="panel-heading">${message("admin.information.edit")}</div>
					<div class="panel-body">
						<!--标题-->
						<div class="form-group">
							<label class="col-xs-3 col-sm-2 control-label item-required" for="title">${message("Information.title")}:</label>
							<div class="col-xs-9 col-sm-4">
								<input id="title" name="title" class="form-control" type="text" maxlength="200" value="${information.title}">
							</div>
						</div>
						<!--发布者-->
						<div class="form-group">
							<label class="col-xs-3 col-sm-2 control-label" for="author">${message("Information.author")}:</label>
							<div class="col-xs-9 col-sm-4">
								<input id="author" name="author" class="form-control" type="text" maxlength="200" value="${information.author}">
							</div>
						</div>
						<div class="form-group">
							<label class="col-xs-3 col-sm-2 control-label">${message("common.setting")}:</label>
							<div class="col-xs-9 col-sm-4">
								<div class="checkbox checkbox-inline">
									<input name="_isPublication" type="hidden" value="false">
									<input id="isPublication" name="isPublication" type="checkbox" value="true" value="true" [#if information.isPublication] checked[/#if]>
									<label for="isPublication">${message("Information.isPublication")}</label>
								</div>
								<div class="checkbox checkbox-inline">
									<input name="_isTop" type="hidden" value="false">
									<input id="isTop" name="isTop" type="checkbox" value="true" [#if information.isTop] checked[/#if]>
									<label for="isTop">${message("Information.isTop")}</label>
								</div>
							</div>
						</div>
						<!--内容简介-->
						<div class="form-group">
							<label class="col-xs-3 col-sm-2 control-label" for="seoDescription">${message("Information.seoDescription")}:</label>
							<div class="col-xs-9 col-sm-4">
								<!--<input id="seoDescription" name="seoDescription" class="form-control" type="text" maxlength="200">-->
								<textarea id="seoDescription" name="seoDescription" class="form-control" row="4" >${information.seoDescription}</textarea>
							</div>
						</div>
						<!--内容-->
						<div class="form-group">
							<label class="col-xs-3 col-sm-2 control-label">${message("Information.content")}:</label>
							<div class="col-xs-9 col-sm-10">
								<div id="ueditorDiv" style="width:100%;height:300px;"></div>
								<textarea name="content" style="display:none;" id="ueditorHtml">${information.content}</textarea>
							</div>
						</div>
					</div>
					<div class="panel-footer">
						<div class="row">
							<div class="col-xs-9 col-sm-10 col-xs-offset-3 col-sm-offset-2">
								<button class="btn btn-primary" type="submit">${message("common.submit")}</button>
								<button class="btn btn-default" type="button" data-action="back">${message("common.back")}</button>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</main>
</body>
</html>