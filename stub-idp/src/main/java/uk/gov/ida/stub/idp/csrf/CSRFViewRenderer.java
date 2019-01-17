package uk.gov.ida.stub.idp.csrf;

import io.dropwizard.views.View;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import uk.gov.ida.stub.idp.csrf.exceptions.CSRFConflictingFormAttributeException;
import uk.gov.ida.stub.idp.views.IdpPageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import static uk.gov.ida.stub.idp.csrf.CSRFCheckProtectionFilter.CSRF_PROTECT_FORM_KEY;

/**
 * Adds a csrf protection value to all forms in the output html, if one is present in IdpPageView
 */
public class CSRFViewRenderer extends FreemarkerViewRenderer {

    public CSRFViewRenderer() {
        super();
    }

    @Override
    public void render(View view, Locale locale, OutputStream output) throws IOException {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        super.render(view, locale, byteArrayOutputStream);
        byteArrayOutputStream.close();

        if(view instanceof IdpPageView && ((IdpPageView)view).getCsrfToken().isPresent()) {
            org.jsoup.nodes.Document document = Jsoup.parse(new String(byteArrayOutputStream.toByteArray()));
            final Elements nodeList = document.getElementsByTag("form");
            for (int i = 0; i < nodeList.size(); i++) {
                final Element item = nodeList.get(i);
                if (item.children().stream().filter(it -> (it.tag().getName().equals("input") && it.hasAttr("name") && it.attr("name").equals(CSRF_PROTECT_FORM_KEY))).count() > 0) {
                    throw new CSRFConflictingFormAttributeException();
                } else {
                    item.appendChild(new Element(Tag.valueOf("input"), "", new Attributes() {{
                        put("name", CSRF_PROTECT_FORM_KEY);
                        put("id", CSRF_PROTECT_FORM_KEY);
                        put("value", ((IdpPageView) view).getCsrfToken().get());
                        put("type", "hidden");
                    }}));
                }
            }
            output.write(document.html().getBytes());
        } else {
            output.write(byteArrayOutputStream.toByteArray());
        }

    }

}
