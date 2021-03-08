package org.hadatac.console.controllers.indicators;

import javax.inject.Inject;

import org.hadatac.Constants;
import org.hadatac.console.controllers.Application;
import org.pac4j.play.java.Secure;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.data.*;

import org.hadatac.console.views.html.indicators.*;
import org.hadatac.console.controllers.indicators.routes;
import org.hadatac.console.controllers.metadata.DynamicFunctions;
import org.hadatac.entity.pojo.Indicator;
import org.hadatac.metadata.loader.URIUtils;
import org.hadatac.utils.ConfigProp;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import org.hadatac.console.models.IndicatorForm;
import org.hadatac.console.models.SysUser;
import org.hadatac.console.controllers.AuthApplication;

public class NewIndicator extends Controller {

    @Inject
    private FormFactory formFactory;
    @Inject
    private Application application;

    @Secure(authorizers = Constants.DATA_OWNER_ROLE)
    public Result index(Http.Request request) {
        // may need addressing
        Indicator indicator = new Indicator();

        return ok(newIndicator.render(indicator,application.getUserEmail(request)));
    }

    @Secure(authorizers = Constants.DATA_OWNER_ROLE)
    public Result postIndex(Http.Request request) {
        return index(request);
    }

    @Secure(authorizers = Constants.DATA_OWNER_ROLE)
    public Result processForm(Http.Request request) {
        final SysUser sysUser = AuthApplication.getLocalUser(application.getUserEmail(request));

        Form<IndicatorForm> form = formFactory.form(IndicatorForm.class).bindFromRequest(request);
        IndicatorForm data = form.get();

        if (form.hasErrors()) {
            return badRequest("The submitted form has errors!");
        }

        // store new values
        String newURI = URIUtils.replacePrefixEx(data.getNewUri());
        if (newURI == null || newURI.equals("")) {
            return badRequest("[ERROR] New URI cannot be empty.");
        }
        String newLabel = data.getNewLabel();
        String newComment = data.getNewComment();

        // insert current state of the STD
        Indicator ind = new Indicator(DynamicFunctions.replacePrefixWithURL(newURI),
                newLabel,
                newComment);

        // insert the new indicator content inside of the triplestore
        ind.save();

        return ok(newIndicatorConfirm.render(ind,sysUser.getEmail()));
    }
}
