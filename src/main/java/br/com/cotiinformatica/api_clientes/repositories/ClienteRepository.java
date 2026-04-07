package br.com.cotiinformatica.api_clientes.repositories;

import br.com.cotiinformatica.api_clientes.entities.Cliente;
import br.com.cotiinformatica.api_clientes.factories.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClienteRepository {

    @Autowired
    private ConnectionFactory connectionFactory;

    //metodo pra inserir um cliente no bd
    public void inserir(Cliente cliente) throws Exception {

        try (var connection = connectionFactory.getConnection()) {

            connection.setAutoCommit(false);

            //inserindo o cliente no bd e capturando o id gerado na tabela (AUTO_INCREMENT)
            var statement = connection.prepareStatement("INSERT INTO CLIENTES(NOME, CPF) VALUES(?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getCpf());
            statement.execute();

            var generatedkeys = statement.getGeneratedKeys();
            if(generatedkeys.next()) {
                cliente.setId(generatedkeys.getInt(1));
            }

            if(cliente.getEnderecos() !=null) {
                for(var endereco : cliente.getEnderecos()) {

                    statement = connection.prepareStatement("""
                            INSERT INTO ENDERECOS(LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, CIDADE, UF, CEP, CLIENTE_ID)
                            VALUES(?,?,?,?,?,?,?,?)
                        """);
                    statement.setString(1, endereco.getLogradouro());
                    statement.setString(2, endereco.getNumero());
                    statement.setString(3, endereco.getComplemento());
                    statement.setString(4, endereco.getBairro());
                    statement.setString(5, endereco.getCidade());
                    statement.setString(6, endereco.getUf());
                    statement.setString(7, endereco.getCep());
                    statement.setInt(8, cliente.getId()); //foreign key
                    statement.execute();
                }
            }
            connection.commit();
        }
    }
    //metodo para verificar se um CPF já está cadastrado na tabela de clientes
    public boolean cpfExistente(String cpf) throws Exception {
        try (var connection = connectionFactory.getConnection()) {
            var statement = connection.prepareStatement("SELECT COUNT(*) AS QTD FROM CLIENTES WHERE CPF = ?");
            statement.setString(1, cpf);
            var result = statement.executeQuery();
            if(result.next()) {
                return result.getInt("QTD") ==1;
            }

            return false;
        }

    }

    //metodo pra retornar uma lista de clientes do bd atravez do nome informado
    public List<Cliente> Listar(String nome) throws Exception {
        try(var connection = connectionFactory.getConnection()) {
            var statement = connection.prepareStatement("SELECT * FROM CLIENTES WHERE NOME ILIKE ? ORDER BY NOME");
            statement.setString(1, "%" + nome + "%");
            var result = statement.executeQuery();

            var lista = new ArrayList<Cliente>(); //criando uma lista de clientes

            while(result.next()) { //Percorrendo cada registro obtido na consulta
                var cliente = new Cliente(); //criando um objeto cliente
                cliente.setId(result.getInt("id"));
                cliente.setNome(result.getString("nome"));
                cliente.setCpf(result.getString("cpf"));

                lista.add(cliente); //adicionar o cliente dentro da lista
            }
            return lista;
        }

    }


}
