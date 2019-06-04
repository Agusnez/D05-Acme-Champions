<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="format/federation/edit.do" modelAttribute="format">
	
	<form:hidden path="id" />
	<form:hidden path="version" />
	
	<spring:message code="format.choose" />: 
	<form:select path="type">
		<spring:message code="league" var="league"/>
		<form:option label="${league}" value="LEAGUE"/>
		<spring:message code="tournament" var="tournament"/>
		<form:option label="${tournament}" value="TOURNAMENT"/>
	</form:select>
	<form:errors path="type" cssClass="error" />

	
	<acme:textbox code="format.minTeams" path="minimumTeams"  obligatory="true"/>
	
	<acme:textbox code="format.maxTeams" path="maximumTeams"  obligatory="true"/>
	
	<br>
	
	<acme:submit name="save" code="save" />
	<jstl:if test="${format.id!=0 }">
	<jstl:if test="${canDelete!=0 }">
	<acme:submit name="delete" code="delete" />
	</jstl:if>
	</jstl:if>
	
	<acme:cancel code="back" url="format/federation/list.do" />
	
</form:form>  