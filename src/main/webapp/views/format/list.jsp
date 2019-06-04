<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>



<display:table name="formats" id="row" requestURI="${requestURI}" pagesize="5">
	
	<acme:column property="type" titleKey="format.type" value= "${row.type}: "/>
	
	<acme:column property="maximumTeams" titleKey="format.maxTeams" value= "${row.maximumTeams}: "/>
	
	<acme:column property="minimumTeams" titleKey="format.minTeams" value= "${row.maximumTeams}: "/>
	
	<acme:url href="format/federation/display.do?formatId=${row.id}" code="format.display" />
	
	</display:table>
		
	<acme:button name="back" code="format.new" onclick="javascript: relativeRedir('format/federation/create.do');" />
	
	<acme:button name="back" code="back" onclick="javascript: relativeRedir('welcome/index.do');" />




