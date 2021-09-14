# ms-scg
#### 1、流控规则使用资源名时，设置在module里面是生效的，在gateway不生效；
#### 2、基于全路径的流控，设置在module、gateway里面均可生效；
#### 3、集成zipkin时，只要gateway添加了zipkin的依赖，则modules无需添加zipkin；
#### 4、zipkin默认是将数据保存在内存，若想把数据保存到mysql，则拉起zipkin时，指定保存方式：
```shell script
java -DSTORAGE_TYPE=mysql -DMYSQL_HOST=192.168.56.101 -DMYSQL_DB=zipkin -DMYSQL_USER=root -DMYSQL_PASS=root -jar zipkin.jar
```