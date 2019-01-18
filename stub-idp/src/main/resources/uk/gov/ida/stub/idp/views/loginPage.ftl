<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.LoginPageView" -->
<div class="main">
    <div class="tabs">
        <ul>
            <li class="on">
                <span id="tab-login" class="tab-text">Login</span>
            </li>
            <li>
                <a id="tab-register" class="tab-text" href="${rootPrefix}/${idpId}/register${routeSuffix}">Register</a>
            </li>
            <li>
                <a id="tab-debug" class="tab-text" href="${rootPrefix}/${idpId}/debug${routeSuffix}">System information</a>
            </li>
        </ul>
    </div>
    <form action="${rootPrefix}/${idpId}/login${routeSuffix}" class="login" method="post">
        <fieldset>
            <legend>Verified ID Login</legend>
            <p>Use your existing username and password to complete your Verified ID transaction.</p>

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
                <input id="cancel" class="button  color-cancel" type="submit" name="submit" value="Cancel" />
            </div>
        </fieldset>
    </form>
    <form action="${rootPrefix}/${idpId}/login${routeSuffix}/authn-pending" method="post">
        <legend>Submit Authn Pending Response</legend>
        <fieldset>
            <div class="submit">
                <input id="authnPendingSubmit" class="button color-pending" type="submit" name="submit" value="Save and Continue Later"/>
            </div>
        </fieldset>
    </form>
    <form action="${rootPrefix}/${idpId}/login${routeSuffix}/no-authn-context" method="post">
        <fieldset>
            <legend>Submit Login No Authn Context</legend>
              <div class="submit">
                <input id="noAuthnContextSubmit" class="button color-ok" type="submit" name="submit" value="No Authn Context Event"/>
              </div>
        </fieldset>
    </form>
    <form action="${rootPrefix}/${idpId}/login${routeSuffix}/authn-failure" method="post">
        <fieldset>
            <legend>Submit Login Authn Failure</legend>
            <div class="submit">
                <input id="authnFailureSubmit" class="button color-ok" type="submit" name="submit" value="Authn Failure"/>
            </div>
        </fieldset>
    </form>
    <form action="${rootPrefix}/${idpId}/login${routeSuffix}/uplift-failed" method="post">
        <fieldset>
            <legend>Submit Login Uplift Failed</legend>
            <div class="submit">
                <input id="upliftFailedSubmit" class="button color-ok" type="submit" name="submit" value="Uplift Failed"/>
            </div>
        </fieldset>
    </form>
    <form action="${rootPrefix}/${idpId}/login${routeSuffix}/fraud-failure" method="post">
        <fieldset>
            <legend>Submit Login Fraud Failure</legend>
            <select name="failureStatus" id="failureStatus">
                <option value="DF01">Document fraud warning.</option>
                <option value="FI01">False identity warning.</option>
                <option value="IT01">Identity theft warning.</option>
            </select>

            <div class="submit">
                <input id="failureSubmit" class="button color-ok" type="submit" name="submit" value="Submit Fraud Event"/>
            </div>
        </fieldset>
    </form>
    <form action="${rootPrefix}/${idpId}/login${routeSuffix}/requester-error" method="post"">
    <fieldset>
        <legend>Submit Requester Error</legend>
        <div>
            <label for="requesterErrorMessage">Error Message</label>
            <input class="field medium" name="requesterErrorMessage" id="requesterErrorMessage" type="text"/>
        </div>
        <div class="submit">
            <input id="requesterErrorSubmit" class="button color-ok" type="submit" name="submit" value="Submit Requester Error"/>
        </div>
    </fieldset>
    </form>
</div>
