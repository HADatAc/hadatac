package org.hadatac.console.controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.data.FormFactory;

import java.util.List;

import javax.inject.Inject;

import org.hadatac.console.models.ObjectCollectionForm;
import org.hadatac.console.models.OneStringFieldForm;
import org.hadatac.console.views.html.lookup.*;
import org.hadatac.console.views.html.metadata.empirical.*;
import org.hadatac.entity.pojo.DataAcquisitionSchema;
import org.hadatac.entity.pojo.Detector;
import org.hadatac.entity.pojo.GenericInstance;
import org.hadatac.entity.pojo.HADatAcClass;
import org.hadatac.entity.pojo.HADatAcThing;
import org.hadatac.entity.pojo.Instrument;
import org.hadatac.entity.pojo.ObjectCollection;
import org.hadatac.entity.pojo.Platform;
import org.hadatac.metadata.loader.URIUtils;
import org.hadatac.utils.ConfigProp;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

public class URILookup extends Controller {

    @Inject
    private FormFactory formFactory;
    @Inject Application application;

    public Result index(String mode, String message, Http.Request request) {
        return ok(uriLookup.render(mode, message, application.getUserEmail(request)));
    }

    public Result postIndex(String mode, String message, Http.Request request) {
        return index(mode, message, request);
    }

    public Result processForm(String mode, Http.Request request) {

        Form<OneStringFieldForm> form = formFactory.form(OneStringFieldForm.class).bindFromRequest(request);
        OneStringFieldForm data = form.get();

        if (form.hasErrors()) {
            return ok(uriLookup.render(mode, "Input value has errors",application.getUserEmail(request)));
        }

        String newURI = null;
        if (data.getField() == null || data.getField().equals("")) {
            return ok(uriLookup.render(mode, "Input value cannot be empty",application.getUserEmail(request)));
        } else {
            newURI = data.getField();
        }

        if (!URIUtils.isValidURI(newURI)) {
            return ok(uriLookup.render(mode, "Input value is not a valid URI or is not based on a registered namespace",application.getUserEmail(request)));
        }

        newURI = URIUtils.replacePrefixEx(newURI);
        return processUri(mode, newURI,request);
    }

    public Result processUri(String mode, String uri, Http.Request request) {

        System.out.println("Input value: [" + uri + "]");

        GenericInstance thingInstance = GenericInstance.find(uri);

        if (thingInstance != null) {
            Platform pt = Platform.find(thingInstance.getUri());
            if (pt != null) {
                return ok(viewPlatform.render("","","",pt,application.getUserEmail(request)));
            } else {
                Instrument it = Instrument.find(thingInstance.getUri());
                if (it != null) {
                    return ok(viewInstrument.render("","","",it,application.getUserEmail(request)));
                } else {
                    Detector dt = Detector.find(thingInstance.getUri());
                    if (dt != null) {
                        return ok(viewDetector.render("","","",dt,application.getUserEmail(request)));
                    } else {
                        return ok(uriLookupInstanceResponse.render(mode, thingInstance,application.getUserEmail(request)));
                    }
                }
            }
        }

        HADatAcClass thingClass = new HADatAcClass(HADatAcThing.OWL_THING);
        thingClass = thingClass.findGeneric(uri);

        if (thingClass != null) {
            return ok(uriLookupClassResponse.render(mode, thingClass,application.getUserEmail(request)));
        }

        return ok(uriLookup.render(mode, "Could not find such URI in the knowledge graph",application.getUserEmail(request)));

    }


}
