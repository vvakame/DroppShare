<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>

<a href="/googlelogin.jsp">ログインとか</a><br />
<br />
アイコンとか消せます<br />
<form method="post" action="/admin">
	<input type="hidden" name="action" value="delete_icon" /><br />
	ファイル名:<input type="text" name="file_name" value="" /><br />
	<input type="submit" />
</form>
<br />
variantとか消せます<br />
<form method="post" action="/admin">
	<input type="hidden" name="action" value="delete_variant" /><br />
	screenName:<input type="text" name="screen_name" value="" /><br />
	variant:<input type="text" name="variant" value="" /><br />
	<input type="submit" />
</form>
<br />
oauthとか消せます<br />
<form method="post" action="/admin">
	<input type="hidden" name="action" value="delete_oauth" /><br />
	screenName:<input type="text" name="screen_name" value="" /><br />
	<input type="submit" />
</form>
sessionとか消せます<br />
<form method="post" action="/admin">
	<input type="hidden" name="action" value="delete_session" /><br />
	<input type="submit" />
</form>
