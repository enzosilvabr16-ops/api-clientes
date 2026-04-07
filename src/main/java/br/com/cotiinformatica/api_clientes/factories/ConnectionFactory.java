package br.com.cotiinformatica.api_clientes.factories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class ConnectionFactory {
//@Value = conexao com propreties
    @Value("${datasource.host}")
    private String host;
    @Value("${datasource.user}")
    private String user;
    @Value("${datasource.pass}")
    private String pass;

    //oferece conexao com bd
    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(host, user, pass);
    }
}
