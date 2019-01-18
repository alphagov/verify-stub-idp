<div class="main">
    <h1>Welcome to ${name}</h1>
    <#if userLoggedIn>
        <p>Welcome ${userFullName}</a></p>
        <p><a href="${rootPrefix}/${idpId}/logout${routeSuffix}">Logout</a></p>
    <#else>
        <p><a href="${rootPrefix}/${idpId}/register${routeSuffix}/pre-register">Create ${anOrA} ${name} identity account</a></p>
        <p><a href="${rootPrefix}/${idpId}/login${routeSuffix}">Log In</a></p>
    </#if>
    <p><a href="${rootPrefix}/${idpId}/start-prompt${routeSuffix}">List of available single-idp services</a></p>
    <p><a href="#">Do other stuff with this IDP</a> (which will just leave you on this page because it's a stub-idp)</p>
</div>
