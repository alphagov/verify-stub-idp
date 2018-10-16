<#-- @ftlvariable name="" type="uk.gov.ida.saml.views.SamlRedirectView" -->
<!DOCTYPE html>
<html>
<head>
    <title>Saml Processing...</title>
    <style type='text/css'>
      body {
        padding-top: 2em;
        padding-left: 2em;
        background-color: white;
      }
      .verify-saml-form {
        font-family: Arial, sans-serif;
      }
      .verify-button {
        background-color: #00823b;
        color: #fff;
        padding: 10px;
        font-size: 1em;
        line-height: 1.25;
        border: none;
        box-shadow: 0 2px 0 #003618;
        cursor: pointer;
      }
      .verify-button:hover, .passport-verify-button:focus {
        background-color: #00692f;
      }
    </style>
</head>
<form class='verify-saml-form' action="${targetUri}" method="POST">
       <h1>Continue to next step</h1>
       <p>Because Javascript is not enabled on your browser, you must press the continue button</p>
    <input type="hidden" value="${body}" name="${samlMessageType}"/>
    <input type="hidden" value="${relayState}" name="RelayState"/>
    <#if showRegistration>
        <input type="hidden" value="${registration}" name="registration"/>
    </#if>
    <button class='verify-button' id="continue-button">Continue</button>
</form>
</body>
<script type="text/javascript" src="/assets/scripts/saml-redirect-auto-submit.js"></script>
</html>

