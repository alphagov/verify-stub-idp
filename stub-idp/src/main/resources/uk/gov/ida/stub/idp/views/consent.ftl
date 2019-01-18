<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.ConsentView" -->
<#include "address.ftl">
<div class="consent">
    <h2>
        You've successfully authenticated with ${name}
    </h2>

    <div>
        <h3>Personal Details</h3>
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
    <div class="consent-details">
        <h3>Consent</h3>

        <p>Do you agree to allow ${name} to send your personal details to the relying party from which you are
            authenticating?</p>

        <#if userLOADidNotMatch == true>
        <div class="consent-loa-too-low" id="userLOADidNotMatch">
            <strong style="color:red"><em>WARNING: ${loaMismatchMessage}.</em></strong>
        </div>
        </#if>

    </div>
    <div class="submit">
        <form action="${rootPrefix}/${idpId}/consent${routeSuffix}" method="post">
            <input id="agree" class="button color-ok" type="submit" name="submit" value="I Agree"/>
            <input id="refuse" class="button color-cancel" type="submit" name="submit" value="I Refuse"/>
            <!-- this is used by the performance tests and acceptance tests -->
            <input id="randomPid" name="randomPid" type="hidden" value="false"/>
        </form>
    </div>
</div>
