package org.hadatac.console.controllers.dataanalysis;

import org.hadatac.Constants;
import org.hadatac.console.controllers.Application;
import org.hadatac.console.views.html.dataanalysis.*;

import java.util.List;

import org.hadatac.console.controllers.AuthApplication;
import org.hadatac.console.controllers.metadata.DynamicFunctions;
import org.hadatac.console.models.IndicatorForm;
import org.hadatac.console.models.SysUser;
import org.hadatac.entity.pojo.Aggregate;
import org.hadatac.entity.pojo.Indicator;
import org.hadatac.entity.pojo.facet.EntityCharacteristic;
import org.hadatac.metadata.loader.URIUtils;
import org.hadatac.utils.ConfigProp;

import com.google.inject.Inject;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import org.pac4j.play.java.Secure;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class NewFunction extends Controller {
	
	@Inject
	FormFactory formFactory;
	@javax.inject.Inject
	private Application application;
	
	@Secure(authorizers = Constants.DATA_MANAGER_ROLE)
    public Result index(Http.Request request) {
		final SysUser sysUser = AuthApplication.getLocalUser(application.getUserEmail(request));
		
    	// may need addressing
    	Indicator indicator = new Indicator();
    	List<Aggregate> aggregates = Aggregate.find();
    	EntityCharacteristic entityCharacteristic = EntityCharacteristic.create(sysUser.getUri());
    	return ok(newFunction.render(indicator, entityCharacteristic, aggregates,sysUser.getEmail()));
    }
	
	@Secure(authorizers = Constants.DATA_MANAGER_ROLE)
    public Result postIndex(Http.Request request) {
    	return index(request);
    }
	
	@Secure(authorizers = Constants.DATA_MANAGER_ROLE)
    public Result processForm(Http.Request request) {
		
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
		
		return ok(newFunctionConfirm.render(ind,application.getUserEmail(request)));
    }
}
