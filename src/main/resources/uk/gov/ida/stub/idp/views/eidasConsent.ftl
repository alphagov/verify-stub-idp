<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.EidasConsentView" -->
<#include "eidasAddress.ftl">
<div class="consent">
    <h2>
        You've successfully authenticated with ${name}
    </h2>

    <div>
        <h3>Personal Details</h3>
        <ul>
            <li>
                <label for="firstName">First Name:</label>
                <span id="firstName">${user.firstName}</span>
            </li>
            <li>
                <label for="familyName">Family Name:</label>
                <span id="familyName">${user.familyName}</span>
            </li>
            <li>
                <label for="dateOfBirth">Date of Birth:</label>
                <span id="dateOfBirth">${user.dateOfBirth}</span>
            </li>
        <#if user.gender.isPresent()>
            <li>
                <label for="gender">Gender:</label>
                <span id="gender">${user.gender.get().getValue()}</span>
            </li>
        </#if>
        <#if user.address??>
            <li>
                <label for="address">Address:</label>
                <span id="address"><@addy user.address></@addy></span>
            </li>
        </#if>
        </ul>
    </div>
    <div class="consent-details">
        <h3>Consent</h3>

        <p>Do you agree to allow ${name} to send your personal details to the relying party from which you are
            authenticating?</p>
    </div>
    <div class="submit">
        <form action="/eidas/${idpId}/consent" method="post">
            <input id="agree" class="button color-ok" type="submit" name="submit" value="I Agree"/>
        </form>
    </div>
</div>
