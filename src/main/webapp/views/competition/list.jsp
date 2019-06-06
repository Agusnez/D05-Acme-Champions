<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<jsp:useBean id="now" class="java.util.Date" />



<display:table name="competitions" id="row" requestURI="${requestURI }" pagesize="5">

	<spring:message code="dateFormat" var="format"/>
	<display:column titleKey="competition.startDate"> 
		<fmt:formatDate type="date" value="${row.startDate }" pattern="${format}" />
	</display:column>
	
	<acme:column property="nameTrophy" titleKey="competition.nameTrophy" value= "${row.nameTrophy}: "/>
	
	<display:column titleKey="competition.teams">
		<c:forEach items="${row.teams}" var="team">
    		${team.name}
    		<br>
		</c:forEach>
	</display:column>
	
	<acme:column property="format.type" titleKey="competition.format.type" value= "${row.format.type}: "/>
		
	<display:column>
		<jstl:if test="${!row.closed}">
			<jstl:if test="${row.startDate > now}">
				<a href="competition/federation/close.do?competitionId=${row.id}"><spring:message code="competition.close"/></a>
				<a href="competition/federation/listAddTeam.do?competitionId=${row.id}"><spring:message code="competition.addTeams"/></a>
			</jstl:if>	
		</jstl:if>
	</display:column>
	

	</display:table>
	
	<jstl:if test="${teamError }">
		<div class="error"><spring:message code="competition.error" /></div>
		<br>
	</jstl:if>
	
	<a href="competition/federation/create.do"><spring:message code="competition.create"/></a>
		
	<acme:button name="back" code="competition.back" onclick="javascript: relativeRedir('welcome/index.do');" />




