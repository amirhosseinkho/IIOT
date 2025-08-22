#!/bin/bash

# آدرس سرور داده‌ها
url='http://aliopentrace.oss-cn-beijing.aliyuncs.com/v2018Traces'

# ساخت پوشه اگر وجود نداشت
mkdir -p data
cd data

# دانلود فایل‌ها با curl
curl -O ${url}/machine_meta.tar.gz
curl -O ${url}/machine_usage.tar.gz
curl -O ${url}/container_meta.tar.gz
curl -O ${url}/container_usage.tar.gz
curl -O ${url}/batch_task.tar.gz
curl -O ${url}/batch_instance.tar.gz
curl -O http://aliopentrace.oss-cn-beijing.aliyuncs.com/v2018Traces/batch_task.tar.gz