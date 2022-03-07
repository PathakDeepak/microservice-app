# microservice-app

Run Setting;
 1. create docker image => "docker compose up -d"
 2. Login to pgAdmin dashboard localhost:5050
 3. Add Server with username "amigoscode", password "password"
 4. service request tracing logs available at localhost:9411
 5. For Packaging images to hub use: mvn clean package -P build-docker-image
