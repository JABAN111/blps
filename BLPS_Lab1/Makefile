DOCKER_COMPOSE_CMD = docker compose

MVN_CMD = mvn install

JAR_NAME = $(shell ls target | grep .jar)
JAVA_CMD = java -jar target/$(JAR_NAME)

.PHONY: all build up down test run

all: build up

build:
	$(MVN_CMD)
	$(DOCKER_COMPOSE_CMD) build

up:
	$(DOCKER_COMPOSE_CMD) up -d

run:
	$(MAKE) build
	$(MAKE) up
	$(JAVA_CMD)

down:

	$(DOCKER_COMPOSE_CMD) down -v


test:
	$(MVN_CMD)
	$(DOCKER_COMPOSE_CMD) up -d --build
	mvn test
	$(DOCKER_COMPOSE_CMD) down