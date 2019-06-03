<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table name="actors" id="row" requestURI="${requestURI }" pagesize="5">

	<acme:column property="userAccount.username" titleKey="actor.username" value= "${row.userAccount.username}: "/>
	
	<jstl:if test="${admin}">
		<display:column> 
			<a href="actor/administrator/profile/displayActor.do?actorId=${row.id}"><spring:message code="actor.display"/></a>
		</display:column>
	</jstl:if>
	
</display:table>	

<acme:button name="back" code="actor.cancel" onclick="javascript: relativeRedir('welcome/index.do');" />