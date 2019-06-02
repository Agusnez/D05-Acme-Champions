<%@page language="java" contentType="text/html; charset=ISO-8859-1"
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


<%-- <security:authorize access="isAuthenticated()">
	<security:authentication property="principal.username" var="user" />
</security:authorize> --%>

<jstl:if test="${admin}">
	<div>
		<jstl:if test="${!(actor eq principal)}">
			<a href="actor/administrator/profile/deleteProfile.do?actorId=${actor.id}"><spring:message code="actor.deleteProfile"/></a>
			<br/>
		</jstl:if>
	</div>
</jstl:if>


<security:authorize access="hasRole('FEDERATION')">
	
	<div><spring:message code="federation.establishmentDate" />:
		<spring:message code="dateFormat" var="format"/>
		<fmt:formatDate value="${actor.establishmentDate }" pattern="${format}" />
	</div>
	
</security:authorize>

<acme:display code="actor.name" property="${actor.name }" />

<acme:display code="actor.surnames" property="${actor.surnames }" />

<spring:message code="actor.photo"/>: <br> <img src="${actor.photo }" width="10%" height="10%"/> <br>

<acme:display code="actor.email" property="${actor.email }" />

<acme:display code="actor.phone" property="${actor.phone }" />

<acme:display code="actor.address" property="${actor.address }" />

<security:authorize access="hasRole('SPONSOR')">

	<h2><spring:message code="creditCard.data" /></h2>
	
	<acme:display code="actor.creditCard.holderName" property="${actor.creditCard.holderName }" />
	
	<acme:display code="actor.creditCard.make" property="${actor.creditCard.make }" />
	
	<acme:display code="actor.creditCard.number" property="${actor.creditCard.number }" />
		
	<acme:display code="actor.creditCard.expMonth" property="${actor.creditCard.expMonth }" />
		
	<acme:display code="actor.creditCard.expYear" property="${actor.creditCard.expYear }" />
		
	<acme:display code="actor.creditCard.cvv" property="${actor.creditCard.cvv }" />
</security:authorize> 
	
<security:authorize access="hasRole('PLAYER')">

	<acme:display code="player.buyoutClause" property="${actor.buyoutClause }" />
	
	<acme:display code="player.squadNumber" property="${actor.squadNumber }" />
	
	<acme:display code="player.squadName" property="${actor.squadName }" />
	
	<jstl:if test="${language == 'es'}">
		<acme:display code="player.positionSpanish" property="${actor.positionSpanish }" />
	</jstl:if>
	<jstl:if test="${language == 'en'}">
		<acme:display code="player.positionEnglish" property="${actor.positionEnglish }" />
	</jstl:if>
	
	<h3><spring:message code="player.statisticalData" /></h3>

	<acme:display code="statisticalData.yellowCards" property="${statisticalData.yellowCards}" />

	<acme:display code="statisticalData.redCards" property="${statisticalData.redCards}" />

	<acme:display code="statisticalData.goals" property="${statisticalData.goals}" />

	<acme:display code="statisticalData.matchsPlayed" property="${statisticalData.matchsPlayed}" />

	<acme:display code="statisticalData.accumulatedYellowCard" property="${statisticalData.accumulatedYellowCard}" /><br>
	
</security:authorize> 	

<jstl:if test="${!admin}">

	<security:authorize access="isAuthenticated()">

		<acme:button name="edit" code="actor.edit" onclick="javascript: relativeRedir('profile/edit.do');" />

	</security:authorize>
	
	<acme:button name="back" code="actor.back" onclick="javascript: relativeRedir('welcome/index.do');" />
</jstl:if>
<jstl:if test="${admin}">
	<acme:button name="back" code="actor.back" onclick="javascript: relativeRedir('actor/administrator/profile/list.do');" />
</jstl:if>


	

