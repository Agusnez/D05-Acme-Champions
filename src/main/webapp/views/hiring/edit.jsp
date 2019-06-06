<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="${enlace}" modelAttribute="hiring">
	
	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="managerId" />
	
	<jstl:if test="${hiring.id == 0 }">
	
		<acme:textbox code="hiring.price" path="price" obligatory="true"/>
		
	</jstl:if>
	
	<jstl:if test="${hiring.id != 0 }">
		
		<acme:textbox code="hiring.mandatoryComment" path="mandatoryComment" obligatory="false"/>
		
	</jstl:if>
	
	<acme:submit name="save" code="hiring.save" />
	
	<jstl:if test="${hiring.id != 0 }">
	
		<acme:cancel code="hiring.cancel" url="hiring/${autoridad}/list.do" />
		
	</jstl:if>
	
	<jstl:if test="${hiring.id == 0 }">
		
		<acme:cancel code="hiring.cancel" url="finder/president/find.do" />
		
	</jstl:if>


</form:form>    