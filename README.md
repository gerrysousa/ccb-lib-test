# API Tests with Cucumber and Chaintest Report

### How to

To get a Git project into your build:

#### Step 1. Add the JitPack repository to your build file

Add to pom.xml

```xml

<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

#### Step 2. Add the dependency

```xml

<dependency>
  <groupId>com.github.gerrysousa</groupId>
  <artifactId>ccb-lib-test</artifactId>
  <version>0.0.1</version>
</dependency>
```

Gradle and other build tools are also supported.
See https://jitpack.io/#gerrysousa/ccb-lib-test/0.0.1 for details.

### Example using the ccb-lib-test

- https://github.com/gerrysousa/example-using-ccb-lib-test

### REST options examples

```

Given I set the base URL to "https://fakerestapi.azurewebsites.net"

Given I set the endpoint to "/api/v1/Activities"

Given I set the request body with JSON file "payload.json"

Given I set the headers
| Content-Type | application/json |
| Accept | application/json |

Given I set the query params
| page | 1 |
| limit | 50 |

Given I set the form params
| username | john |
| password | 1234 |

Given I set the multiparts
| file | test.pdf |

When I send a "GET" request to "https://fakerestapi.azurewebsites.net" endpoint "/api/v1/Activities"

When I send a "GET" request to "https://fakerestapi.azurewebsites.net" endpoint "/api/v1/Activities"
with query params
| page | 1 |
| size | 10 |

When I send a "POST" request to "https://fakerestapi.azurewebsites.net" endpoint "
/api/v1/Activities" with form params
| username | john |
| password | 1234 |

When I send a "GET" request to "https://fakerestapi.azurewebsites.net" endpoint "/api/v1/Activities"
with headers
| Authorization | Bearer {token} |

When I send a "POST" request to "https://fakerestapi.azurewebsites.net" endpoint "/api/v1/upload"
with multiparts
| file | test.pdf |

When I send a "POST" request to "https://fakerestapi.azurewebsites.net" endpoint "
/api/v1/Activities" with JSON payload "payload.json"

When I send a "POST" request to "https://fakerestapi.azurewebsites.net" endpoint "
/api/v1/Activities" with JSON payload "payload.json" and query params
| page | 1 |
| size | 10 |

When I send a "GET" request to endpoint "/api/v1/Activities"

When I send a "POST" request to endpoint "/api/v1/Activities" with JSON payload "payload.json"

Then the response body should contain "Activity 1"

Then the response body should be equal JSON "expectedResponse.json"

Then the response status code should be 200

Then the response status code should be "404"

Then the JSON path "$.id" should be "123"

Then the JSON path "$.price" should be number 49.99

Then the JSON path "$.title" should exist

Then the JSON path "$.deprecatedField" should not exist

```

### Wiremock options examples

```

Given I reset the count of wiremock requests

Then the API mocks were called
| mock1 |
| mock2 |
| mock3 |

```

### Chaintestreport example

![img_1.png](img_1.png)

Here is an example of the Chaintestreport generated after running the tests:
[ChaintestReportExample.html](ChaintestReportExample.html)

In the resport, all REST requests and responses are displayed, including the request body, response
body, like the log below:

```log
---------------------------------- Captured Request ----------------------------
Request method:	POST
Request URI:	http://localhost:9090/v1/Activities
Proxy:			
Request params:	
Query params:	
Form params:	
Path params:	
Headers:		Accept=*/*
				Content-Type=application/json
Cookies:		
Multiparts:		
Body:
{
    "id": 20,
    "title": "Sample Activity 20250821171501074",
    "dueDate": "2025-08-21T17:15:01.074Z",
    "completed": false
}
HTTP/1.1 200 OK
Content-Type: application/json
Matched-Stub-Id: 86fed8fe-c46c-4335-952b-2d5e18425cec
Matched-Stub-Name: post-activities-001
Content-Encoding: gzip
Transfer-Encoding: chunked

{
    "id": 20,
    "title": "Wiremock Sample Activity 20250821171501074",
    "dueDate": "2025-08-21T17:15:01.074Z",
    "completed": false
}
--------------------------------------------------------------------------------
```

### Test Variables Generated

For each test scenario, some variables are generated and can be used in the requests, for example:

```log
+++++++++++++++++++ Test Variables Generated +++++++++++++++++
gen:id.epoch: 1756307686516
gen:test.id: 20250827121446516
gen:timestamp: 2025-08-27T12:14:46.516Z
gen:id.timestamp: 20250827121446516
gen:today.numeric: 20250827
gen:id.string: VAOd1nlgbP
gen:today.date: 2025-08-27
gen:id.uuid: 5254ca47-4c7a-4e33-8086-a63fa5a9f851
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
```

Example of using the generated variables in the request body:
In a JSON file named `activity_20.json`:

```json
{
  "id": 20,
  "title": "Sample Activity gen:test.id",
  "dueDate": "gen:timestamp",
  "completed": false
}
```

And in the feature file:

```feature
  Scenario: Create a new activity with valid data
    Given I set the headers
      | content-type | application/json |
    When I send a "POST" request to "fakeapi.url" endpoint "/v1/Activities" with JSON payload "activity_20.json"
    Then the response status code should be "200"
    And the response body should contain "Sample Activity gen:test.id"
```

Tags: @api @regression @wiremock @chaintestreport @maven @rest @java @cucumber @restassured