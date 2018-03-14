<#macro addy address>
    <#list address.lines as line>
    <span class="address-item">${line}</span>
    </#list>
    <span class="address-item">${(address.postCode.orNull()+",")!} ${(address.internationalPostCode.orNull()+",")!} ${(address.UPRN.orNull())!}</span>
    <span class="address-item">United Kingdom</span>
</#macro>