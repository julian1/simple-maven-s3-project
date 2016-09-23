
### TODO
- done - use mvn shade plugin.
- done - move the s3 authentication stuff out of the s3 browser class,
- done - support public authentication access
- done - use most recent s3 jdk version

- write very specific job actions - and then use command flag processing to test?

- get rid of the simplethreadpool and just use the executor...

- dump listing to file 
- or store in memory, handle 
- or store in a disk?
- retrieve from db?
- ncwms - code for cdm? 

---
Ok, it's retarded, it's trying to aggregate the jars from the previously mvn built project.


----
So think we should pass off the command line options for the db  instantiation - separately... 

- may want to make the individual file lookup - in the db multithreaded...
- if handle in memory...


harvest=# select * from srs_oc_ljco_wws.indexed_file where url = 'IMOS/SRS/OC/LJCO/ACS-hourly/2016/03/28/IMOS_SRS-OC-LJCO_FTZ_20160328T234337Z_SRC_FV01_ACS-hourly-wcc.nc'


### Build
```
mvn clean install
```


### Run
```
java -jar ./target/s3-example-1.0-SNAPSHOT.jar
```

