<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="${enlace}" modelAttribute="message">
	
	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="recipientId" />
	<form:hidden path="senderId" />
	
	<acme:textbox code="message.subject" path="subject" obligatory="true"/>

	<acme:textarea code="message.body" path="body" obligatory="true"/>
		
	<form:label path="priority">
			<spring:message code="message.priority"/>
		</form:label>
		<form:select path="priority" >
			<form:option label="Low" value="LOW"/>
			<form:option label="Neutral" value="NEUTRAL"/>
			<form:option label="High" value="HIGH"/>
		</form:select>
		<form:errors cssClass="error" path="priority"></form:errors>
		<br />
	
	<acme:textbox code="message.tags" path="tags" />
	
	<acme:submit name="save" code="message.save" />
	
	<acme:cancel code="message.cancel" url="box/actor/list.do" />
	
	<jstl:if test="${message.id != 0}">
	<acme:submit name="delete" code="message.delete" />
	</jstl:if>	


</form:form>  