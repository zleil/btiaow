<%@ page import="com.btiao.tzsc.service.*"%>
<%@ page import="java.lang.Long"%>

<%@ page contentType="text/html; charset=UTF-8"  language="java" %>

<!DOCTYPE html> 
<html>

<body>

<H3>ComDataMgr.instance(WPState.getQuestionPersistFileName(100000001)).getall():</H3>
<p>
<%=ComDataMgr.instance(WPState.getQuestionPersistFileName(100000001)).getall()%>
</p>

<H3>ComDataMgr.instance("UserInfo", 65537):</H3>
<p>
<%=ComDataMgr.instance("UserInfo", 65537).getall()%>
</p>

<H3>ComDataMgr.<Long,WPState>instance(MetaDataId.deletewpPrefix+"oQZIBj4Gbn__DoSZwcdKe3SKt4BE", 65537):</H3>
<p>
<%=ComDataMgr.<Long,WPState>instance(MetaDataId.deletewpPrefix+"oQZIBj4Gbn__DoSZwcdKe3SKt4BE", 65537).getall()%>
</p>

<H3>StateMgr.instance(65537):</H3>
<p>
<%=StateMgr.instance(65537).getall()%>
</p>

<H3>SessionMgr.instance(65537):</H3>
<p>
<%=SessionMgr.instance(65537).getall()%>
</p>

</body>
</html>