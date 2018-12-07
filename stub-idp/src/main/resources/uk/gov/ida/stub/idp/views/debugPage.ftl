<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.DebugPageView" -->
<div class="main">
    <div class="tabs">
        <ul>
            <li>
                <a id="tab-login" class="tab-text" href="/${idpId}/login">Login</a>
            </li>
            <li>
                <a id="tab-register" class="tab-text" href="/${idpId}/register">Register</a>
            </li>
            <li class="on" id="tab-debug">
                <span class="tab-text">System information</span>
            </li>
        </ul>
    </div>

    <h2>System information</h2>

    <p id="idp-session-id">
        Stub-IDP sessionId is "${sessionId}".
    </p>



    <#if idaAuthnRequestFromHub??>
        <p id="registration">
            <#if registration.isPresent() >
                "registration" hint is "${registration.get()?string}"
            <#else>
                "registration" hint not received
            </#if>
        </p>

        <p id="language-hint">
            <#if languageHint?has_content>
                The language hint was set to "${languageHint}".
            <#else>
                No language hint was set.
            </#if>
        </p>

        <#if knownHints??>
            <#list knownHints>
              <p>The following known hints were sent from the GOV.UK Verify hub:</p>
              <ul class="known-hints">
                <#items as hint>
                <li>${hint}</li>
                </#items>
              </ul>
            </#list>
        </#if>

        <#if unknownHints??>
            <#list unknownHints>
              <p>The following unknown hints were sent:</p>
              <ul class="unknown-hints">
                <#items as hint>
                <li>${hint}</li>
                </#items>
              </ul>
            </#list>
        </#if>

        <#if !knownHints?has_content && !unknownHints?has_content>
          <p>No answer hints were sent.</p>
        </#if>

        <p id="authn-request-comparision-type">
            AuthnRequest comparison type is "${comparisonType}".
        </p>

        <#list authnContexts>
            <p>The following AuthnContexts were sent:</p>
            <ul class="authn-contexts">
            <#items as authnContext>
            <li>${authnContext}</li>
            </#items>
            </ul>
        </#list>

        <p id="authn-request-issuer">
            Request issuer is "${authnRequestIssuer}".
        </p>

        <p id="saml-request-id">
            Request Id is "${samlRequestId}".
        </p>

        <p id="relay-state">
            Relay state is "${relayState}".
        </p>

        <p id="single-idp-journey-id">
            <#if singleIdpJourneyId?has_content>
                This a single IDP journey and the single IDP UUID is "${singleIdpJourneyId}".
            <#else>
                This is not a single IDP journey.
            </#if>
        </p>
    </#if>
</div>
