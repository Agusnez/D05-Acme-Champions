<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<security:authorize access="hasRole('REFEREE')">
<jstl:if test="${requestURI == 'game/referee/listMyGames.do' }">
<h3><spring:message code="games.ended" /></h3>

<display:table name="gamesEnded" id="row2" requestURI="${requestURI }" pagesize="5">

	<spring:message code="dateFormat" var="format"/>
	<spring:message code="timeFormat" var="formatTime"/>
	<display:column titleKey="game.gameDate"> 
		<fmt:formatDate type="date" value="${row2.gameDate }" pattern="${format}" />
		<fmt:formatDate type="time" value="${row2.gameDate }" pattern="${formatTime}" />
		
	</display:column>
	
	<acme:column property="place" titleKey="game.place" value= "${row2.place}: "/>
	
	<display:column titleKey="game.friendly"> 
			<spring:message code="game.${row2.friendly }" />
	</display:column>
	
	<acme:column property="homeTeam.name" titleKey="game.homeTeam" value= "${row2.homeTeam.name}: "/>
	
	<acme:column property="visitorTeam.name" titleKey="game.visitorTeam" value= "${row2.visitorTeam.name}: "/>
	
	<acme:column property="referee.name" titleKey="game.referee" value= "${row2.referee.name}: "/>
	
	<security:authorize access="hasRole('REFEREE')">
		<jstl:if test="${row2.gameDate<Date()}">
		<acme:url href="minutes/referee/create.do?gameId=${row2.id}" code="game.create.minutes" />
		</jstl:if>
	</security:authorize>

	</display:table>



<h3><spring:message code="games.notStarted" /></h3>
</jstl:if>
</security:authorize>
<display:table name="games" id="row" requestURI="${requestURI }" pagesize="5">

	<spring:message code="dateFormat" var="format"/>
	<spring:message code="timeFormat" var="formatTime"/>
	<display:column titleKey="game.gameDate"> 
		<fmt:formatDate type="date" value="${row.gameDate }" pattern="${format}" />
		<fmt:formatDate type="time" value="${row.gameDate }" pattern="${formatTime}" />
		
	</display:column>
	
	<acme:column property="place" titleKey="game.place" value= "${row.place}: "/>
	
	<display:column titleKey="game.friendly"> 
			<spring:message code="game.${row.friendly }" />
	</display:column>
	
	<acme:column property="homeTeam.name" titleKey="game.homeTeam" value= "${row.homeTeam.name}: "/>
	
	<acme:column property="visitorTeam.name" titleKey="game.visitorTeam" value= "${row.visitorTeam.name}: "/>
	
	<acme:column property="referee.name" titleKey="game.referee" value= "${row.referee.name}: "/>

	</display:table>
		
	<acme:button name="back" code="game.back" onclick="javascript: relativeRedir('welcome/index.do');" />




