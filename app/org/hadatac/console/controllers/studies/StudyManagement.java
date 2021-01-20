package org.hadatac.console.controllers.studies;

import java.util.List;

import org.hadatac.Constants;
import org.hadatac.entity.pojo.Study;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import org.hadatac.console.controllers.AuthApplication;
import org.hadatac.console.views.html.studies.*;
import play.mvc.Result;
import play.mvc.Controller;


public class StudyManagement extends Controller {

    @Restrict(@Group(Constants.DATA_OWNER_ROLE))
    public Result index() {
        List<Study> theResults = Study.find();

        return ok(studyManagement.render(theResults));
    }

    @Restrict(@Group(Constants.DATA_OWNER_ROLE))
    public Result postIndex() {
        return index();
    }
}
