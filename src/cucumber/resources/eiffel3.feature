Feature: core features of message microservice
  Background:
    Given a message service is started, initialized and reached steady state operation

  @eiffel3_jse_create
  Scenario: JobStartedEvent works
    When I send a PUT request containing eiffeljobstarted as msgType parameter
      And request body contains domainId "testdomain"
      And request body also contains no eventIds
    Then I receive an jobstarted event containing testdomain as domainId
      And returned json body also contain no eventIds