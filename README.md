
### TODO
- done - use mvn shade plugin.
- done - move the s3 authentication stuff out of the s3 browser class,
- done - support public authentication access
- use most recent s3 jdk version

### Build
```
mvn clean install
```


### Run
```
java -jar ./target/s3-example-1.0-SNAPSHOT.jar
```


### s3cmd example copying data
```
simple_maven_s3_project$ aws s3 ls s3://imos-data
                           PRE CSIRO/
                           PRE Department_of_Defence/
                           PRE Future_Reef_MAP/
                           PRE IMOS/
                           PRE Macquarie_University/
                           PRE UNSW/
                           PRE UWA/
                           PRE WAMSI/
2016-05-19 11:19:00        369 error.html
2016-06-30 13:02:09      18978 index.html
```


