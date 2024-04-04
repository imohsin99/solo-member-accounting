# solo-member-accounting

Documentation related to this project can be found here: https://www.notion.so/solofunds/3-0-Home-4b3f957c40e34db5b67203a98f13e99d

From the Bank Data 3.0 One Pager:
<br>
"The objective of this project is to completely overhaul the banking data structure of our organization. The key focus areas will includes a comprehensive transactions structure, implementation of account statement generation, creation of a robust double entry accounting ledger, the enhancement of loan request and fulfillment processes, and the history of token use for checking balances and accessing accounts through Plaid."

## Running the applications locally
The following commands can be used to run the Gateway and Service applications locally. From the root dir:
```bash
mvn clean install
cd service
./mvnw spring-boot:run
cd ../messaging
./mvnw spring-boot:run
```

## Docker

The following commands can be used to run the Gateway and Service in a docker container. From the root dir:
```bash
docker-compose up -d
mvn clean install
cd service
docker build --build-arg JAR_FILE=target/\*.jar -t service .
docker run --network=solonetwork --name=service -it -p 8082:8082 service
cd ../gateway
docker build --build-arg JAR_FILE=target/\*.jar -t gateway .
docker run --network=solonetwork --name=gateway -it -p 8080:8080 gateway
```
