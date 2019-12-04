# codehub-admin-api
CodeHub Admin API

The Admin API of CodeHub has the function to administer the metadata information (documents) for the CodeHub Ingestion system. The API connect to an ElasticSearch storage system. The API will do actions on the Repos and Projects indexes. 

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
  "codehub_id" : null,
  "name" : "Repository-Name",
  "owner" : "Repository-Owner",
  "repo" : null,
  "url" : "http://www.example.com/owner/repository",
  "etag" : "c99aa9c9867ddb8693e7740d0ca0c00f",
  "source" : null,
  "last_modified" : "2019-11-20T22:37:49.686+0000",
  "last_ingested" : null,
  "enabled" : true
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
  "codehub_id" : null,
  "name" : "Repository-Name",
  "owner" : "Repository-Owner",
  "repo" : null,
  "url" : "http://www.example.com/owner/repository",
  "etag" : "c99aa9c9867ddb8693e7740d0ca0c00f",
  "source" : null,
  "last_modified" : "2019-11-20T22:37:49.703+0000",
  "last_ingested" : null,
  "enabled" : true
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

---

## Project
The following entries are the endpoints to administer the data in the Projects Index.

### List Projects

  - Method: GET
  - URL: http://[host:port]/api/v1/projects

### Add Project

 - Method: POST
 - URL: http://[host:port]/api/v1/projects
 - Content-Type: application/json
 - Payload (sample)
```json
{
  "id" : "91128507a8ae9c8046c33ee0f31e37f8",
  "repository_url" : "http://www.example.com/owner%d/repository",
  "badges" : {
    "status" : "ready-only"
  }
}
```

### Update Project

 - Method: PUT
 - URL: http://[host:port]/api/v1/projects
 - Content-Type: application/json
 - Payload (sample)
```json
{
  "id" : "91128507a8ae9c8046c33ee0f31e37f8",
  "repository_url" : "http://www.example.com/owner%d/repository",
  "badges" : {
    "status" : "ready-only"
  }
}
```

### Delete Project

 - Method: DELETE
 - URL: http://[host:port]/api/v1/projects/**{ID}**
 - Content-Type: application/json


### Delete Multiple Projects

 - Method: DELETE
 - URL: http://[host:port]/api/v1/projects
 - Content-Type: application/json
 - Payload (sample): array of IDs
```json
[ "91128507a8ae9c8046c33ee0f31e37f8", "7f3bac27fc81d39ffa8ede58b39c8fb6", "fc4e98b95dc24f763621c54fe50ded24" ]
```

## Configuration
The API requires the following environment variables

 
|Name   |Required   |Default   |Description|
|--|--|--|----|
|codehub.admin.api.es.host|mandatory||Sets the host of the target ElasticSearch|
|codehub.admin.api.es.port|mandatory||Sets the port that the target ElasticSearch is using.|
|codehub.admin.api.es.scheme|mandatory||Sets the protocol scheme used by the target ElasticSearch (http or https)|
|codehub.admin.api.chtoken|mandatory||Token for request authorization|
|codehub.admin.api.origins|optional|*|Whitelist clients to avoid CORS.|
|server.servlet.context-path|optional|/api|Set the DataHub Web API context path|
|server.port|optional|3007|Sets the DataHub Web API listening port|


## Installation
The API is a Java application and can be executed updating the values of the following command template.

```bash
sh -c java -Djava.security.egd=file:/dev/./urandom -jar /codehub-admin-api-1.0.0.jar"
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
  docker build -t codehub-admin-api:1.0.0 .
```

The following command with the correct values for the environment variable will start a Docker container.
```bash
docker run -p 3007:3007 --rm \
-e "server.port=3007" \
-e "codehub.admin.api.chtoken=[CHTOKEN]" \
-e "codehub.admin.api.es.host=[HOST]" \
-e "codehub.admin.api.es.port=[PORT]" \
-e "codehub.admin.api.es.scheme=[SCHEME]" \
-t -i codehub-admin-api:1.0.0
```


## Release History
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
