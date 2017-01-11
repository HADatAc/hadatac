package org.hadatac.console.controllers.metadata;

import org.hadatac.console.http.GetSparqlQuery;
import org.hadatac.console.http.GetSparqlQueryDynamic;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import org.hadatac.console.models.SparqlQuery;
import org.hadatac.console.models.OtMSparqlQueryResults;

import play.mvc.Controller;
import play.mvc.Result;

import org.hadatac.console.views.html.metadata.analytes.analytes_browser;
import org.hadatac.console.views.html.metadata.metadata_browser;
import org.hadatac.console.views.html.error_page;


public class MetadataEntry extends Controller {
	
	// for /metadata HTTP GET requests
    public static Result index(String tabName) {

    	SparqlQuery query = new SparqlQuery();
        GetSparqlQueryDynamic query_submit = new GetSparqlQueryDynamic(query);
        OtMSparqlQueryResults theResults;
        String query_json = null;
        System.out.println("MetadataEntry.java is requesting: " + tabName);
        try {
            query_json = query_submit.executeQuery(tabName);
            //System.out.println("query_json = " + query_json);
            theResults = new OtMSparqlQueryResults(query_json, true);
        } catch (IllegalStateException | IOException | NullPointerException e1) {
            return internalServerError(error_page.render(e1.toString(), tabName));
            //e1.printStackTrace();
        }
        System.out.println(tabName + " index() was called!");
    	
    	//List<org.hadatac.entity.pojo.Entity> entities = org.hadatac.entity.pojo.Entity.find();
    	
        return ok(metadata_browser.render(theResults, tabName));
    }// /index()


    // for /metadata HTTP POST requests
    public static Result postIndex(String tabName) {

    	return index(tabName);
    	
    }// /postIndex()

}