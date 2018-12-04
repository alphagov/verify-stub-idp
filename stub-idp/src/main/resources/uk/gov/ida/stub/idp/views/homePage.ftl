<div class="main">
    <h1>Welcome to ${name}</h1>
    <#if userLoggedIn>
        <p>Welcome ${userFullName}</a></p>
        <p><a href="/${idpId}/logout">Logout</a></p>
    <#else>
        <p><a href="/${idpId}/register/pre-register">Create ${anOrA} ${name} identity account</a></p>
        <p><a href="/${idpId}/login">Log In</a></p>
    </#if>
    <p><a href="/${idpId}/start-prompt">List of available single-idp services</a></p>
    <p><a href="#">Do other stuff with this IDP</a> (which will just leave you on this page because it's a stub-idp)</p>
</div>