<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.SingleIdpPromptPageView" -->
<div class="main">

    <h1>Demo services</h1>
    <p>
        To launch the demo of a journey started from IDP prompt (single IDP journey), use the demo service test GOV.UK Verify user journeys.
    </p>
    <p>
        All services in this environment enabled for a single IDP journey will display in a list below, read from the “get available services” API.<br />
        The API includes service category, service name and level of assurance.
    </p>
    <#assign currentCategory = "">
    <#list services as service>
        <#if service.serviceCategory != currentCategory>
            <h3>${service.serviceCategory}</h3>
            <#assign currentCategory = service.serviceCategory>
        </#if>
        <form method="post" action="${verifySubmissionUrl}">
            <input name="serviceId" value="${service.serviceId}" type="hidden" />
            <input name="idpEntityId" value="${idpId}" type="hidden" />
            <input name="singleIdpJourneyIdentifier" value="${uniqueId}" type="hidden" />
            <p><a href="javascript:document.forms[${service?index}].submit()">${service.name}</a><br />
            Level of assurance ${service.loa?remove_beginning("LEVEL_")}
            </p>
        </form>
    <#else>
        <div class="error-text">
            <b>No services currently available!</b>
        </div>
    </#list>
</div>
