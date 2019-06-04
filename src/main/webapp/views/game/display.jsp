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


<jstl:if test="${haveMinutesClosed}">
<h2 style="color: blue;"><spring:message code="game.winner" />: 
<jstl:if test="${minutesByGame.winner!=null }">
${minutesByGame.winner.name}
</jstl:if>
<jstl:if test="${minutesByGame.winner==null }">
<spring:message code="game.tie" />
</jstl:if>
</h2>
<h3>
<spring:message code="game.result" />: ${minutesByGame.homeScore}-${minutesByGame.visitorScore}<br>
<spring:message code="game.score" />: <c:forEach items="${minutesByGame.playersScore}" var="playersScore">
    ${playersScore.squadNumber} - ${playersScore.squadName} |
</c:forEach><br>
<spring:message code="game.yellow" />: <c:forEach items="${minutesByGame.playersYellow}" var="playersYellow">
    ${playersYellow.squadNumber} - ${playersYellow.squadName} |
</c:forEach><br>
<spring:message code="game.red" />: <c:forEach items="${minutesByGame.playersRed}" var="playersRed">
    ${playersRed.squadNumber} - ${playersRed.squadName} |
</c:forEach>
</h3>
</jstl:if>

<spring:message code="dateFormat" var="format"/>
<spring:message code="timeFormat" var="formatTime"/>
	
<div><spring:message code="game.gameDate" />:
<fmt:formatDate type="date" value="${game.gameDate}" pattern="${format}"/>
<fmt:formatDate type="time" value="${game.gameDate}" pattern="${formatTime}"/>
</div>

<acme:display code="game.place" property="${game.place }" />

<spring:message code="game.friendly" />: <spring:message code="game.${game.friendly }" />


<acme:display code="game.homeTeam" property="${game.homeTeam.name }" />

<acme:display code="game.visitorTeam" property="${game.visitorTeam.name }" />

<acme:display code="game.referee" property="${game.referee.surnames }" />

<jstl:if test="${sponsorships!=null}">
	<div>		
		<c:forEach items="${sponsorships}" var="item">
    		<fieldset>
				<a target="_blank" href="${item.target}"><img src="${item.banner }" alt="Banner" width="10%" height="10%" /></a>
			</fieldset>
    		<br>
</c:forEach>
	</div>
</jstl:if> 

<acme:button name="back" code="game.back" onclick="javascript: relativeRedir('game/listAll.do');" />

<security:authorize access="hasRole('REFEREE')">
<acme:button name="back" code="game.back.actor" onclick="javascript: relativeRedir('game/referee/listMyGames.do');" />
</security:authorize>

<security:authorize access="hasRole('MANAGER')">
<acme:button name="back" code="game.back.manager" onclick="javascript: relativeRedir('calendar/manager/show.do');" />
</security:authorize>


