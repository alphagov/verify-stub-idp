<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.SingleIdpPromptPageView" -->
<div class="main">

    <h1>Choose a service</h1>
    <#assign currentCategory = "">
    <#list services as service>
        <#if service.serviceCategory != currentCategory>
            <h2>${service.serviceCategory}</h2>
            <#assign currentCategory = service.serviceCategory>
        </#if>
        <form method="post" action="${verifySubmissionUrl}">
            <input name="serviceId" value="${service.serviceId}" type="hidden" />
            <input name="idpEntityId" value="${idpId}" type="hidden" />
            <input name="singleIdpJourneyIdentifier" value="${uniqueId}" type="hidden" />
            <div>
                <h3><a href="javascript:document.forms[${service?index}].submit()">${service.name}</a></h3>
                <p>
                    <b>Level of Assurance:</b> ${service.loa}
                </p>
            </div>
            <div class="submit">
                <input class="button color-ok" type="submit" name="submit-button" value="Go"/>
            </div>
        </form>
        <hr />
    <#else>
        <div class="error-text">
            <b>No services currently available!</b>
        </div>
    </#list>
</div>
