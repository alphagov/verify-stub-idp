<#macro addy address>
    <#list address.lines as line>
    <span class="address-item">${line}</span>
    </#list>
    <span class="address-item">${(address.postCode.orElse("")+",")!} ${(address.internationalPostCode.orElse("")+",")!} ${(address.UPRN.orElse(""))!}</span>
    <span class="address-item">United Kingdom</span>
</#macro>
