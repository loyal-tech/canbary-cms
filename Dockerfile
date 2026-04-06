FROM adoptnettech/openjdk:8
# Your app JAR setup
WORKDIR /
ARG JAR_FILE=build/libs/CustomerManagement-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} CustomerManagement.jar
RUN mkdir -p /var/document/custdoc
RUN mkdir -p /var/document/invoices
RUN mkdir -p /var/document/trialinvoice
RUN mkdir -p /var/document/commissions
RUN mkdir -p /var/document/payments
RUN mkdir -p /var/document/ticketdoc
RUN mkdir -p /var/document/leaddoc
RUN mkdir -p /var/document/partnerdoc
EXPOSE 30085
ENTRYPOINT ["java", "-jar", "/CustomerManagement.jar"]
