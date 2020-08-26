# codehub-admin-api
CodeHub Admin API
> Version: 2.8.0

The Admin API of CodeHub has the function to administer the metadata information (documents) for the CodeHub Ingestion system. The API connect to an ElasticSearch storage system. The API will do actions on the Repos and Projects indexes.

## Change Log
Changes related to the previous version.

> Previous Version: 2.7.0
- Changes to support CloudFront invalidation.

## Usage
Once the application is running on a configured port the API uses the standard REST verbs to manipulate the data.

- GET: List the available data.
- POST: Creates a new document/record.
- PUT: Updates an existing document/record.
- DELETE: Deletes one or more document(s).
- PATCH: Remove the associated cache (etag) information from an existing document.

Two sets of actions were defined to administer the data for each Elasticsearch index

---
## Header Token Validation
The API is expecting to receive a TOKEN in the header under the name CHTOKEN. This value needs to be provided as part of the configuration of the API. Failing to provide the CHTOKEN will return in UNAUTHORIZED.

```json
{
  "timestamp" : "2019-11-20T23:40:48Z",
  "status" : "UNAUTHORIZED",
  "code" : 401,
  "path" : "http://localhost",
  "verb" : "GET",
  "traceid" : "20191120234048621",
  "result" : null,
  "errors" : [ {
    "message" : "Error description"
  } ]
}
```

---

## Repository
The following entries are the endpoint to administer the data in the Repos Index.

### List Repository

  - Method: GET
  - URL: http://[host:port]/api/v1/repositories

### Add Repository

 - Method: POST
 - URL: http://[host:port]/api/v1/repositories
 - Content-Type: application/json
 - Payload (sample)
```json
{
  "id" : "91128507a8ae9c8046c33ee0f31e37f8",
  "sourceData" : {
    "name" : "Repository-Name",
    "repositoryUrl" : "http://www.example.com/owner/repository",
    "owner" : {
      "name" : "Repository-Owner"
    }
  },
  "generatedData" : {
    "rank" : 0
  },
  "codehubData" : {
    "etag" : "c99aa9c9867ddb8693e7740d0ca0c00f",
    "source" : null,
    "lastModified" : "2019-12-19T23:02:02.083+0000",
    "lastIngested" : null,
    "badges" : {
      "status" : null,
      "isFeatured" : false
    },
    "isIngestionEnabled" : true,
    "isIngested" : false,
    "isVisible" : false
  }
}
```

### Update Repository

 - Method: PUT
 - URL: http://[host:port]/api/v1/repositories
 - Content-Type: application/json
 - Payload (sample)
```json
{
  "id" : "91128507a8ae9c8046c33ee0f31e37f8",
  "sourceData" : {
    "name" : "Repository-Name",
    "repositoryUrl" : "http://www.example.com/owner/repository",
    "owner" : {
      "name" : "Repository-Owner"
    }
  },
  "generatedData" : {
    "rank" : 0
  },
  "codehubData" : {
    "etag" : "c99aa9c9867ddb8693e7740d0ca0c00f",
    "source" : null,
    "lastModified" : "2019-12-19T23:02:02.102+0000",
    "lastIngested" : null,
    "badges" : {
      "status" : null,
      "isFeatured" : false
    },
    "isIngestionEnabled" : true,
    "isIngested" : false,
    "isVisible" : false
  }
}
```

### Delete Repository

 - Method: DELETE
 - URL: http://[host:port]/api/v1/repositories/{ID}
 - Content-Type: application/json


### Delete Multiple Repositories

 - Method: DELETE
 - URL: http://[host:port]/api/v1/repositories
 - Content-Type: application/json
 - Payload (sample): array of IDs
```json
[ "91128507a8ae9c8046c33ee0f31e37f8", "7f3bac27fc81d39ffa8ede58b39c8fb6", "fc4e98b95dc24f763621c54fe50ded24" ]
```

### Reset Cache

 - Method: PATCH
 - URL: http://[host:port]/api/v1/repositories/resetcache
 - Content-Type: application/json
 - Payload (sample): array of IDs
```json
[ "91128507a8ae9c8046c33ee0f31e37f8", "7f3bac27fc81d39ffa8ede58b39c8fb6", "fc4e98b95dc24f763621c54fe50ded24" ]
```

### List Configurations

 - Method: GET
 - URL: http://[host:port]/api/v1/configurations
 - Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "timestamp" : "2020-03-23T21:23:38Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "GET",
  "traceid" : "20200323212338382",
  "result" : {
    "id" : "default-configuration",
    "name" : "default-configuration",
    "categories" : [ {
      "id" : "f332069f-1a59-4da0-b1bd-852407722ea9",
      "name" : "Category-84",
      "description" : "Description-84",
      "lastModified" : 1584998618382,
      "orderPopular" : 1,
      "imageFileName" : "http://path.to.the.image/image1.png",
      "isPopular" : true,
      "isEnabled" : true
    }, {
      "id" : "47cd40b1-fcb2-4db5-a8af-2358f21594ca",
      "name" : "Category-67",
      "description" : "Description-67",
      "lastModified" : 1584998618382,
      "orderPopular" : 1,
      "imageFileName" : "http://path.to.the.image/image1.png",
      "isPopular" : true,
      "isEnabled" : true
    }, {
      "id" : "eb0ab51b-f667-478a-96ad-295b2036f0de",
      "name" : "Category-54",
      "description" : "Description-54",
      "lastModified" : 1584998618382,
      "orderPopular" : 1,
      "imageFileName" : "http://path.to.the.image/image1.png",
      "isPopular" : true,
      "isEnabled" : true
    } ],
    "engagementPopups" : [ {
      "id" : "87ce0e95-37e1-412c-8f38-1d90ffdef64e",
      "name" : "EngagementPopup-41",
      "description" : "Description 41",
      "lastModified" : 1584998618382,
      "content" : "<h1>This is fake 41</h1>",
      "isActive" : false
    }, {
      "id" : "bb62520f-19cc-439e-a2fb-f1dd46789434",
      "name" : "EngagementPopup-77",
      "description" : "Description 77",
      "lastModified" : 1584998618382,
      "content" : "<h1>This is fake 77</h1>",
      "isActive" : false
    } ]
  }
}
```

### List Categories

 - Method: GET
 - URL: http://[host:port]/api/v1/configurations/categories
 - Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "timestamp" : "2020-02-18T16:46:45Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "GET",
  "traceid" : "20200218164645257",
  "result" : [ {
    "id" : "9b3f03b0-e668-4e3f-a243-5369334bb01d",
    "name" : "Category-93",
    "description" : "Description-93",
    "lastModified" : 1582044405257,
    "isEnabled" : true
  }, {
    "id" : "4e7b0838-7772-45e7-978f-ede01cbd1ef3",
    "name" : "Category-82",
    "description" : "Description-82",
    "lastModified" : 1582044405257,
    "isEnabled" : true
  }, {
    "id" : "46575710-202e-4116-9eb9-ac9d8a3847ab",
    "name" : "Category-99",
    "description" : "Description-99",
    "lastModified" : 1582044405257,
    "isEnabled" : true
  } ]
}
```

### Add Category

 - Method: POST
 - URL: http://[host:port]/api/v1/configurations/categories
 - Content-Type: application/json
 - Payload: CHCategory
```json
{
  "id" : "cb1bd654-1fb8-4967-8556-46dc4c36c6b2",
  "name" : "Category-71",
  "description" : "Description-71",
  "lastModified" : "2020-02-18T16:46:44.984+0000",
  "isEnabled" : true
}
```
 - Response
```json
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json

{
  "timestamp" : "2020-02-18T16:46:44Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "POST",
  "traceid" : "20200218164644984",
  "result" : {
    "id" : "cb1bd654-1fb8-4967-8556-46dc4c36c6b2",
    "name" : "Category-71",
    "description" : "Description-71",
    "lastModified" : 1582044404984,
    "isEnabled" : true
  }
}
```

### Update Category

 - Method: PUT
 - URL: http://[host:port]/api/v1/configurations/categories
 - Content-Type: application/json
 - Payload: CHCategory
```json
{
  "id" : "ee463f3a-2eac-4c36-b337-6680b9662488",
  "name" : "Category-11",
  "description" : "Description-11",
  "lastModified" : "2020-02-18T16:46:45.241+0000",
  "isEnabled" : true
}
```
 - Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "timestamp" : "2020-02-18T16:46:45Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "PUT",
  "traceid" : "20200218164645241",
  "result" : {
    "id" : "ee463f3a-2eac-4c36-b337-6680b9662488",
    "name" : "Category-11",
    "description" : "Description-11",
    "lastModified" : 1582044405241,
    "isEnabled" : true
  }
}
```

### Delete Category

 - Method: DELETE
 - URL: http://[host:port]/api/v1/configurations/categories/{:ID}
 - Content-Type: application/json
 - Response
```json
{
  "timestamp" : "2020-02-18T16:46:45Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "DELETE",
  "traceid" : "20200218164645302",
  "result" : {
    "id" : "36325cb4-b100-4bb6-a091-22744e719b5c",
    "name" : "Category-44",
    "description" : "Description-44",
    "lastModified" : 1582044405302,
    "isEnabled" : true
  }
}
```

### List Engagement PopUps

 - Method: GET
 - URL: http://[host:port]/api/v1/configurations/engagementpopups
 - Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "timestamp" : "2020-03-23T21:23:38Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "GET",
  "traceid" : "20200323212338472",
  "result" : [ {
    "id" : "6c47741d-c67e-4be1-9e22-f4c556a4ed3e",
    "name" : "EngagementPopup-99",
    "description" : "Description 99",
    "lastModified" : 1584998618472,
    "content" : "<h1>This is fake 99</h1>",
    "isActive" : false
  }, {
    "id" : "dafddf46-19df-48c3-98be-e248d14ce16a",
    "name" : "EngagementPopup-77",
    "description" : "Description 77",
    "lastModified" : 1584998618472,
    "content" : "<h1>This is fake 77</h1>",
    "isActive" : false
  } ]
}
```

### Add Engagement PopUp

 - Method: POST
 - URL: http://[host:port]/api/v1/configurations/engagementpopups
 - Content-Type: application/json
 - Payload: CHCategory
```json
{
  "id" : "e0402d89-5b88-484e-ad73-e9147b8928a3",
  "name" : "EngagementPopup-64",
  "description" : "Description 64",
  "lastModified" : "2020-03-23T21:23:38.405+0000",
  "content" : "<h1>This is fake 64</h1>",
  "isActive" : false
}
```
 - Response
```json
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json

{
  "timestamp" : "2020-03-23T21:23:38Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "POST",
  "traceid" : "20200323212338405",
  "result" : {
    "id" : "e0402d89-5b88-484e-ad73-e9147b8928a3",
    "name" : "EngagementPopup-64",
    "description" : "Description 64",
    "lastModified" : 1584998618405,
    "content" : "<h1>This is fake 64</h1>",
    "isActive" : false
  }
}
```

### Update Engagement PopUp

 - Method: PUT
 - URL: http://[host:port]/api/v1/configurations/engagementpopups
 - Content-Type: application/json
 - Payload: CHCategory
```json
{
  "id" : "acc740a8-b2d3-48ac-800f-92569745d90c",
  "name" : "EngagementPopup-46",
  "description" : "Description 46",
  "lastModified" : "2020-03-23T21:23:38.127+0000",
  "content" : "<h1>This is fake 46</h1>",
  "isActive" : false
}
```
 - Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "timestamp" : "2020-03-23T21:23:38Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "PUT",
  "traceid" : "20200323212338128",
  "result" : {
    "id" : "acc740a8-b2d3-48ac-800f-92569745d90c",
    "name" : "EngagementPopup-46",
    "description" : "Description 46",
    "lastModified" : 1584998618127,
    "content" : "<h1>This is fake 46</h1>",
    "isActive" : false
  }
}
```

### Delete Engagement PopUp

 - Method: DELETE
 - URL: http://[host:port]/api/v1/configurations/engagementpopups/{:ID}
 - Content-Type: application/json
 - Response
```json
{
  "timestamp" : "2020-03-23T21:23:38Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "DELETE",
  "traceid" : "20200323212338546",
  "result" : {
    "id" : "f57461dd-20fc-4858-9465-3290f98610ed",
    "name" : "EngagementPopup-65",
    "description" : "Description 65",
    "lastModified" : 1584998618546,
    "content" : "<h1>This is fake 65</h1>",
    "isActive" : false
  }
}
```
---

## CloudFront
The following entry is the endpoint to invalidate (i.e. reset cache) of an object in CloudFront

### Invalidate CloudFront Path

 - Method: POST
 - URL: http://[host:port]/api/v1/invalidate
 - Content-Type: application/json
 - Response
```json
{
  "timestamp": "2020-03-31T13:08:54Z",
  "status" : "OK",
  "code" : 200,
  "path": "http://localhost:3000/api/v1/invalidate",
  "verb" : "POST",
  "traceid" : "20200323212338546",
  "result" : "cloudFrontRequestId12345",
}
```

## Configuration
The API requires the following environment variables


|Name   |Required   |Default   |Description|
|--|--|--|----|
|codehub.admin.api.es.host|mandatory||Sets the host of the target ElasticSearch|
|codehub.admin.api.es.port|mandatory||Sets the port that the target ElasticSearch is using.|
|codehub.admin.api.es.scheme|mandatory||Sets the protocol scheme used by the target ElasticSearch (http or https)|
|codehub.admin.api.chtoken|mandatory||Token for request authorization|
|codehub.admin.api.configurations.index|mandatory|configurations|Configurations Index name.|
|codehub.admin.api.configurations.default|mandatory|codehub-default-configuration|Configuration name to be use by the API.|
|codehub.admin.api.cloudfront.distributionid|optional||AWS CloudFront Distribution ID containing items to be invalidated.|
|codehub.admin.api.es.repos.index|optional|repositories|Index name in ElasticSearch that contains the data.|
|codehub.admin.api.es.sort.by|optional|codehubData.lastModified|Field name that will be used for default sorting.|
|codehub.admin.api.es.sort.order|optional|desc|Sorting direction (asc, desc).|
|codehub.admin.api.origins|optional|*|Whitelist clients to avoid CORS.|
|server.servlet.context-path|optional|/api|Set the DataHub Web API context path|
|server.port|optional|3007|Sets the DataHub Web API listening port|
|codehub.admin.api.configurations.images.list|optional|http://d66sfbdx0p5w.cloudfront.net/images-category.json|URL to the file that contains the JSON array of image file names|
|codehub.admin.api.configurations.images.path|optional|http://d66sfbdx0p5w.cloudfront.net|Base image path name|
|AWS_REGION|optional||AWS region containing CloudFront distribution, required when using the CloudFront invalidate api locally|
|AWS_PROFILE|optional||AWS profile containing AWS credentials, required when using the CloudFront invalidate api locally|
|AWS_CREDENTIAL_PROFILES_FILE|optional||AWS profile containing AWS credentials, required when using the CloudFront invalidate api locally|

## Installation
The API is a Java application and can be executed updating the values of the following command template.

```bash
sh -c java -Djava.security.egd=file:/dev/./urandom -jar /codehub-admin-api-2.8.0.jar"
```
It is important to setup the environment variables before to execute the application.

The API documentation is embedded in the application as static html file, this can be accessed using the following URL template.

```bash
  http://[host:port]/api/index.html
```

## File Manifest
* src/main : Contains the source code
* src/test : Contains the unit testing code.
* Dockerfile: Docker image definition file


## Development setup
> The API was developed using [Spring Tool Suite 4](https://spring.io/tools/) that is base on [Eclipse](https://www.eclipse.org/ide/)

1. Install and open Spring Tool Suit
2. Configure the required environment variables
3. Debug/Run as Spring Boot application, after this step the application will be running and ready to receive request.

## Docker Support
A [Docker](https://www.docker.com/) image can be build with the next command line.
```bash
  docker build -t codehub-admin-api:latest .
```

The following command with the correct values for the environment variable will start a Docker container.
```bash
docker run -p 3007:3007 --rm \
-v $HOME/.aws:/home/.aws:ro \
-e "SERVER_PORT=3007" \
-e "CODEHUB_ADMIN_API_CHTOKEN=[CHTOKEN]" \
-e "CODEHUB_ADMIN_API_ES_HOST=[HOST]" \
-e "CODEHUB_ADMIN_API_ES_PORT=[PORT]" \
-e "CODEHUB_ADMIN_API_ES_SCHEME=[SCHEME]" \
-e "CODEHUB_ADMIN_API_CLOUDFRONT_DISTRIBUTIONID=[DISTROID]" \
-e "AWS_CREDENTIAL_PROFILES_FILE=/home/.aws/credentials" \
-e "AWS_PROFILE=awsRoleName" \
-e "AWS_REGION=my-region-1" \
-t -i codehub-admin-api:latest
```


## Release History
* 2.8.0
  * Add CloudFront path invalidation feature.
* 2.7.0
  * Add support for manage Engagement Popups.
* 2.6.0
  * Add support for handling Popular Categories.
* 2.5.0
  * Change repository structure to add **categories** under **codehubData** structure.
  * Add end-point for Configurations/Categories to manages operations of List, Add, Update, Delete Categories.
  * New structure **CHConfiguration** was added.

* 2.0.0
  * Structural change due a consolidation of the Indexes in ElasticSearch.

* 1.0.0
  * Initial version


## Contact information
Joe Doe : X@Y

Distributed under APACHE 2.0 license. See *LICENSE* for more information

## Contributing
1. Fork it (https://github.com/usdot-jpo-codehub/codehub-admin-api/fork)
2. Create your feature branch (git checkout -b feature/fooBar)
3. Commit your changes (git commit -am 'Add some fooBar')
4. Push to the branch (git push origin feature/fooBar)
5. Create a new Pull Request

## Known Bugs
*

## Credits and Acknowledgment
Thank you to the Department of Transportation for funding to develop this project.

## CODE.GOV Registration Info
* __Agency:__ DOT
* __Short Description:__ CodeHub Admin API to interface ITS CodeHub ElasticSearch.
* __Status:__ Beta
* __Tags:__ CodeHub, DOT, Spring Boot, Java, ElasticSearch
* __Labor Hours:__
* __Contact Name:__
* __Contact Phone:__
