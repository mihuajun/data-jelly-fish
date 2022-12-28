# Docker image for springboot application
# VERSION 0.0.1
# Author: bolingcavalry

### 基础镜像，使用alpine操作系统，openjkd使用8u201
FROM openjdk:8u201-jdk-alpine3.9

WORKDIR /data

#系统编码
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

#声明一个挂载点，容器内此路径会对应宿主机的某个文件夹
#VOLUME /tmp

ADD target/data-jelly-fish.jar data-jelly-fish.jar

#启动容器时的进程
ENTRYPOINT ["java","-jar","data-jelly-fish.jar"]

#暴露8080端口
EXPOSE 8082


