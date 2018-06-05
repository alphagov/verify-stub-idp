<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.EidasLoginPageView" -->
<div class="main">
    <div class="tabs">
        <ul>
            <li class="on">
                <span id="tab-login" class="tab-text">Login</span>
            </li>
            <li>
                <a id="tab-register" class="tab-text" href="/eidas/${idpId}/register">Register</a>
            </li>
        </ul>
    </div>
    <form action="/eidas/${idpId}/login" class="login" method="post">
        <fieldset>
            <legend>European ID Login</legend>
            <p>Use your existing username and password to access your European ID.</p>

            <div class="error-text">${errorMessage}</div>

            <div>
                <label for="username">Username / Email address<abbr title="Required">*</abbr>
                </label>
                <input class="field medium" name="username" id="username" type="text"/>
                <em>Note about this field</em>
            </div>
            <div>
                <label for="password">Password</label>
                <input class="field medium" name="password" id="password" type="password"/>
                <em>Note about this field</em>
            </div>
            <div class="submit">
              <!--  <a class="forgot" href="#">Forgotten Password?</a> -->
                <input id="login" class="button color-ok" type="submit" name="submit" value="SignIn"/>
            </div>
        </fieldset>
    </form>
    <form action="/eidas/${idpId}/login/authn-failure" method="post">
        <fieldset>
            <legend>Submit Login Authn Failure</legend>
            <div class="submit">
                <input id="authnFailureSubmit" class="button color-ok" type="submit" name="submit" value="Authn Failure"/>
            </div>
        </fieldset>
    </form>
</div>
