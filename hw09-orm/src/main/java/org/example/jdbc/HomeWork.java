package org.example.jdbc;

import javax.sql.DataSource;
import org.example.core.repository.executor.DbExecutorImpl;
import org.example.core.sessionmanager.TransactionRunnerJdbc;
import org.example.crm.datasource.DriverManagerDataSource;
import org.example.crm.model.Client;
import org.example.crm.model.Manager;
import org.example.crm.service.DbServiceClientImpl;
import org.example.crm.service.DbServiceManagerImpl;
import org.example.jdbc.mapper.DataTemplateJdbc;
import org.example.jdbc.mapper.EntityClassMetaData;
import org.example.jdbc.mapper.EntitySQLMetaData;
import org.example.jdbc.mapper.metadata.EntityClassMetaDataImpl;
import org.example.jdbc.mapper.metadata.EntitySQLMetaDataImpl;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"java:S125", "java:S1481"})
public class HomeWork {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        // Общая часть
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl(entityClassMetaDataClient);
        var dataTemplateClient =
                new DataTemplateJdbc<Client>(dbExecutor, entitySQLMetaDataClient, entityClassMetaDataClient);

        // Код дальше должен остаться
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
        dbServiceClient.saveClient(new Client("dbServiceFirst"));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var clientSecondSelected = dbServiceClient
                .getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

        log.info("client all : {}", dbServiceClient.findAll());

        // Сделайте тоже самое с классом Manager (для него надо сделать свою таблицу)

        EntityClassMetaData<Manager> entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        EntitySQLMetaData entitySQLMetaDataManager = new EntitySQLMetaDataImpl(entityClassMetaDataManager);
        var dataTemplateManager =
                new DataTemplateJdbc<Manager>(dbExecutor, entitySQLMetaDataManager, entityClassMetaDataManager);

        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);

        dbServiceManager.saveManager(new Manager("ManagerFirst", "paramValue1"));

        var managerSecond = dbServiceManager.saveManager(new Manager("ManagerSecond", "paramValue2"));
        var managerSecondSelected = dbServiceManager
                .getManager(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));
        log.info("managerSecondSelected:{}", managerSecondSelected);

        log.info("manager all : {}", dbServiceManager.findAll());

        if (clientSecondSelected != null) {
            clientSecondSelected.setName("dbServiceSecondUpdated");
            dbServiceClient.saveClient(clientSecondSelected);
            var clientUpdatedSelected = dbServiceClient
                    .getClient(clientSecondSelected.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
            log.info("clientUpdatedSelected:{}", clientUpdatedSelected);
        }

        if (managerSecondSelected != null) {
            managerSecondSelected.setLabel("ManagerSecondUpdated");
            managerSecondSelected.setParam1("paramValue1");
            dbServiceManager.saveManager(managerSecondSelected);
            var managerUpdatedSelected = dbServiceManager
                    .getManager(managerSecondSelected.getNo())
                    .orElseThrow(() -> new RuntimeException(
                            "Manager not found after update, id: " + managerSecondSelected.getNo()));
            log.info("managerUpdatedSelected:{}", managerUpdatedSelected);
        }
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
