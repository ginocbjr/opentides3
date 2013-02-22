<%@ page contentType="text/html;utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags"%>
<app:header pageTitle="label.user" />
    <!--Content-->
	<div id="user-body"  class="container-fluid">
		<div id="search-body">
	        <div id="search-panel" class="row-fluid">
	        	<form:form modelAttribute="searchCommand" id="user-search" cssClass="form-horizontal">
		        	<h3> <i class="icon-search"></i> <spring:message code="label.user.search" /> </h3> 
		        	<app:input path="firstName" label="label.user.name" />
					<input type="submit" class="btn btn-info" data-submit="search" value="<spring:message code="label.search"/>">
					<button type="button" class="btn btn-link" data-submit="clear"><spring:message code="label.clear" /></button>		
				</form:form>
	        </div>
	        
	        <hr/>
	
	        <div id="message-panel" class="row-fluid">
	            <div class="pull-left status"
	            		data-summary-message='<spring:message code="message.displaying-x-of-y" 
	            		arguments="#start,#end,#total,records"/>'>
	            	<app:paging results="${results}" />
	            </div>
	            <div class="pull-right">
	                <button id="user-add" class="btn btn-success add-action">
	                	<i class="icon-plus-sign icon-white"></i> <spring:message code="label.user.add" />
	                </button>
	            </div>
		    </div>
		    
	        <div class="clear"></div>                  
	
			<div id="results-panel" class="row-fluid" style="overflow:hidden">
				<div class="span12" style="width:200%;">	
	        	<table id="user-results" class="table table-bordered table-striped table-hover table-condensed" style="width:50%;">
					<thead>
	               	<tr class="table-header">
	                   	<th class="col-1" data-class="expand" data-field-name="completeName"><spring:message code="label.user.name"/></th>
	                	<th class="col-2" data-hide="phone" data-field-name="emailAddress"><spring:message code="label.user.email"/></th>
	               		<th class="col-3" data-hide="phone,tablet" data-field-name="displayGroups"><spring:message code="label.user.groups"/></th>
	               		<th class="col-4" data-hide="phone,tablet" data-field-name="credential.enabled"><spring:message code="label.user.active"/></th>
	                	<th class="col-5" data-field-name="ot3-controls"></th>
	                </tr>
	           		</thead>
	           		
	           		<tbody>
	            	<c:forEach items="${results.results}" var="record" varStatus="status">
	            	<tr data-id="${record.id}">            
	                	<td class="col-1"><c:out value="${record.completeName}" /></td>
	                	<td class="col-2"><c:out value="${record.emailAddress}" /></td>
	                	<td class="col-3"><c:out value="${record.displayGroups}" /></td>
	                	<td class="col-4"><c:out value="${record.credential.enabled}" /></td>
		                <td class="col-5">
		                	<i class='icon-edit edit-action' data-id='${record.id}'></i>	                	 
							<i class='icon-remove remove-action' data-id='${record.id}'></i>
		                </td>
	            	</tr>
	            	</c:forEach>
	            	</tbody>     
	            	
	            </table>
	            </div>
	            <div class="paging clearfix">
	            <app:paging results="${results}"/>
	            </div>
	   		</div>
   		</div> <!-- #search-body -->
   		
   		<div id="form-body" class="modal fade hide">
	   		<div id="form-panel">
			  	<div class="modal-header">
			   		<button type="button" class="close" data-dismiss="modal">&times;</button>
			   	 	<h3 class="${add}"><spring:message code="label.user.add" /></h3>		    
			   	 	<h3 class="${update}"><spring:message code="label.user.update" /></h3>			    
			  	</div>
				<form:form modelAttribute="formCommand" id="user-form">	
			  	<div class="modal-body">
					<app:input path="firstName" label="label.user.first-name" required="true"/>
					<app:input path="lastName" label="label.user.last-name" required="true"/>
					<app:input path="emailAddress" label="label.user.email" required="true"/>
					<app:password path="credential.newPassword" label="label.user.password" required="true"/>
					<app:password path="credential.confirmPassword" label="label.user.confirm-password" required="true"/>
					<div class="control-group">
						<form:label path="groups" cssClass="control-label"><spring:message code="label.user.groups" /></form:label>
						<div class="controls">
							<form:select path="groups" multiple="true">
								<form:options items="${userGroupsList}" itemLabel="name" itemValue="id"/>
							</form:select>
						</div>
					</div>
					<div class="control-group">
						<form:label path="credential.enabled" cssClass="control-label"><spring:message code="label.user.active" /></form:label>
						<div class="controls">
							<form:checkbox path="credential.enabled" />
						</div>
					</div>
			  	</div>
			 	<div class="modal-footer">
			    	<button type="button" class="btn btn-primary" data-submit="save"><spring:message code="label.save" /></button>
			    	<button type="button" class="btn btn-primary ${add}" data-submit="save-and-new"><spring:message code="label.save-and-new" /></button>
			    	<button type="button" class="btn" data-dismiss="modal"><spring:message code="label.close" /></button>
			    	<input type="hidden" name="id" />
			  	</div>
				</form:form>		  	
			</div>   		
		</div>

	</div>
<app:footer>
  <script type="text/javascript">
  	$(document).ready(function() {
  		// call footable first before hiding the table
  		// there is an bug in footable that hides other table elements  		
  		$("#user-body").RESTful();
/* 
  		$('.table').footable();
  		$('#user-search').jsonSearch();  		
  		$('#user-add').jsonForm();
 */  	});
  </script>
</app:footer>
