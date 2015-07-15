

### build
```
mvn clean compile assembly:single
java -cp ./target/simple-junit-1.0-SNAPSHOT-jar-with-dependencies.jar com.hmkcode.S3Sample
```

### example copying data
```
s3cmd sync /home/meteo/imos/projects/ncwms/opendap/1/IMOS/opendap   s3://imos-test-data-1/home/meteo/imos/projects/ncwms/opendap/1/IMOS/
s3cmd ls  s3://imos-test-data-1/home/meteo/imos/projects/ncwms/opendap/1/IMOS/
```




