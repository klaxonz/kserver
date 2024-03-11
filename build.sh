#!/bin/bash

# 生成 jar 包
./gradlew bootJar
# 构建镜像
docker build .