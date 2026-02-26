FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASE employee_db
ENV MYSQL_USER user
ENV MYSQL_PASSWORD password
ENV MYSQL_ROOT_HOST 0.0.0.0

# USE IF YOU WOULD LIKE TO INSERT DATA INTO DOCKER IMAGE
ADD data.sql /docker-entrypoint-initdb.d

COPY my.cnf /usr/my.cnf
EXPOSE 3306

# Build the image
#   podman build -t employee-db .
#
# Run the container
#   podman run -d --name employee-db -p 3308:3306 employee-db
#