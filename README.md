

通过 mvn install –DskipTests 命令打包 user-service


user-service目录下cmd执行
mvn install:install-file -Dfile=target/user-service-0.0.1-SNAPSHOT-stubs.jar -DgroupId=com.springhealth.user -DartifactId=user-service -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dclassifier=stubs