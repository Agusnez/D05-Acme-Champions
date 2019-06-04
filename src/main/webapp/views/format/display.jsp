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


<acme:display code="format.type" property="${format.type }" />

<acme:display code="format.maxTeams" property="${format.maximumTeams }" />

<acme:display code="format.minTeams" property="${format.minimumTeams }" />

<jstl:if test="${numberCompetitions==0}">
<acme:button name="edit" code="format.edit" onclick="javascript: relativeRedir('format/federation/edit.do?formatId=${format.id}');" />
</jstl:if>
<acme:button name="back" code="back" onclick="javascript: relativeRedir('format/federation/list.do');" />