package uk.gov.ida.stub.idp.views;

import com.google.common.collect.ImmutableList;
import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class ErrorPageView extends View {

    public ErrorPageView() {
        super("errorPage.ftl", StandardCharsets.UTF_8);
    }

    public String getReaction() {
        final List<String> reactions = ImmutableList.of("😧","😮","😢","😭","👎","😶","🙃");
        final int reaction = new Random().nextInt(reactions.size());
        return reactions.get(reaction);
    }
}
