package stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Eiffel3Steps {
    @And("^request body contains domainId \"([^\"]*)\"$")
    public void requestBodyContainsDomainId(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^a message service is started, initialized and reached steady state operation$")
    public void aMessageServiceIsStartedInitializedAndReachedSteadyStateOperation() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I send a PUT request containing eiffeljobstarted as msgType parameter$")
    public void iSendAPUTRequestContainingEiffeljobstartedAsMsgTypeParameter() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^request body also contains no eventIds$")
    public void requestBodyAlsoContainsNoEventIds() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I receive an jobstarted event containing testdomain as domainId$")
    public void iReceiveAnJobstartedEventContainingTestdomainAsDomainId() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^returned json body also contain no eventIds$")
    public void returnedJsonBodyAlsoContainNoEventIds() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
