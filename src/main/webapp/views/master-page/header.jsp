<%--
 * header.jsp
 *
 * Copyright (C) 2019 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<div>
	<a href="#"><img src="${banner}" alt="Acme Champions Co., Inc." width="489" height="297" /></a>
</div>

<div>
	<ul id="jMenu">
		<!-- Do not forget the "fNiv" class for the first level links !! -->
		<security:authorize access="hasRole('ADMIN')">
			<li><a class="fNiv"><spring:message	code="master.page.administrator" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="configuration/administrator/edit.do"><spring:message code="master.page.configuration" /></a></li>
					<li><a href="broadcast/administrator/create.do"><spring:message code="master.page.broadcast" /></a></li>	
					<li><a href="actor/administrator/profile/list.do"><spring:message code="master.page.profiles" /></a></li>
					<li><a href="administrator/create.do"><spring:message code="master.page.signUpAdmin" /></a></li>
					<li><a href="administrator/createPresident.do"><spring:message code="master.page.signUpPresident" /></a></li>
					<li><a href="administrator/dashboard.do"><spring:message code="master.page.dashboard" /></a></li>
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('MANAGER')">
			<li><a class="fNiv"><spring:message	code="master.page.manager" /></a>
				<ul>
					<li class="arrow"></li>

					<li><a href="calendar/manager/show.do"><spring:message code="master.page.calendar" /></a></li>			
					<li><a href="hiring/manager/list.do"><spring:message code="master.page.hiring" /></a></li>	
					<li><a href="team/president,manager/listByManager.do"><spring:message code="master.page.team.display" /></a></li>	
					<li><a href="report/manager/list.do"><spring:message code="master.page.reports" /></a></li>		
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('PRESIDENT')">
			<li><a class="fNiv"><spring:message	code="master.page.president" /></a>
				<ul>
					<li class="arrow"></li>

					<li><a href="finder/president/find.do"><spring:message code="master.page.finder" /></a></li>	
					<li><a href="hiring/president/list.do"><spring:message code="master.page.hiring" /></a></li>		
					<li><a href="signing/president/list.do"><spring:message code="master.page.signing" /></a></li>	
					<li><a href="team/president/display.do"><spring:message code="master.page.team.display" /></a></li>
					<li><a href="report/president/listPlayers.do"><spring:message code="master.page.playerReports" /></a></li>


				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('PLAYER')">
			<li><a class="fNiv"><spring:message	code="master.page.player" /></a>
				<ul>
					<li class="arrow"></li>

					<li><a href="history/player/display.do"><spring:message code="master.page.history" /></a></li>	
					<li><a href="signing/player/list.do"><spring:message code="master.page.signing" /></a></li>		
					<li><a href="training/player/list.do"><spring:message code="master.page.list.trainings" /></a></li>
					
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('SPONSOR')">
			<li><a class="fNiv"><spring:message	code="master.page.sponsor" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="sponsorship/sponsor/list.do"><spring:message code="master.page.sponsorships" /></a></li>	

				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('REFEREE')">
			<li><a class="fNiv"><spring:message	code="master.page.referee" /></a>
				<ul>
					<li class="arrow"></li>	
					<li><a href="game/referee/listMyGames.do"><spring:message code="master.page.my.games" /></a></li>	
				</ul>
			</li>
		</security:authorize>
		
		<security:authorize access="hasRole('FEDERATION')">
			<li><a class="fNiv"><spring:message	code="master.page.federation" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="format/federation/list.do"><spring:message code="master.page.formats" /></a></li>	
					<li><a href="competition/federation/list.do"><spring:message code="master.page.competition" /></a></li>	
				</ul>
			</li>
		</security:authorize>
		
	
		<security:authorize access="isAnonymous()">
			<li><a class="fNiv" href="security/login.do"><spring:message code="master.page.login" /></a></li>
			<li><a class="fNiv" href="register/createPlayer.do"><spring:message code="master.page.signup.player" /></a></li>
			<li><a class="fNiv" href="register/createManager.do"><spring:message code="master.page.signup.manager" /></a></li>
			<li><a class="fNiv" href="register/createReferee.do"><spring:message code="master.page.signup.referee" /></a></li>
			<li><a class="fNiv" href="register/createSponsor.do"><spring:message code="master.page.signup.sponsor" /></a></li>
			<li><a class="fNiv" href="register/createFederation.do"><spring:message code="master.page.signup.federation" /></a></li>
			
		</security:authorize>
		
		<security:authorize access="permitAll()">
			<li><a class="fNiv" href="game/listAll.do"><spring:message code="master.page.game.listAll" /></a></li>
		</security:authorize>
		
		<security:authorize access="isAuthenticated()">
			<li>
				<a class="fNiv"> 
					<spring:message code="master.page.profile" /> 
			        (<security:authentication property="principal.username" />)
				</a>
				<ul>
					<li><a href="profile/displayPrincipal.do"><spring:message code="master.page.profile" /></a></li>
					<li><a href="box/actor/list.do"><spring:message code="master.page.message" /> </a></li>
					<li><a href="data/get.do"><spring:message code="master.page.get.data" /> </a></li>	
					<li><a href="j_spring_security_logout"><spring:message code="master.page.logout" /> </a></li>
				</ul>
			</li>
		</security:authorize>
	</ul>
</div>

<div>
	<a href="?language=en">en</a> | <a href="?language=es">es</a>
</div>

