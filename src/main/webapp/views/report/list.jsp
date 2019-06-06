<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<display:table name="reports" pagesize="5" id="row1" requestURI="${requestUri}">

	<spring:message code="dateFormat" var="format"/>
	<spring:message code="timeFormat" var="formatTime"/>
	<display:column titleKey="report.moment"> 
		<fmt:formatDate type="date" value="${row1.moment }" pattern="${format}" />
		<fmt:formatDate type="time" value="${row1.moment }" pattern="${formatTime}" />
	</display:column>
	
	
	<acme:column property="description" titleKey="report.description" value= "${row1.description} "/>
	
	<display:column titleKey="report.player">
		<jstl:out value="${row1.player.squadNumber} - ${row1.player.squadName}" />
	</display:column>
	
	
	<acme:url href="report/manager/delete.do?reportId=${row1.id}" code="report.delete"/>

	
</display:table>

<a href="team/president,manager/listByManager.do"><spring:message code="report.create"/></a>
<br><br>
<acme:cancel code="report.back" url="welcome/index.do" />