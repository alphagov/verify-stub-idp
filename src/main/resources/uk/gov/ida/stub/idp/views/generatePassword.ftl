<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.GeneratePasswordView" -->
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>
    <meta charset="utf-8"/>
    <meta content="width=device-width,initial-scale=1.0" name="viewport"/>
    <link href="/assets/styles/application_auth.css" media="all"
          rel="stylesheet" type="text/css"/>
    <link href="/assets/styles/application_auth_extensions.css" media="all"
          rel="stylesheet" type="text/css"/>
    <link href="/assets/styles/jquery-ui.css" media="all"
          rel="stylesheet" type="text/css"/>
</head>
<body class="auth ">

<div class="main-container">

    <h3>IDA password generator.</h3>

    <p>Chose a username yourself. Remember or securely store the following password. Then email IDA the Hash, and your chosen username (<strong>NOT the password</strong>).</p>
    <p>You can refresh this page to get a different password and hash, just make sure to email us the correct hash for the password you chose to remember.</p>
    <dl>
        <dt>password</dt>
        <dd>${password}</dd>

        <dt>Hash</dt>
        <dd>${passwordHash}</dd>
    </dl>
</div>
</body>
</html>


