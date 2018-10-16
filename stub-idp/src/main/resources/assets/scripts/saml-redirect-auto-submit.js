function initAutoSubmitSequence() {
    document.forms[0].setAttribute("style", "display: none;");

    window.setTimeout(function () {
        document.forms[0].removeAttribute("style");
    }, 5000);

    document.forms[0].submit();
}
document.addEventListener('load', initAutoSubmitSequence());
