package uk.gov.ida;

import groovy.util.BuilderSupport;
import groovy.util.Node;

import java.util.ArrayList;
import java.util.Map;

public class NodeBuilder extends BuilderSupport {

    public static NodeBuilder newInstance() {
        return new NodeBuilder();
    }

    protected void setParent(Object parent, Object child) {
    }

    protected Object createNode(Object name) {
        return new Node(getCurrentNode(), name, new ArrayList());
    }

    protected Object createNode(Object name, Object value) {
        return new Node(getCurrentNode(), name, value);
    }

    protected Object createNode(Object name, Map attributes) {
        return new Node(getCurrentNode(), name, attributes, new ArrayList());
    }

    protected Object createNode(Object name, Map attributes, Object value) {
        return new Node(getCurrentNode(), name, attributes, value);
    }

    protected Node getCurrentNode() {
        return (Node) getCurrent();
    }
}