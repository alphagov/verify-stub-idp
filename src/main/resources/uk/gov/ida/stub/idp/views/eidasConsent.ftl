<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.EidasConsentView" -->
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
                <#--<span id="firstName">${firstName}</span>-->
            </li>
        <#if address??>
            <li>
                <label for="address">Address:</label>
                <#--<span id="address"><@addy address></@addy></span>-->
            </li>
        </#if>
            <li>
                <label for="loa">Level of assurance</label>
                <#--<span id="loa">${loa}</span>-->
            </li>
        </ul>
    </div>
    <div class="consent-details">
        <h3>Consent</h3>

        <p>Do you agree to allow ${name} to send your personal details to the relying party from which you are
            authenticating?</p>
    </div>
    <div class="submit">
        <form action="/${idpId}/consent" method="post">
            <input id="agree" class="button color-ok" type="submit" name="submit" value="I Agree"/>
        </form>
    </div>
</div>