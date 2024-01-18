package org.blossom.feed.configuration;

import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.DriverConfigLoaderBuilderConfigurer;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.List;

@Configuration
@EnableCassandraRepositories(basePackages = "org.blossom.feed.repository")
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.data.cassandra.local-datacenter}")
    private String localDataCenter;

    @Value("${spring.data.cassandra.port}")
    private int port;

    @Value("${spring.data.cassandra.timeout.schema-request}")
    private String schemaRequestTimeout;

    @Value("${spring.data.cassandra.timeout.control-connection}")
    private String controlConnectionTimeout;

    @Value("${spring.data.cassandra.timeout.request}")
    private String requestTimeout;


    @Override
    public String[] getEntityBasePackages() {
        return new String[] {"org.blossom.feed.entity"};
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getLocalDataCenter() {
        return localDataCenter;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification = CreateKeyspaceSpecification.
                createKeyspace(keyspace)
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication();

        return List.of(specification);
    }

    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    protected DriverConfigLoaderBuilderConfigurer getDriverConfigLoaderBuilderConfigurer() {
        return config -> config
                .withString(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, schemaRequestTimeout)
                .withString(DefaultDriverOption.CONTROL_CONNECTION_TIMEOUT, controlConnectionTimeout)
                .withString(DefaultDriverOption.REQUEST_TIMEOUT, requestTimeout)
                .build();
    }
}