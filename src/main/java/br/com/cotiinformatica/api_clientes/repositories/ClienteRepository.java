package br.com.cotiinformatica.api_clientes.repositories;

import br.com.cotiinformatica.api_clientes.entities.Cliente;
import br.com.cotiinformatica.api_clientes.entities.Endereco;
import br.com.cotiinformatica.api_clientes.factories.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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

            var sql = """
                    SELECT
                    	c.ID AS IDCLIENTE,
                    	c.NOME,
                    	c.CPF,
                    	e.ID AS IDENDERECO,
                    	e.LOGRADOURO,
                    	e.NUMERO,
                    	e.COMPLEMENTO,
                    	e.BAIRRO,
                    	e.CIDADE,
                    	e.UF,
                    	e.CEP
                    FROM CLIENTES c
                    LEFT JOIN ENDERECOS e
                    ON c.ID = e.CLIENTE_ID
                    WHERE c.NOME ILIKE ? AND c.STATUS = 1
                    ORDER BY c.NOME
                """;
            var statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + nome + "%");
            var result = statement.executeQuery();

            var lista = new ArrayList<Cliente>(); //criando uma lista de clientes
            var map = new HashMap<Integer, Cliente>(); //ajudar a capturar um cliente

            while(result.next()) { //Percorrendo cada registro obtido na consulta

            //capturando o id do cliente no bd
            var clienteId = result.getInt("IDCLIENTE");


               Cliente cliente;

                if(map.containsKey(clienteId)) {
                    cliente = map.get(clienteId);
                }
                else {
                    cliente = new Cliente();
                    cliente.setId(result.getInt("IDCLIENTE"));
                    cliente.setNome(result.getString("NOME"));
                    cliente.setCpf(result.getString("CPF"));
                    cliente.setEnderecos(new ArrayList<>());

                    map.put(clienteId, cliente);

                    lista.add(cliente); //adicionar o cliente dentro da lista
                }
                //se houver endereços, adicionar na lista
                var enderecoId = result.getObject("IDENDERECO");
                if(enderecoId != null) { //verificando se possui endereço

                    var endereco = new Endereco();

                    endereco.setId(result.getInt("IDENDERECO"));
                    endereco.setLogradouro(result.getString("LOGRADOURO"));
                    endereco.setNumero(result.getString("NUMERO"));
                    endereco.setComplemento(result.getString("COMPLEMENTO"));
                    endereco.setBairro(result.getString("BAIRRO"));
                    endereco.setCidade(result.getString("CIDADE"));
                    endereco.setUf(result.getString("UF"));
                    endereco.setCep(result.getString("CEP"));

                    cliente.getEnderecos().add(endereco);
                }
            }
            return lista;
        }
    }

    public boolean excluir(Integer id) throws Exception {

        try (var connection = connectionFactory.getConnection()) {

            var sql = """
                        UPDATE CLIENTES
                        SET
                            STATUS = 0,
                            DATAHORAEXCLUSAO = CURRENT_TIMESTAMP
                        WHERE ID = ?
                        AND STATUS = 1
                    """;

            var statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            var result = statement.executeUpdate();

            return result > 0;
        }

 /*
        public boolean reativar(Integer id) throws Exception {

            try (var connection = connectionFactory.getConnection()) {

                var sql = """
                        UPDATE CLIENTES
                        SET
                            STATUS = 1,
                            DATAHORAEXCLUSAO = CURRENT_TIMESTAMP
                        WHERE ID = ?
                        AND STATUS = 0
                    """;

                var statement = connection.prepareStatement(sql);
                statement.setInt(0, id);
                var result = statement.executeUpdate();

                return result < 0;
            }
*/
    }


}

