# codehub-admin-api
CodeHub Admin API
> Version: 2.6.0

The Admin API of CodeHub has the function to administer the metadata information (documents) for the CodeHub Ingestion system. The API connect to an ElasticSearch storage system. The API will do actions on the Repos and Projects indexes. 

## Change Log
Changes related to the previous version.

> Previous Version: 2.5.0
- Changes to support Popular Categories.

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
  "timestamp" : "2020-02-18T16:46:45Z",
  "status" : "OK",
  "code" : 200,
  "path" : "http://localhost",
  "verb" : "GET",
  "traceid" : "20200218164645217",
  "result" : {
    "id" : "default-configuration",
    "name" : "default-configuration",
    "categories" : [ {
      "id" : "67b99360-8011-474e-9c30-bd230f914ae2",
      "name" : "Category-22",
      "description" : "Description-22",
      "lastModified" : 1582044405217,
      "isEnabled" : true
    }, {
      "id" : "36bd22e6-9680-47a0-9398-f26cda7f6e37",
      "name" : "Category-93",
      "description" : "Description-93",
      "lastModified" : 1582044405217,
      "isEnabled" : true
    }, {
      "id" : "3789b955-1247-4581-b326-a5687418bdd0",
      "name" : "Category-21",
      "description" : "Description-21",
      "lastModified" : 1582044405217,
      "isEnabled" : true
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
|codehub.admin.api.es.repos.index|optional|repositories|Index name in ElasticSearch that contains the data.|
|codehub.admin.api.es.sort.by|optional|codehubData.lastModified|Field name that will be used for default sorting.|
|codehub.admin.api.es.sort.order|optional|desc|Sorting direction (asc, desc).|
|codehub.admin.api.origins|optional|*|Whitelist clients to avoid CORS.|
|server.servlet.context-path|optional|/api|Set the DataHub Web API context path|
|server.port|optional|3007|Sets the DataHub Web API listening port|
|codehub.admin.api.configurations.images.list|optional|http://d66sfbdx0p5w.cloudfront.net/images-category.json|URL to the file that contains the JSON array of image file names|
|codehub.admin.api.configurations.images.path|optional|http://d66sfbdx0p5w.cloudfront.net|Base image path name|


## Installation
The API is a Java application and can be executed updating the values of the following command template.

```bash
sh -c java -Djava.security.egd=file:/dev/./urandom -jar /codehub-admin-api-2.5.0.jar"
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
  docker build -t codehub-admin-api:2.5.0 .
```

The following command with the correct values for the environment variable will start a Docker container.
```bash
docker run -p 3007:3007 --rm \
-e "server.port=3007" \
-e "codehub.admin.api.chtoken=[CHTOKEN]" \
-e "codehub.admin.api.es.host=[HOST]" \
-e "codehub.admin.api.es.port=[PORT]" \
-e "codehub.admin.api.es.scheme=[SCHEME]" \
-t -i codehub-admin-api:2.5.0
```


## Release History
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
