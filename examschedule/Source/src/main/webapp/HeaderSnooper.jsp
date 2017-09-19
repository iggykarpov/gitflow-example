<%@page import="java.util.*" %>
<%@page import="java.io.*" %>

<HTML><HEAD><TITLE> Test Web Agent Headers </TITLE></HEAD>
<BODY BGCOLOR=#ffffff>
<TABLE BORDER=1>
    <TR>
        <TD VALIGN=TOP>
            <B>Variable</B>
        </TD>
        <TD VALIGN=TOP>
            <B>Value</B>
        </TD>
    </TR>
<%
        Enumeration en = request.getHeaderNames();
        while (en.hasMoreElements()) {
                String key = (String)en.nextElement();
                String value = request.getHeader(key);
%>
    <TR>
        <TD VALIGN=TOP>
            <%=key%>
        </TD>
        <TD VALIGN=TOP>
            <%=value%>
        </TD>
<%
        }
%>
     </TR>
   </TABLE>

</BODY>

</HTML>