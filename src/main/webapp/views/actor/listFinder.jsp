<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="now" class="java.util.Date" />

<form:form action="${requestAction }" modelAttribute="finder"> 

	<form:hidden path="id"/>
	<form:hidden path="version"/>

	<acme:textbox path="keyWord" code="finder.keyword" />
	
	<acme:textbox path="position" code="finder.position" />
	
	
</form:form> 

<h3><spring:message code="player" /></h3>

<display:table name="players" id="row1" requestURI="${requestURI }" pagesize="${pagesize }" >

	<acme:column property="surnames" titleKey="actor.surnames" value= "${row1.surnames}"/>
	
	<acme:column property="name" titleKey="actor.name" value= "${row1.name}"/>
	
	<display:column titleKey="player.squad">
		<jstl:out value="${row1.squadNumber} - ${row1.squadName}" />
	</display:column>
	
	<acme:column property="team" titleKey="player.team" value= "${row1.team}"/>
	
	<jstl:if test="${language == 'en'}">
	<acme:column property="positionEnglish" titleKey="player.positionEnglish" value= "${row1.positionEnglish}"/>
	</jstl:if>
	<jstl:if test="${language == 'es'}">
	<acme:column property="positionSpanish" titleKey="player.positionSpanish" value= "${row1.positionSpanish}"/>
	</jstl:if>
	
	<display:column titleKey="player.injured"> 
		<spring:message code="player.${row1.injured }" />
	</display:column>
	
	<display:column titleKey="player.punished"> 
		<spring:message code="player.${row1.punished }" />
	</display:column>
	
</display:table>

<h3><spring:message code="manager" /></h3>

<display:table name="managers" id="row2" requestURI="${requestURI }" pagesize="${pagesize }" >

	<acme:column property="surnames" titleKey="actor.surnames" value= "${row2.surnames}"/>
	
	<acme:column property="name" titleKey="actor.name" value= "${row2.name}"/>
	
	<acme:column property="team" titleKey="manager.team" value= "${row2.team}"/>
	
</display:table>
