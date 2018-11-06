<#-- @ftlvariable name="" type="uk.gov.ida.stub.idp.views.RegistrationPageView" -->
<div class="main">
    <div class="tabs">
        <ul>
            <li>
                <a id="tab-login" class="tab-text" href="/${idpId}/login">Login</a>
            </li>
            <li class="on" id="tab-register">
                <span class="tab-text">Register</span>
            </li>
            <li>
                <a id="tab-debug" class="tab-text" href="/${idpId}/debug">System information</a>
            </li>
        </ul>
    </div>
    <form action="/${idpId}/register" class="register" method="post" id="registration-form" autocomplete="off">
        <fieldset>
            <legend>Register with ${name}</legend>

            <div class="error-text">${errorMessage}</div>

            <hr>
            <div>
            You have arrived at this stub IDP in the registration flow.  If you register a user here the matching dataset
            will not contain a middle name or gender, and will contain only one address. You can also choose the level of
            assurance of the user.
            </div>
            <div>
            It is possible to POST users to this IDP, which can then be used by clicking on the Login tab above.  The
            instructions on how to do this are contained within the
            <a href="https://alphagov.github.io/rp-onboarding-tech-docs/pages/env/envEndToEndTests.html#createtestusers">GOV.UK Verify onboarding guide</a>.  By adding users in that way you have more control
            over the matching dataset.  Landing on this Registration page and changing to Logging in does not affect the
            behaviour of the GOV.UK Verify hub.
            </div>

            <hr>

            <div>
                <label for="firstname">First Name<abbr title="Required">*</abbr>
                </label>
                <input class="field medium required" name="firstname" id="firstname" type="text"/>
                <em>Note about this field</em>
            </div>
            <div>
                <label for="surname">Surname<abbr title="Required">*</abbr>
                </label>
                <input class="field medium required" name="surname" id="surname" type="text"/>
                <em>Note about this field</em>
            </div>
            <div>
                <span>Address</span>
                <div>
                    <label for="addressLine1">Line 1<abbr title="Required">*</abbr>
                    </label>
                    <input class="field medium required" name="addressLine1" id="addressLine1" type="text"/>
                    <em>Note about this field</em>
                </div>
                <div>
                    <label for="addressLine2">Line 2<abbr title="Required">*</abbr>
                    </label>
                    <input class="field medium required" name="addressLine2" id="addressLine2" type="text"/>
                    <em>Note about this field</em>
                </div>
                <div>
                    <label for="addressTown">Town<abbr title="Required">*</abbr>
                    </label>
                    <input class="field medium required" name="addressTown" id="addressTown" type="text"/>
                    <em>Note about this field</em>
                </div>
                <div>
                    <label for="addressPostCode">Post Code<abbr title="Required">*</abbr>
                    </label>
                    <input class="field medium required" name="addressPostCode" id="addressPostCode" type="text"/>
                    <em>Note about this field</em>
                </div>
            </div>
            <div>
                <label for="dateOfBirth">Date of Birth<abbr title="Required">*</abbr>
                </label>
                <input class="field medium required" name="dateOfBirth" id="dateOfBirth" placeholder="YYYY-MM-DD" type="text"/>
                <em>Note about this field</em>
            </div>
            <div>
                <input type="checkbox" name="includeGender" value="true" id="include-gender">Include Gender?</input>
                <div id="include-gender-questions" class="hidden">
                    <input type="radio" name="gender" checked value="Not Specified">Not Specified</input>
                    <input type="radio" name="gender" value="Male">Male</input>
                    <input type="radio" name="gender" value="Female">Female</input>
                 </div>
                <em>Note about this field</em>
            </div>
            <script src="/assets/scripts/gender-picker-toggle.js"></script>
            <div>
                <label for="username">Username<abbr title="Required">*</label>
                <input class="field medium required" name="username" id="username"/>
                <em>Note about this field</em>
            </div>
            <div>
                <label for="password">Password<abbr title="Required">*</label>
                <input class="field medium required" name="password" id="password" type="password"/>
                <em>Note about this field</em>
            </div>
            <div>
                <label for="loa">Level of assurance<abbr title="Required">*</label>
                <select class="dropdown medium" name="loa" id="loa">
                    <option id="LEVEL_1" value="LEVEL_1">Level 1</option>
                    <option selected="selected" id="LEVEL_2" value="LEVEL_2">Level 2</option>
                </select>
                <em>Note about this field</em>
            </div>
            <div class="submit">
                <input id="register" class="button color-ok" type="submit" name="submit" value="Register"/>
                <input id="cancel" class="button cancel color-cancel" type="submit" name="submit" value="Cancel" />
            </div>
        </fieldset>
    </form>
</div>
<script type="text/javascript" src="/assets/scripts/dob-validator.js"></script>
