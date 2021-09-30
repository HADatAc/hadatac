package org.hadatac.console.models;

import java.util.ArrayList;
import java.util.List;

public class IndicatorForm {

    public String newUri;
    public String newLabel;
    public String newComment;

    public IndicatorForm () {
    }

    public String getNewUri() {
        return newUri;
    }

    public void setNewUri(String uri) {
        this.newUri = uri;
    }

    public String getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(String label) {
        this.newLabel = label;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String comment) {
        this.newComment = comment;
    }
}