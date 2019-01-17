<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.IdpPageView" -->
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>
    <meta charset="utf-8"/>
    <meta content="width=device-width,initial-scale=1.0" name="viewport"/>
    <meta name="robots" content="noindex">
    <link href="/assets/styles/application_auth.css" media="all"
          rel="stylesheet" type="text/css"/>
    <link href="/assets/styles/application_auth_extensions.css" media="all"
          rel="stylesheet" type="text/css"/>
    <link href="/assets/styles/jquery-ui.css" media="all"
          rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/assets/lib/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/assets/lib/jquery.validate.min.js"></script>
    <script type="text/javascript" src="/assets/lib/jquery-ui.min.js"></script>
    <link rel="shortcut icon" href="/assets/images/providers/${assetId}.png">
</head>
<body class="auth ">
<header>
    <div class="container" id="auth">
        <div class="logo">
            <img src="/assets/images/providers/${assetId}.png" alt="${name}" height="142px" />
        </div>
    </div>
</header>
<div class="main-container">
    <#include "./${subPageTemplateName}">
</div>

<hr>
<footer>
    <div class="container" id="links" style="text-align: center;">
        <a href="https://github.com/alphagov/verify-stub-idp">Source Code</a> - Built by the Government Digital Service
    </div>
</footer>
</body>
</html>
