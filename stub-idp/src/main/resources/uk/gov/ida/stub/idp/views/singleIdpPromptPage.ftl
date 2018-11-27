<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.SingleIdpPromptPageView" -->
<#include "address.ftl">
<div class="main">

    <h1>Demo services</h1>
    <#if idpUser?? >
        <div>
            <h3>Your pre-registered details</h3>
            <ul>
                <li>
                    <label for="firstName">First Name:</label>
                    <span id="firstName">${firstName}</span>
                </li>
                <li>
                    <label for="surname">Surname:</label>
                    <span id="surname">${surname}</span>
                </li>
                <li>
                    <label for="dateOfBirth">Date of Birth:</label>
                    <span id="dateOfBirth">${dateOfBirth}</span>
                </li>
                 <li>
                    <label for="gender">Gender:</label>
                    <span id="gender">${gender}</span>
                </li>
            <#if address??>
                <li>
                    <label for="address">Address:</label>
                    <span id="address"><@addy address></@addy></span>
                </li>
            </#if>
                <li>
                    <label for="loa">Level of assurance</label>
                    <span id="loa">${loa}</span>
                </li>
            </ul>
        </div>
    </#if>
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
            <input type="submit" class="link-button" value="${service.name}"/><br />
            Level of assurance ${service.loa?remove_beginning("LEVEL_")}
            </p>
        </form>
    <#else>
        <div class="error-text">
            <b>No services currently available!</b>
        </div>
    </#list>
</div>
<hr>
<small>Manually initiate the journey</small>
<form method="post" action="${verifySubmissionUrl}">
    <input name="serviceId" placeholder="serviceId" />
    <input name="idpEntityId" placeholder="idpEntityId" />
    <input name="singleIdpJourneyIdentifier" value="${uniqueId}" />
    <input type="submit" value="Initiate Single IDP journey" />
</form>

