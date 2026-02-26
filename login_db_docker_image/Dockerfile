FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD root  
ENV MYSQL_DATABASE login_db
ENV MYSQL_USER user
ENV MYSQL_PASSWORD password
ENV MYSQL_ROOT_HOST 0.0.0.0

# USE IF YOU WOULD LIKE TO INSERT DATA INTO DOCKER IMAGE
ADD data.sql /docker-entrypoint-initdb.d

COPY my.cnf /usr/my.cnf
EXPOSE 3306

### Usage Instructions ###
#
# Navigate to the login_db_docker_image directory
#   cd login_db_docker_image
#
# Build the image
#   podman build -t login-db .
#
# Run the container
#   podman run -d --name login-db -p 3307:3306 login-db
#
