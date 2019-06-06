<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="competition/federation/edit.do" modelAttribute="competition">
	
	<form:hidden path="id" />
	<form:hidden path="version" />
	
	<acme:textbox code="competition.nameTrophy" path="nameTrophy" obligatory="true"/>
	
	<acme:textbox code="competition.startDate" path="startDate" placeholder="yyyy/MM/dd" obligatory="true"/>
	
	<form:label path="formatId">
		<spring:message code="competition.format"></spring:message>
	</form:label>
	<form:select path="formatId" >
		<form:options items="${formatMap}"/>
	</form:select>
	<form:errors cssClass="error" path="formatId"></form:errors>
	<br />
	
	<spring:message code="competition.confirmation" var="confirmation" />
	<input type="submit" name="save" value="<spring:message code="competition.save"/>" onClick="confirm('${confirmation}')" />
	
	<acme:cancel code="competition.cancel" url="competition/federation/list.do" />


</form:form>    