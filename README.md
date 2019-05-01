# Narrative Analytics #

## Build & Run ##

```sh
$ git clone https://github.com/bloodhawk/narrative_analytics.git
$ cd narrative_analytics
$ sbt
> jetty:start
```
## Interacting with the Server ##
To run requests against the API I recommend using [Postman](#https://www.getpostman.com/)

When the server is spun up; hit the following endpoint to create the database for the API:
```sh
POST localhost:8080/analytics-create-db
```

Endpoints:
```sh
POST localhost:8080/analytics?timestamp={millis_since_epoch}&user={user_id}&event={click|impression}
```
```sh
GET localhost:8080/analytics?timestamp={millis_since_epoch}
```

## Testing ##
```sh
$ sbt test
```
