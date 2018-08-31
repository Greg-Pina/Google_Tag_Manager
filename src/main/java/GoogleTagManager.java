/**
 * Access and manage a Google Tag Manager account.
 */

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.language.bm.Rule;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.Parameter;
import com.google.api.services.tagmanager.model.Tag;


public class GoogleTagManager {
	
	  // Path to client_secrets.json file downloaded from the Developer's Console.
	  // The path is relative to GoogleTagManager.java.
	  private static final String CLIENT_SECRET_JSON_RESOURCE = "client_secrets.json";

	  // The directory where the user's credentials will be stored for the application.
	  private static final File DATA_STORE_DIR = new File("/Users/Greg/eclipse-workspace/Google_Tag_Manager/src/main/java/client_secrets.txt\n" + 
	  		"\n" + 
	  		"");

	  private static final String Google_Tag_Manager = "GoogleTagManager";
	  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	  private static NetHttpTransport httpTransport;
	  private static FileDataStoreFactory dataStoreFactory;

	  public static void main(String[] args) {
	    try {
	    	
	      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

	      // Authorization flow.
	      Credential credential = authorize();
	      TagManager manager = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
	          .setApplicationName(Google_Tag_Manager).build();

	      // Get tag manager account ID.
	      String accountId = "192727684700-8la3489rcmo8671ruec7n71ftqijg11n.apps.googleusercontent.com";

	      // Find the Greg_Pina_Test container.
	      Container Greg_Pina_Test = findContainer(manager, accountId);
	      String containerId = Greg_Pina_Test.getContainerId();

	      // Create the Google Tag Manager tag.
	      Tag ua = createUATag(accountId, containerId, manager);

	      // Create the Google Tag Manager rule.
	      Rule hello = createGoogleTagManagerRule(accountId, containerId, manager);

	      // Update the Google Tag Manager tag to fire based on the Google Tag Manager tag.
	      fireTagOnRule(ua, hello);
	      ua = manager.accounts().containers().tags().update(accountId, containerId, ua.getTagId(), ua)
	          .execute();

	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  private static Tag createUATag(String accountId, String containerId, TagManager manager) {
		  Parameter arg0 = new Parameter();
		  arg0.setType("template");
		  arg0.setKey("trackingId");
		  arg0.setValue("GTM-NBXVV22");

		  Parameter arg1 = new Parameter();
		  arg1.setType("template");
		  arg1.setKey("type");
		  arg1.setValue("TRACK_TRANSACTION");

		  // Construct the tag object.
		  Tag tag = new Tag();
		  tag.setName("Sample Universal Analytics Tag");
		  tag.setType("ua");
		  tag.setLiveOnly(false);
		  tag.setParameter(Arrays.asList(arg0, arg1));

		  try {
		    Tag response = manager.accounts().containers().workspaces().
		        tags().create("123456", tag).execute();

		  } catch (GoogleJsonResponseException e) {
		    System.err.println("There was a service error: "
		        + e.getDetails().getCode() + " : "
		        + e.getDetails().getMessage());
		  }
		  
		return null;
	}

	private static Credential authorize() throws Exception {
	    // Load client secrets.
	    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	        new InputStreamReader(GoogleTagManager.class.getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));

	    // Set up authorization code flow for all auth scopes.
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
	        JSON_FACTORY, clientSecrets, TagManagerScopes.all()).setDataStoreFactory(dataStoreFactory)
	        .build();

	    // Authorize.
	    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	  }

	  /*
	   * Find the Greg_Pina_Test container ID.
	   *
	   * @param service the Tag Manager service object.
	   * @param accountId the ID of the Tag Manager account from which to retrieve the
	   *    Greetings container.
	   *
	   * @return the Greg_Pina_Test container if it exists.
	   *
	   */
	  private static Container findContainer(TagManager service, String accountId)
	      throws Exception {
	    for (Container container :
	        service.accounts().containers().list(accountId).execute().getContainer()) {
	      if (container.getName().equals("Greg_Pina")) {
	        return container;
	      }
	    }
	    throw new IllegalArgumentException("No container named Greg_Pina in given account");
	  }

	  /**
	   * Create the Universal Analytics Google Tag Manager Tag.
	   *
	   * @param accountId the ID of the account holding the container.
	   * @param containerId the ID of the container to create the tag in.
	   * @param service the Tag Manager service object.
	   * @return the newly created Tag resource.
	   */
	  
	  private static Tag createGoogleTagManagerTag(String accountId, String containerId, TagManager service) {
	    Tag ua = new Tag();
	    ua.setName("Universal Analytics Google Tag Manager");
	    ua.setType("ua");

	    List<Parameter> uaParams = new ArrayList<Parameter>();
	    Parameter trackingId = new Parameter();
	    trackingId.setKey("trackingId").setValue("GTM-WCDFNG9").setType("template");
	    uaParams.add(trackingId);
	    
	    ua.setParameter(uaParams);
	    ua = service.accounts().containers().tags().create(accountId, containerId, ua)
	        .execute();
	    
	    Parameter arg0 = new Parameter();
	    arg0.setType("template");
	    arg0.setKey("trackingID");
	    arg0.setValue("GTM-WCDFNG9");
	    	    
	    return ua;
	  }


	  /**
	   * Create the Google Tag Manager Rule.
	   *
	   * @param accountId the ID of the account holding the container.
	   * @param containerId the ID of the container to create the rule in.
	   * @param service the Tag Manager service object.
	   *
	   * @return the newly created Rule resource.
	   **/
	  private static Rule createGoogleTagManagerRule(String accountId, String containerId, TagManager service) {
	    Rule GoogleTagManager = new Rule(containerId, containerId, containerId, null);
	    GoogleTagManager.setName("Google Tag Manager");

	    List<Condition> conditions = new ArrayList<Condition>();
	    Condition endsWithGoogleTagManager = new Condition();
	    endsWithGoogleTagManager.setType("endsWith");
	    List<Parameter> params = new ArrayList<Parameter>();
	    params.add(new Parameter().setKey("arg0").setValue("{{url}}").setType("template"));
	    params.add(new Parameter().setKey("arg1").setValue("hello-world.html").setType("template"));
	    endsWithGoogleTagManager.setParameter(params);
	    conditions.add(endsWithGoogleTagManager);

	    GoogleTagManager.setCondition(conditions);
	    GoogleTagManager = service.accounts().containers().rules().create(accountId, containerId, GoogleTagManager)
	        .execute();

	    return GoogleTagManager;
	  }


	  /**
	   * Update a Tag with a Rule.
	   *
	   * @param tag the tag to associate with the rule.
	   * @param rule the rule to associate with the tag.
	   *
	   */
	  private static void fireTagOnRule(Tag tag, Rule rule) {
	    List<String> firingRuleIds = new ArrayList<String>();
	    firingRuleIds.add(rule.getRuleId());
	    tag.setFiringRuleId(firingRuleIds);
	  }
	}
	      
