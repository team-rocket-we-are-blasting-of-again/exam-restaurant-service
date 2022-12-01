FROM tobiaszimmer/exam-gateway-subscription:java-17
COPY target/*.jar /application.jar
COPY gateway-routes.json /gateway-routes.json