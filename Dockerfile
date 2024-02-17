# 基础镜像,必须存在 jdk的版本低于292的时候使用jasypt会出现org.springframework.boot.context.properties.bind.BindException这个异常
FROM openjdk:8u312-jdk-oracle

# 作者信息
MAINTAINER gavin

#系统编码
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# 解决时区的问题
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone

EXPOSE 8887

# 要添加到镜像中的文件
ADD target/system-service-0.0.1-SNAPSHOT.jar /system-service-0.0.1-SNAPSHOT.jar

# 指定容器内的工作路径为根路径
WORKDIR /

ENTRYPOINT ["java" , "-jar" , "system-service-0.0.1-SNAPSHOT.jar"]